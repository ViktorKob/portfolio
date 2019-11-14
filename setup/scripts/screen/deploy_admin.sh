#!/usr/bin/env bash
echo "(Re)deploying admin service"
screen -S Admin -X kill
mkdir -p ~/services/admin
cp ~/services/admin-service.jar ~/services/admin/
cd ~/services/admin/
rm screenlog.0
screen -S Admin -L -d -m java -Xmx100M -jar admin-service.jar
cd
echo "Done (probably)"
