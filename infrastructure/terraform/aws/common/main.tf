terraform {
  backend "s3" {
    bucket = "pstroganov-terraform-main"
    key    = "common.tfstate"
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
      Project = "Common"
    }
  }
}

locals {
  vpc_id = "vpc-001cabde8511e99b8"
}
