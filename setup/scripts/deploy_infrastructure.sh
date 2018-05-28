echo "(Re)deploying infrastructure service"
screen -S Infrastructure -X kill
mkdir -p ~/services/infrastructure
cp ~/services/infrastructure-service.jar ~/services/infrastructure/
cd ~/services/infrastructure/
rm screenlog.0
screen -S Infrastructure -L -d -m java -Xmx128M -jar infrastructure-service.jar
cd
echo "Done (probably)"


