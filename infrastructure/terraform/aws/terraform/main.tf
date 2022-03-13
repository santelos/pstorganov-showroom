terraform {
  backend "s3" {
    bucket = "pstroganov-terraform-main"
    key    = "terraform.tfstate"
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
      Project = "Terraform"
    }
  }
}