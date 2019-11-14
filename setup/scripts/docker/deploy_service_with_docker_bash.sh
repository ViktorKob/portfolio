#!/bin/bash

if ! [ $# = 5 ]
then
  echo -e "\e[0;32mYou need to specify exactly the following parameters for the script:\e[0;00m"
  echo -e "###################################################################################"
  echo -e "\e[0;32mService name:\e[0;00m    The name of the service in maven. E.g.: proxy-service"
  echo -e "\e[0;32mContainer name:\e[0;00m  The name of the container in docker. E.g.: proxy"
  echo -e "\e[0;32mJar version:\e[0;00m     The version of the jar in maven. E.g.: 0.0.1-SNAPSHOT"
  echo -e "\e[0;32mMounts:\e[0;00m          The folders that should be mapped into the container. E.g.: -v /home/ubuntu/docker/config:/config"
  echo -e "\e[0;32mPorts:\e[0;00m           The port-mappings to expose to the world. E.g.: -p 443:443"
  echo -e "###################################################################################"
  echo -e "It is valid to add several mounts and port in sequence. E.g.: -p 443:443 -p 80:80"
  echo
  exit 1
else
  SERVICE_NAME=${1}
  CONTAINER_NAME=${2}
  JAR_VERSION=${3}
  MOUNTS=${4}
  PORTS=${5}

  echo "(Re)deploying ${SERVICE_NAME}:${JAR_VERSION} to ${CONTAINER_NAME}"

  echo "Fetching new image"
  docker pull viktorkob/portfolio-${SERVICE_NAME}:${JAR_VERSION}

  echo "Stopping old container"
  docker stop ${CONTAINER_NAME}

  echo
  echo "Waiting for container to exit"
  sleep 5

  echo
  echo "Removing old container"
  docker rm ${CONTAINER_NAME}
  docker system prune -f

  echo
  echo "Starting container"
  docker run --name ${CONTAINER_NAME} --network host ${MOUNTS} -tid ${PORTS} viktorkob/portfolio-${SERVICE_NAME}:${JAR_VERSION}

  echo
  echo "All done"
fi