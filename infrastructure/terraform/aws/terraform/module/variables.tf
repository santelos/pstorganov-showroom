variable "project_name" {
  type        = string
  description = "Name of the Terraform project"
}

variable "terraform_bucket_arn" {
  type        = string
  description = "ARN of the policy to list bucket with terraform states"
}

variable "trusted_user_arn" {
  type        = string
  description = "ARN of the truested user to assume this role"
}

variable "extra_policy" {
  type        = string
  description = "Extra policy that will be inlined to role"
}
