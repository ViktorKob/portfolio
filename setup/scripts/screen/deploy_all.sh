#!/usr/bin/env bash
./deploy_infrastructure.sh
sleep 10
./deploy_proxy.sh
./deploy_admin.sh
./deploy_hbase_index.sh
sleep 10
./deploy_render.sh
./deploy_usage_data.sh
./deploy_analytics.sh
./deploy_legal.sh
./deploy_nexus.sh