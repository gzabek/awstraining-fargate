#!/usr/bin/env bash

set -e

# Load properties file
source 'wrapper.properties'

# Declare an associative array
declare -A REGION_TO_HUB=(
  ["eu-central-1"]="emea"
  ["us-east-1"]="us"
  ["cn-north-1"]="cn"
)

SCRIPT=$1
TYPE=$2
PROFILE=$3
REGION=$4
# Get the corresponding hub from the associative array
HUB="${REGION_TO_HUB[$REGION]}"
ACTION=${@:5}

TF_STATE_BUCKET="tf-state-${PROFILE}-${REGION}-${UNIQUE_BUCKET_STRING}"
TF_STATE_BUCKET_EKS=$TF_STATE_BUCKET-eks

delete_tfstate_bucket() {
  aws s3api delete-objects \
      --bucket $TF_STATE_BUCKET \
      --delete "$(aws s3api list-object-versions --bucket ${TF_STATE_BUCKET} --output=json --query='{Objects: Versions[].{Key:Key,VersionId:VersionId}}')" \
      --profile $PROFILE \
      --region $REGION || true
  aws s3 rb s3://$TF_STATE_BUCKET --profile $PROFILE --region $REGION --force || true
}

delete_eks_tfstate_bucket() {
  aws s3api delete-objects \
      --bucket $TF_STATE_BUCKET_EKS \
      --delete "$(aws s3api list-object-versions --bucket ${TF_STATE_BUCKET_EKS} --output=json --query='{Objects: Versions[].{Key:Key,VersionId:VersionId}}')" \
      --profile $PROFILE \
      --region $REGION || true
  aws s3 rb s3://$TF_STATE_BUCKET_EKS --profile $PROFILE --region $REGION --force || true
}

delete_secrets_manager() {
  aws secretsmanager delete-secret \
      --secret-id backend-secretsmanager-test-eu-central-1 \
      --force-delete-without-recovery \
    	--profile $PROFILE \
    	--region $REGION || true
}

empty_ecr() {
  ECR_REPOSITORY="backend"
  aws ecr batch-delete-image \
      --repository-name $ECR_REPOSITORY \
      --profile $PROFILE \
      --region $REGION \
      --image-ids "$(aws ecr list-images --region $REGION --profile $PROFILE --repository-name $ECR_REPOSITORY --query 'imageIds[*]' --output json)" || true
}

empty_monitoring_ecr() {
  ECR_MONITORING_REPOSITORY="monitoring"
  aws ecr batch-delete-image \
      --repository-name $ECR_MONITORING_REPOSITORY \
      --profile $PROFILE \
      --region $REGION \
      --image-ids "$(aws ecr list-images --region $REGION --profile $PROFILE --repository-name $ECR_MONITORING_REPOSITORY --query 'imageIds[*]' --output json)" || true
}

delete_log_groups() {
  aws logs describe-log-groups --query 'logGroups[*].logGroupName' --output table --region $REGION --profile $PROFILE | \
  awk '{print $2}' | grep -v ^$ | while read x; do  echo "deleting $x" ; aws logs delete-log-group --log-group-name $x --region $REGION --profile $PROFILE; done || true
}

if [ "$#" -lt 5 ]; then
  echo "Not enough arguments provided."
  echo
  echo "Script should be used in following form:"
  echo
  echo "$0 SCRIPT TYPE PROFILE REGION ACTION"
  echo
  echo "example usage: "
  echo
  echo "$0 setup_new_region.sh ecs backend-test eu-central-1 plan"
  echo
  echo "or: "
  echo
  echo "$0 setup_new_region.sh eks backend-test eu-central-1 apply"
  echo "$0 setup_new_region.sh eks backend-test eu-central-1 apply -auto-approve"
  echo
  echo "Apply will ask for your confirmation after each module."
  exit 1
fi

# remove any state from previous runs (possibly on different environments)
rm common/*/*/.terraform/terraform.tfstate || true

if [ "$ACTION" = "destroy -auto-approve" ]; then
  # Destroy infrastructure
  if [ "$TYPE" = "eks" ]; then
    echo "Checking if $TF_STATE_BUCKET_EKS exists..."
    if aws s3api head-bucket --bucket $TF_STATE_BUCKET_EKS --profile $PROFILE --region $REGION 2>/dev/null; then
      echo "Removing EKS..."
      cd common/services/eks/cluster

      echo "Loading Kubernetes config from EKS"
      aws eks update-kubeconfig --name backend-eks --profile $PROFILE --region $REGION

      terraform init -backend-config "bucket=${TF_STATE_BUCKET_EKS}" -backend-config "key=eks" -backend-config "region=${REGION}" -backend-config "profile=${PROFILE}" -var profile=${PROFILE} -var region=${REGION}
      terraform destroy -var="region=$REGION" -var="profile=$PROFILE" -auto-approve
      delete_eks_tfstate_bucket
    else
      echo "Skipping destroy - everything was already destroyed!"
    fi
  else
    echo "Checking if $TF_STATE_BUCKET exists..."
    if aws s3api head-bucket --bucket $TF_STATE_BUCKET --profile $PROFILE --region $REGION 2>/dev/null; then
      echo "Removing ECS..."
      delete_secrets_manager
      empty_ecr
      # empty_monitoring_ecr
      # Monitoring (Elasticsearch, Filebeat, Kibana, Prometheus, Grafana)
