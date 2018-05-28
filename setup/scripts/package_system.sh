echo "Updating sources and building packages"
cd ~/git/portfolio
git pull
cd ~/git/portfolio/tools
mvn install
cd ~/git/portfolio/infrastructure
mvn package
cd ~/git/portfolio/services
mvn package

cd 

echo "Moving services to staging folder"
cp ~/git/portfolio/infrastructure/target/infrastructure-service.jar ~/services/

cp ~/git/portfolio/services/analytics-service/target/analytics-service.jar ~/services/
cp ~/git/portfolio/services/hbase-indexing-service/target/hbase-indexing-service.jar ~/services/
cp ~/git/portfolio/services/legal-service/target/legal-service.jar ~/services/
cp ~/git/portfolio/services/nexus-service/target/nexus-service.jar ~/services/
cp ~/git/portfolio/services/render-service/target/render-service.jar ~/services/
cp ~/git/portfolio/services/usage-data-service/target/usage-data-service.jar ~/services/

echo "Ready for deployment"
