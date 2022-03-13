terraform {
  backend "s3" {
    bucket = "pstroganov-terraform-main"
    key    = "ecr.tfstate"
    region = "eu-central-1"
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

  default_tags {
    tags = {
      Managed = "Terraform"
      Project = "ECR"
    }
  }
}

module "repo" {
  source       = "./module"
  for_each     = toset(local.repos)
  project_name = each.key
}

locals {
  repos = [
    "oauth2-server",
    "resource-server",
    "oauth2-web-client"
  ]
}