#      ./$SCRIPT $PROFILE $REGION common/monitoring/ecs-monitoring-service $ACTION
#      ./$SCRIPT $PROFILE $REGION common/monitoring/ecs-monitoring-cluster $ACTION

      ./$SCRIPT $PROFILE $REGION common/services/ecs-backend-service $ACTION
      ./$SCRIPT $PROFILE $REGION common/services/ecs-backend-cluster $ACTION
      ./$SCRIPT $PROFILE $REGION common/services/ecr $ACTION
      ./$SCRIPT $PROFILE $REGION common/monitoring/sns $ACTION
      ./$SCRIPT $PROFILE $REGION common/networking/securitygroups $ACTION
      ./$SCRIPT $PROFILE $REGION common/networking/vpc $ACTION
      ./$SCRIPT $PROFILE $REGION environments/$PROFILE/$HUB/$REGION/globals $ACTION
      ./$SCRIPT $PROFILE $REGION common/general/dynamo-lock $ACTION
      ./$SCRIPT $PROFILE $REGION common/services/measurements-dynamodb $ACTION
      delete_tfstate_bucket
      delete_log_groups
    else
      echo "Skipping destroy - everything was already destroyed!"
    fi
  fi
else
  # Setup infrastructure
  if [ "$TYPE" = "eks" ]; then
    cd common/services/eks
    echo "Creating EKS..."
    echo "Checking if $TF_STATE_BUCKET_EKS exists..."
    if aws s3api head-bucket --bucket $TF_STATE_BUCKET_EKS --profile $PROFILE --region $REGION 2>/dev/null; then
      echo "Skipping EKS remote state bucket creation"
    else
      echo "Creating EKS remote state bucket"
      cd remote-state-bucket
      terraform init -var="name=$TF_STATE_BUCKET_EKS" -var="region=$REGION" -var="profile=$PROFILE"
      terraform plan -out planfile -target aws_s3_bucket.remote_state -var="name=$TF_STATE_BUCKET_EKS" -var="region=$REGION" -var="profile=$PROFILE"
      terraform apply planfile
      cd ..
    fi

    # This is only needed if we re-run the workflow (after some errors)
    echo "Loading Kubernetes config from EKS (if exists)"
    aws eks update-kubeconfig --name backend-eks --profile $PROFILE --region $REGION || true

    cd cluster
    terraform init -backend-config "bucket=${TF_STATE_BUCKET_EKS}" -backend-config "key=eks" -backend-config "region=${REGION}" -backend-config "profile=${PROFILE}" -var="region=$REGION" -var="profile=$PROFILE"
    terraform validate
    terraform plan -out planfile -target module.vpc -target module.eks -target null_resource.next -var="region=$REGION" -var="profile=$PROFILE"
    terraform apply planfile
    terraform plan -out planfile -var="region=$REGION" -var="profile=$PROFILE"
    terraform apply planfile
  else
    echo "Creating ECS..."
    if aws s3api head-bucket --bucket $TF_STATE_BUCKET --profile $PROFILE --region $REGION 2>/dev/null; then
      echo "Skipping ECS remote state bucket creation"
    else
      echo "Creating ECS remote state bucket"
      ./$SCRIPT $PROFILE $REGION common/general/create-remote-state-bucket $ACTION
    fi

    ./$SCRIPT $PROFILE $REGION common/general/dynamo-lock $ACTION
    ./$SCRIPT $PROFILE $REGION environments/$PROFILE/$HUB/$REGION/globals $ACTION
    ./$SCRIPT $PROFILE $REGION common/networking/vpc $ACTION
    ./$SCRIPT $PROFILE $REGION common/networking/securitygroups $ACTION
    ./$SCRIPT $PROFILE $REGION common/monitoring/sns $ACTION
    ./$SCRIPT $PROFILE $REGION common/services/ecr $ACTION
    ./$SCRIPT $PROFILE $REGION common/services/ecs-backend-cluster $ACTION
    ./$SCRIPT $PROFILE $REGION common/services/ecs-backend-service $ACTION

    # Monitoring (Elasticsearch, Filebeat, Kibana, Prometheus, Grafana)
#    ./$SCRIPT $PROFILE $REGION common/monitoring/ecs-monitoring-cluster $ACTION
#    ./$SCRIPT $PROFILE $REGION common/monitoring/ecs-monitoring-service $ACTION

    ./$SCRIPT $PROFILE $REGION common/services/measurements-dynamodb $ACTION
  fi
fi
