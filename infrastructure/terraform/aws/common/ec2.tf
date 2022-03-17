resource "aws_instance" "main" {
  ami           = "ami-0f61af304b14f15fb"
  instance_type = "t2.micro"

  ebs_block_device {
    volume_size           = "30"
    device_name           = "/dev/xvda"
    delete_on_termination = true
  }

  vpc_security_group_ids = [aws_security_group.main.id]
  iam_instance_profile   = aws_iam_instance_profile.main.id

  tags = {
    Name = "Main"
  }
}

resource "aws_iam_instance_profile" "main" {
  name_prefix = "main"
  path        = "/common/"
  role        = aws_iam_role.ec2_main.id
}