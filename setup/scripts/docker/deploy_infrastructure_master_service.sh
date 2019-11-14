#!/bin/bash
###########################

SERVICE_NAME="infrastructure-service"
CONTAINER_NAME="infrastructure-master"
JAR_VERSION="0.0.1-SNAPSHOT"
MOUNTS="-v /home/ubuntu/docker/config:/config"
PORTS="-p 8000:8000"

###########################

./deploy_service.sh ${SERVICE_NAME} ${CONTAINER_NAME} ${JAR_VERSION} "${MOUNTS}" "${PORTS}"