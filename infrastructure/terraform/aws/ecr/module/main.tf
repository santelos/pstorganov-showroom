terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.4.0"
    }
  }
}

locals {
  trusted_role_arns = [
    "arn:aws:iam::162173573602:role/ecr/*"
  ]
}
