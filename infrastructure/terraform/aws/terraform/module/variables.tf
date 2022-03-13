variable "project_name" {
  type        = string
  description = "Name of the Terraform project"
}

variable "terraform_bucket_arn" {
  type        = string
  description = "ARN of the policy to list bucket with terraform states"
}

variable "oidc_provider_arn" {
  type        = string
  description = "ARN of the OIDC provider in AWS IAM"
}

variable "github_ref" {
  type        = string
  description = "Ref to a github repo/branch that will assume the role"
}

variable "extra_policy" {
  type        = string
  description = "Extra policy that will be inlined to role"
}
