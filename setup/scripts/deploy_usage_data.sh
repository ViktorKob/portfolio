echo "(Re)deploying usage-data service"
screen -S UsageData -X kill
mkdir -p ~/services/usage-data
cp ~/services/usage-data-service.jar ~/services/usage-data/
cd ~/services/usage-data/
rm screenlog.0
screen -S UsageData -L -d -m java -Xmx128M -jar usage-data-service.jar
cd
echo "Done (probably)"
