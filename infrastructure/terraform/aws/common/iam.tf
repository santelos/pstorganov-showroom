data "aws_iam_openid_connect_provider" "github" {
  arn = "arn:aws:iam::162173573602:oidc-provider/token.actions.githubusercontent.com"
}

resource "aws_iam_role" "ec2_connect" {
  name = "ec2-connect"
  path = "/common/"

  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json

  inline_policy {
    name   = "ec2-connect"
    policy = data.aws_iam_policy_document.ec2_connect.json
  }
}

data "aws_iam_policy_document" "assume_role_policy" {
  statement {
    actions = ["sts:AssumeRoleWithWebIdentity"]
    effect  = "Allow"
    principals {
      type        = "Federated"
      identifiers = [data.aws_iam_openid_connect_provider.github.arn]
    }
    condition {
      test     = "ForAllValues:StringEquals"
      variable = "token.actions.githubusercontent.com:aud"

      values = ["sts.amazonaws.com"]
    }
    condition {
      test     = "ForAllValues:StringLike"
      variable = "token.actions.githubusercontent.com:sub"

      values = ["repo:santelos/pstorganov-showroom*"]
    }
  }
}

data "aws_iam_policy_document" "ec2_connect" {
  statement {
    actions = ["ec2:DescribeInstances"]
  }
  statement {
    actions = ["ec2-instance-connect:SendSSHPublicKey"]
    principals {
      type        = "Federated"
      identifiers = [aws_instance.main.arn]
    }
    condition {
      test     = "StringEquals"
      variable = "ec2:osuser"
      values   = ["ec2-user"]
    }
  }
}

resource "aws_iam_role" "ec2_main" {
  name = "ec2-main"
  path = "/common/"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })

  inline_policy {
    name = "ecr-connect"

    policy = jsonencode({
      Version = "2012-10-17"
      Statement = [
        {
          Effect = "Allow"
          Action = [
            "ecr:GetAuthorizationToken",
            "ecr:BatchCheckLayerAvailability",
            "ecr:GetDownloadUrlForLayer",
            "ecr:GetRepositoryPolicy",
            "ecr:DescribeRepositories",
            "ecr:ListImages",
            "ecr:DescribeImages",
            "ecr:BatchGetImage",
            "ecr:GetLifecyclePolicy",
            "ecr:GetLifecyclePolicyPreview",
            "ecr:ListTagsForResource",
            "ecr:DescribeImageScanFindings"
          ]
          Resource = "*"
        },
      ]
    })
  }
}
