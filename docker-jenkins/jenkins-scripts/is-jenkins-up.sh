#!/bin/bash

initial_admin_password_file=/var/jenkins_home/secrets/initialAdminPassword

while [ ! -f ${initial_admin_password_file} ]; do
  echo "  waiting for generation of admin config file"
  sleep 2
done
echo "***** Admin config file created by Jenkins.*****"
