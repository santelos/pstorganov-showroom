resource "aws_iam_role" "terraform_backend" {
  name = "terraform-${var.project_name}-backend"
  path = "/terraform/"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          AWS = "${var.trusted_user_arn}"
        }
      },
    ]
  })

  inline_policy {
    name = "terraform-main-policy"

    policy = jsonencode({
      Version = "2012-10-17"
      Statement = [
        {
          Action   = ["s3:ListBucket"]
          Effect   = "Allow"
          Resource = "${var.terraform_bucket_arn}",
        },
        {
          Action = ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"],
          Effect = "Allow",
          Resource = "${var.terraform_bucket_arn}/${var.project_name}.tfstate",
        },
      ]
    })
  }
}

resource "aws_iam_role" "terraform_apply" {
  name = "terraform-${var.project_name}-apply"
  path = "/terraform/"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          AWS = "${var.trusted_user_arn}"
        }
      },
    ]
  })

  inline_policy {
    name = "extra-policy"
    policy = var.extra_policy
  }
}
