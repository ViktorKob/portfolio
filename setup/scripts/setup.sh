sudo apt-get update
sudo apt-get -y upgrade
sudo ufw allow 22
sudo ufw allow 80
echo "y" | sudo ufw enable
sudo apt-get -y install maven mysql-server git openjdk-8-jdk nginx
mkdir git
cd git
git config credential.helper store
git clone https://github.com/ViktorKob/portfolio.git
cd
cp ~/git/portfolio/setup/scripts/*.sh .
chmod a+x *.sh
./package_system.sh
