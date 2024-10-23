output "hub" {
  value = "EMEA"
}

output "account_id" {
  value = "063814393886"
}

output "availability_zones" {
  value = [
    "eu-central-1a",
    "eu-central-1b",
    "eu-central-1c"
  ]
}

output "backend_service_deployment_desired_task_count" {
  description = "Desired Fargate tasks in cluster"
  value = 3
}

output "endpoints" {
  value = {
    "ssm": "com.amazonaws.eu-central-1",
    "logs": "com.amazonaws.eu-central-1",
    "ecr.api": "com.amazonaws.eu-central-1",
    "ecr.dkr": "com.amazonaws.eu-central-1",
    "sts": "com.amazonaws.eu-central-1",
    "secretsmanager": "com.amazonaws.eu-central-1",
    "sts": "com.amazonaws.eu-central-1",
    "ecs": "com.amazonaws.eu-central-1"
  }
}