resource "aws_iam_user" "terraform_main" {
  name = "terraform-main"
  path = "/terraform/"
}

resource "aws_iam_openid_connect_provider" "terraform_main" {
  url = "https://token.actions.githubusercontent.com"

  client_id_list = [
    "sts.amazonaws.com",
  ]

  thumbprint_list = [
    "6938fd4d98bab03faadb97b34396831e3780aea1"
  ]
}
