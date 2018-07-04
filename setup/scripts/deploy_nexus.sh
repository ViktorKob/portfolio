echo "(Re)deploying nexus service"
screen -S Nexus -X kill
mkdir -p ~/services/nexus
cp ~/services/nexus-service.jar ~/services/nexus/
cd ~/services/nexus/
rm screenlog.0
screen -S Nexus -L -d -m java -Xmx400M -jar nexus-service.jar
cd
echo "Done (probably)"
