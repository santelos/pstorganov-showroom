module "terraform" {
  source = "./module"
  project_name = "terraform"
  terraform_bucket_arn = aws_s3_bucket.terraform_main.arn
  trusted_user_arn = aws_iam_user.terraform_main.arn
  extra_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action = [
          "iam:ListPolicies",
          "iam:ListRoles",
          "iam:ListUsers",
        ]
        Resource = "*",
      },
      {
        Effect   = "Allow"
        Action = [
            "iam:ListUserPolicies",
            "iam:ListAttachedUserPolicies",
            "iam:ListUserTags",
            "iam:ListRolePolicies",
            "iam:ListAttachedRolePolicies",
            "iam:ListRoleTags",
            "iam:ListPolicyTags",
            "iam:ListPolicyVersions",
            "iam:GetUser",
            "iam:GetUserPolicy",
            "iam:GetRole",
            "iam:GetRolePolicy",
            "iam:GetPolicy",
            "iam:GetPolicyVersion",
            "iam:CreateUser",
            "iam:UpdateUser",
            "iam:DeleteUser",
            "iam:CreateRole",
            "iam:UpdateRole",
            "iam:DeleteRole",
            "iam:UpdateRoleDescription",
            "iam:AttachUserPolicy",
            "iam:DetachUserPolicy",
            "iam:DeleteUserPolicy",
            "iam:PutUserPolicy",
            "iam:AttachRolePolicy",
            "iam:DetachRolePolicy",
            "iam:DeleteRolePolicy",
            "iam:PutRolePolicy",
          ]
        Resource = [
          "arn:aws:iam::162173573602:user/terraform/*",
          "arn:aws:iam::162173573602:role/terraform/*",
          "arn:aws:iam::162173573602:policy/terraform/*",
        ],
      },
      {
        Effect   = "Allow"
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
    ]
  })
}
