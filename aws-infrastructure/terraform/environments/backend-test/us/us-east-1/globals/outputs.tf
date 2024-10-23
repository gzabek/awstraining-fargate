output "hub" {
  value = "US"
}

output "account_id" {
  value = "063814393886"
}

output "availability_zones" {
  value = [
    "us-east-1a",
    "us-east-1b",
    "us-east-1c"
  ]
}

output "backend_service_deployment_desired_task_count" {
  description = "Desired Fargate tasks in cluster"
  value = 3
}

output "endpoints" {
  value = {
    "ssm": "com.amazonaws.us-east-1",
    "logs": "com.amazonaws.us-east-1",
    "ecr.api": "com.amazonaws.us-east-1",
    "ecr.dkr": "com.amazonaws.us-east-1",
    "sts": "com.amazonaws.us-east-1",
    "secretsmanager": "com.amazonaws.us-east-1",
    "sts": "com.amazonaws.us-east-1",
    "ecs": "com.amazonaws.us-east-1"
  }
}