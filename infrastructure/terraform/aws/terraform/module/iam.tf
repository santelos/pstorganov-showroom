resource "aws_iam_role" "terraform" {
  name = "terraform-${var.project_name}"
  path = "/terraform/"

  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json

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
          Action   = ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"],
          Effect   = "Allow",
          Resource = "${var.terraform_bucket_arn}/${var.project_name}.tfstate",
        },
      ]
    })
  }

  inline_policy {
    name   = "extra-policy"
    policy = var.extra_policy
  }
}

data "aws_iam_policy_document" "assume_role_policy" {
  statement {
    actions = ["sts:AssumeRoleWithWebIdentity"]
    effect  = "Allow"
    principals {
      type        = "Federated"
      identifiers = [var.oidc_provider_arn]
    }
    condition {
      test     = "ForAllValues:StringEquals"
      variable = "token.actions.githubusercontent.com:aud"

      values = ["sts.amazonaws.com"]
    }
    condition {
      test     = "ForAllValues:StringLike"
      variable = "token.actions.githubusercontent.com:sub"

      values = [var.github_ref]
    }
  }
}
