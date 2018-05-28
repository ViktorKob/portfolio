echo "(Re)deploying analytics service"
screen -S Analytics -X kill
mkdir -p ~/services/analytics
cp ~/services/analytics-service.jar ~/services/analytics/
cd ~/services/analytics/
rm screenlog.0
screen -S Analytics -L -d -m java -Xmx128M -jar analytics-service.jar
cd
echo "Done (probably)"
