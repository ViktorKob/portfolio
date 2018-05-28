echo "(Re)deploying legal service"
screen -S Legal -X kill
mkdir -p ~/services/legal
cp ~/services/legal-service.jar ~/services/legal/
cd ~/services/legal/
rm screenlog.0
screen -S Legal -L -d -m java -Xmx128M -jar legal-service.jar
cd
echo "Done (probably)"
