resource "aws_iam_user" "terraform_main" {
  name = "terraform-main"
  path = "/terraform/"
}
