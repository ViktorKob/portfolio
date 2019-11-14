###########################

SERVICE_NAME="proxy-service"
CONTAINER_NAME="proxy"
JAR_VERSION="0.0.1-SNAPSHOT"
MOUNTS="-v /home/ubuntu/docker/config:/config"
PORTS="-p 443:443"

###########################

./deploy_service.sh ${SERVICE_NAME} ${CONTAINER_NAME} ${JAR_VERSION} "${MOUNTS}" "${PORTS}"