#!/usr/bin/env bash
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get -y upgrade
sudo ufw allow 22
sudo ufw allow 443
echo "y" | sudo ufw enable
sudo apt-get -y install maven git oracle-java8-installer
mkdir git
cd git
git config credential.helper store
git clone https://github.com/ViktorKob/portfolio.git
cd
cp ~/git/portfolio/setup/scripts/*.sh .
chmod a+x *.sh
./package_system.sh

sudo setcap CAP_NET_BIND_SERVICE=+eip /usr/lib/jvm/java-8-oracle/jre/bin/java