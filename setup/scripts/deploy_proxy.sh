#!/usr/bin/env bash
echo "(Re)deploying proxy service"
screen -S Proxy -X kill
mkdir -p ~/services/proxy
cp ~/services/proxy-service.jar ~/services/proxy/
cd ~/services/proxy/
rm screenlog.0
screen -S Proxy -L -d -m java -Xmx400M -jar proxy-service.jar
cd
echo "Done (probably)"
