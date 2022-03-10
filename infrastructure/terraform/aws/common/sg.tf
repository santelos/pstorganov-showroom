resource "aws_security_group" "main" {
  name        = "main"
  description = "Allow trafic to main"
  vpc_id      = data.aws_vpc.default.id

  tags = {
    Name = "Main"
  }
}

resource "aws_security_group_rule" "main_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 65535
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.main.id
}
