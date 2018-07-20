echo "Updating sources and building packages"
cd ~/git/portfolio
git pull
cd ~/git/portfolio/tools
mvn clean install
cd ~/git/portfolio/infrastructure
mvn clean package
cd ~/git/portfolio/admin
mvn clean package
cd ~/git/portfolio/proxy
mvn clean package
cd ~/git/portfolio/services
mvn clean package

cd 

echo "Moving services to staging folder"
mkdir -p ~/services 
cp ~/git/portfolio/infrastructure/target/infrastructure-service.jar ~/services/
cp ~/git/portfolio/admin/target/admin-service.jar ~/services/
cp ~/git/portfolio/proxy/target/proxy-service.jar ~/services/

cp ~/git/portfolio/services/analytics-service/target/analytics-service.jar ~/services/
cp ~/git/portfolio/services/hbase-indexing-service/target/hbase-indexing-service.jar ~/services/
cp ~/git/portfolio/services/legal-service/target/legal-service.jar ~/services/
cp ~/git/portfolio/services/nexus-service/target/nexus-service.jar ~/services/
cp ~/git/portfolio/services/render-service/target/render-service.jar ~/services/
cp ~/git/portfolio/services/usage-data-service/target/usage-data-service.jar ~/services/

echo "Ready for deployment"
