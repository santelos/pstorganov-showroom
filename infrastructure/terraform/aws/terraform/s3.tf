resource "aws_s3_bucket" "terraform_main" {
  bucket = "pstroganov-terraform-main"

  tags = {
    Name = "Terraform"
  }
}

resource "aws_s3_bucket_acl" "terraform_main" {
  bucket = aws_s3_bucket.terraform_main.id
  acl    = "private"
}

resource "aws_s3_bucket_public_access_block" "terraform_main" {
  bucket = aws_s3_bucket.terraform_main.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_versioning" "terraform_main" {
  bucket = aws_s3_bucket.terraform_main.id

  versioning_configuration {
    status = "Enabled"
  }
}
