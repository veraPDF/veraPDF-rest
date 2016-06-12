# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

$script = <<SCRIPT
sudo apt-get update
sudo apt-get install -y openjdk-7-jre-headless
sudo apt-get install nginx
sudo cp /vagrant/nginx/sites-available/rest.verapdf.org /etc/nginx/sites-available
sudo ln -s /etc/nginx/sites-available/rest.verapdf.org /etc/nginx/sites-enabled/rest.verapdf.org
sudo service nginx restart
SCRIPT

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  # All Vagrant configuration is done here. The most common configuration
  # options are documented and commented below. For a complete reference,
  # please see the online documentation at vagrantup.com.

  # Every Vagrant virtual environment requires a box to build off of.
  config.vm.box = "puphpet/debian75-x64"

  # Port forward HTTP (80) to host 8080
  # config.vm.network :forwarded_port, :host => 2020, :guest => 80
  config.vm.provider :virtualbox do |vb|
    vb.memory = 1024
    vb.cpus = 2
  end

  # Set the box host-name and IP Address
  config.vm.network "private_network", ip: "192.168.10.50"

  # argument is a set of non-required options.
  config.vm.provider :virtualbox do |vb|
    # set the virtual box name
    vb.name = "veraPDF-REST"
  end

  config.vm.provision "shell", inline: $script
end
