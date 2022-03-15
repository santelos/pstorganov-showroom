data "aws_iam_openid_connect_provider" "github" {
  arn = "arn:aws:iam::162173573602:oidc-provider/token.actions.githubusercontent.com"
}

resource "aws_iam_role" "ec2_connect" {
  name = "ec2-connect"
  path = "/common/"

  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json

  inline_policy {
    name = "ecr-pull"

    policy = jsonencode({
      Version = "2012-10-17"
      Statement = [
        {
          Effect   = "Allow"
          Action   = "ec2:DescribeInstances"
          Resource = "*"
        },
        {
          Effect   = "Allow"
          Action   = "ec2-instance-connect:SendSSHPublicKey"
          Resource = "${aws_instance.main.arn}"
        },
      ]
    })
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