# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure(2) do |config|
  # The most common configuration options are documented and commented below.
  # For a complete reference, please see the online documentation at
  # https://docs.vagrantup.com.

  #config.ssh.username = "vagrant"
  #config.ssh.password = "vagrant"

  # Every Vagrant development environment requires a box. You can search for
  # boxes at https://atlas.hashicorp.com/search.
  config.vm.box = "williamyeh/centos7-docker"
  config.vm.box_download_insecure = true
  # Box with latest version of Docker
  config.vm.box_version =  "1.12.1.20160830"

  # Disable automatic box update checking. If you disable this, then
  # boxes will only be checked for updates when the user runs
  # `vagrant box outdated`. This is not recommended.
  # config.vm.box_check_update = false

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8888" will access port 8888 on the guest machine.
  config.vm.network "forwarded_port", guest: 8800, host: 8800
  config.vm.network "forwarded_port", guest: 50000, host: 50000

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  # config.vm.network "private_network", ip: "192.168.33.10"

  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network "public_network"

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  # config.vm.synced_folder "../data", "/vagrant_data"
  config.vm.synced_folder "./jenkins-scripts", "/jenkins-scripts"

  # Provider-specific configuration so you can fine-tune various
  # backing providers for Vagrant. These expose provider-specific options.
  # Example for VirtualBox:
  #
  config.vm.provider "virtualbox" do |vb|
  #   # Display the VirtualBox GUI when booting the machine
  #   vb.gui = true
  #
  #   # Customize the amount of memory on the VM:
    vb.memory = "4096"

    # Setting the name of the VM
    vb.name = "DockerJenkins2"

    # Using the host's resolver as a DNS proxy in NAT mode
    vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
  end
  #
  # View the documentation for the provider you are using for more
  # information on available options.

  # Define a Vagrant Push strategy for pushing to Atlas. Other push strategies
  # such as FTP and Heroku are also available. See the documentation at
  # https://docs.vagrantup.com/v2/push/atlas.html for more information.
  # config.push.define "atlas" do |push|
  #   push.app = "YOUR_ATLAS_USERNAME/YOUR_APPLICATION_NAME"
  # end

  # Enable provisioning with a shell script. Additional provisioners such as
  # Puppet, Chef, Ansible, Salt, and Docker are also available. Please see the
  # documentation for more information about their specific syntax and use.
  # config.vm.provision "shell", inline: <<-SHELL
  #   sudo apt-get update
  #   sudo apt-get install -y apache2
  # SHELL
  environment_vars = {
    "jenkins_scripts_dir" => '/jenkins-scripts',
    "DEVOPS_JENKINS_HOME" => '/home/vagrant/jenkins_home',
    "DEVOPS_JENKINS_HTTP_PORT" => 8800,
    "DEVOPS_JENKINS_SLAVE_PORT" => 50888,
    "DOCKER_SERVICE_LOCATION" => "/etc/systemd/system/docker.service.d",
    "DOCKER_GROUP_GID" => 204,
    "SERVICE_USER_ID" => ENV['PROXY_USER'],
    "SERVICE_PASSWORD" => ENV['PROXY_PASSWORD']
  }

  config.vm.provision "shell", privileged: false, env: environment_vars, inline: <<-SHELL
    set -e -o pipefail

    mkdir -p $DEVOPS_JENKINS_HOME

    # set environment variables
    for key in DEVOPS_JENKINS_HOME DEVOPS_JENKINS_HTTP_PORT DEVOPS_JENKINS_SLAVE_PORT; do
      # use indirect reference to grab the value of the variable that 'key' refers to
      # see http://www.tldp.org/LDP/abs/html/ivr.html
      value=${!key}
      if [ `grep -wc ${key} ~/.bashrc` -eq 0 ]; then
        echo "export ${key}=${value}" >> ~/.bashrc
      fi
    done

  SHELL

  config.vm.provision "shell", privileged: true, env: environment_vars, inline: <<-SHELL
    # configure shell to exit on any command returning a non-zero status
    set -e -o pipefail

    base_packages="wget git"

    export DOCKER_GROUP_GID

    PATH=${vagrant_scripts_dir}:${jenkins_scripts_dir}:$PATH

    echo "installing packages ${base_packages}"
    yum install -y -q ${base_packages}

    groupmod --gid ${DOCKER_GROUP_GID} docker

    sudo systemctl daemon-reload
    sudo systemctl restart docker

    if [ -n "${DEVOPS_JENKINS_HOME}" -a -d "${DEVOPS_JENKINS_HOME}" ]; then
      rm -rf ${DEVOPS_JENKINS_HOME}/*
    fi

    /usr/local/bin/docker-compose -f /vagrant/docker-compose.yml up -d --build
  SHELL

end
