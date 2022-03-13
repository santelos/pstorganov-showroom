module "terraform" {
  source               = "./module"
  project_name         = "terraform"
  terraform_bucket_arn = aws_s3_bucket.terraform_main.arn
  oidc_provider_arn    = aws_iam_openid_connect_provider.terraform_main.arn
  github_ref           = "repo:santelos/pstorganov-showroom:ref:refs/heads/master"
  extra_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "iam:ListPolicies",
          "iam:ListRoles",
          "iam:ListUsers",
        ]
        Resource = "*",
      },
      {
        Effect = "Allow"
        Action = [
          "iam:ListUserPolicies",
          "iam:ListAttachedUserPolicies",
          "iam:ListUserTags",
          "iam:GetUser",
          "iam:GetUserPolicy",
          "iam:CreateUser",
          "iam:UpdateUser",
          "iam:DeleteUser",
          "iam:TagUser",
          "iam:UntagUser",
          "iam:AttachUserPolicy",
          "iam:DetachUserPolicy",
          "iam:DeleteUserPolicy",
          "iam:PutUserPolicy",
        ]
        Resource = [
          "arn:aws:iam::162173573602:user/terraform/*",
        ],
      },
      {
        Effect = "Allow"
        Action = [
          "iam:ListRolePolicies",
          "iam:ListAttachedRolePolicies",
          "iam:ListInstanceProfilesForRole",
          "iam:ListRoleTags",
          "iam:GetRole",
          "iam:GetRolePolicy",
          "iam:CreateRole",
          "iam:UpdateRole",
          "iam:DeleteRole",
          "iam:UpdateRoleDescription",
          "iam:TagRole",
          "iam:UntagRole",
          "iam:AttachRolePolicy",
          "iam:DetachRolePolicy",
          "iam:DeleteRolePolicy",
          "iam:PutRolePolicy",
        ]
        Resource = [
          "arn:aws:iam::162173573602:role/terraform/*",
        ],
      },
      {
        Effect = "Allow"
        Action = [
          "iam:ListPolicyTags",
          "iam:ListPolicyVersions",
          "iam:GetPolicy",
          "iam:GetPolicyVersion",
          "iam:TagPolicy",
          "iam:UntagPolicy",
        ]
        Resource = [
          "arn:aws:iam::162173573602:policy/terraform/*",
        ],
      },
      {
        Effect = "Allow"
        Action = [
          "s3:ListBucket",
          "s3:ListBucketVersions",
          "s3:GetBucket*",
          "s3:Get*Configuration",
          "s3:CreateBucket",
          "s3:PutBucket*",
          "s3:Put*Configuration",
        ]
        Resource = "${aws_s3_bucket.terraform_main.arn}",
      },
      {
        Effect = "Allow"
        Action = [
          "iam:*OpenIDConnectProvider*",
        ]
        Resource = "arn:aws:iam::162173573602:oidc-provider/*",
      },
    ]
  })
}

module "terraform-common" {
  source               = "./module"
  project_name         = "common"
  terraform_bucket_arn = aws_s3_bucket.terraform_main.arn
  oidc_provider_arn    = aws_iam_openid_connect_provider.terraform_main.arn
  github_ref           = "repo:santelos/pstorganov-showroom:ref:refs/heads/master"
  extra_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ec2:AuthorizeSecurityGroupEgress",
          "ec2:CreateSecurityGroup",
          "ec2:DeleteSecurityGroup",
          "ec2:DescribeAccountAttributes",
          "ec2:DescribeInstanceAttribute",
          "ec2:DescribeInstanceCreditSpecifications",
          "ec2:DescribeInstances",
          "ec2:DescribeNetworkInterfaces",
          "ec2:DescribeRouteTables",
          "ec2:DescribeSecurityGroups",
          "ec2:DescribeTags",
          "ec2:DescribeVolumes",
          "ec2:DescribeVpcAttribute",
          "ec2:DescribeVpcs",
          "ec2:ModifyInstanceAttribute",
          "ec2:RevokeSecurityGroupEgress",
          "ec2:RunInstances",
          "ec2:TerminateInstances",
        ]
        Resource = ["*"],
      },
    ]
  })
}

