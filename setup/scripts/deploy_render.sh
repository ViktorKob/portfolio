echo "(Re)deploying render service"
screen -S Render -X kill
mkdir -p ~/services/render
cp ~/services/render-service.jar ~/services/render/
cd ~/services/render/
rm screenlog.0
screen -S Render -L -d -m java -Xmx128M -jar render-service.jar
cd
echo "Done (probably)"

