terraform {
  backend "s3" {
    bucket = "terraform"
    key    = "common.tfstate"
    region = "eu-central-1"
  }
}

provider "aws" {
  region = "eu-central-1"
}