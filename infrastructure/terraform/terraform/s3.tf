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