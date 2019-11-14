###########################

SERVICE_NAME="admin-service"
CONTAINER_NAME="admin"
JAR_VERSION="0.0.1-SNAPSHOT"
MOUNTS="-v /home/ubuntu/docker/config:/config"
PORTS="-p 8001:8001"

###########################

./deploy_service.sh ${SERVICE_NAME} ${CONTAINER_NAME} ${JAR_VERSION} "${MOUNTS}" "${PORTS}"