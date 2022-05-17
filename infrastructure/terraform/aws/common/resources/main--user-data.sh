#!/bin/bash
sudo yum -y update
sudo yum -y install docker
sudo usermod -a -G docker ec2-user
sudo pip3 --no-input install docker-compose
sudo systemctl enable docker.service
sudo systemctl start docker.service

mkdir ~/infrastructure
mkdir ~/main