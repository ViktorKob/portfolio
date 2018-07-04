echo "(Re)deploying hbase-indexing service"
screen -S HbaseIndexing -X kill
mkdir -p ~/services/hbase-indexing
cp ~/services/hbase-indexing-service.jar ~/services/hbase-indexing/
cd ~/services/hbase-indexing/
rm screenlog.0
screen -S HbaseIndexing -L -d -m java -Xmx600M -jar hbase-indexing-service.jar
cd
echo "Done (probably)"

