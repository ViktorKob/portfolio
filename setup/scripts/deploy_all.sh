./deploy_infrastructure.sh
sleep 30
./deploy_hbase_index.sh
./deploy_render.sh
./deploy_usage_data.sh
./deploy_analytics.sh
./deploy_legal.sh
./deploy_nexus.sh

