sudo yum install docker
sudo usermod -a -G docker ec2-user
id ec2-user
sudo pip3 install docker-compose
sudo systemctl enable docker.service
sudo systemctl start docker.service


