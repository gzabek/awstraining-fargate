resource "aws_sns_topic" "sns_alarm_cloudwatch_topic" {
  name = "backend_sns_alarm_cloudwatch_topic"
  kms_master_key_id = aws_kms_key.aws_backend_sns_key.arn
  tags = var.common_tags
}

resource "aws_kms_key" "aws_backend_sns_key" {
  description = "Policy used to allow SNS to write logs to CloudWatch Logs"
  policy = templatefile("../../../policies/sns-kms-policy.tpl", {
    account_id = var.account_id
  })

  tags = var.common_tags
}

resource "aws_kms_alias" "backend_kms_alias" {
  name          = "alias/aws-backend-sns-key"
  target_key_id = aws_kms_key.aws_backend_sns_key.arn
}
