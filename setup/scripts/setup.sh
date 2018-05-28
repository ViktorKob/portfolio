sudo apt-get update
sudo apt-get upgrade
sudo ufw allow 22
sudo ufw allow 80
sudo ufw enable
sudo apt-get install maven git openjdk-8-jdk nginx
mkdir git
cd git
git config credential.helper store
git clone https://github.com/ViktorKob/portfolio.git
cd
cp ~/git/portfolio/setup/scripts/*.sh .
chmod a+x *.sh
./package_system.sh

