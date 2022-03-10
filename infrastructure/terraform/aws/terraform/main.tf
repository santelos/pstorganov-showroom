terraform {
  backend "s3" {
    bucket       = "pstroganov-terraform-main"
    key          = "terraform.tfstate"
    region       = "eu-central-1"
    role_arn     = "arn:aws:iam::162173573602:role/terraform/terraform-terraform-backend"
    session_name = "TerraformBackend"
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.4.0"
    }
  }
}

provider "aws" {
  region = "eu-central-1"
  assume_role {
    role_arn     = "arn:aws:iam::162173573602:role/terraform/terraform-terraform-apply"
    session_name = "TerraformExecution"
  }

  default_tags {
    tags = {
      Managed = "Terraform"
      Project = "Terraform"
    }
  }
}
