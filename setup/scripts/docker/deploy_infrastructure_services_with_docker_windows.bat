@ECHO off
SET JAR_VERSION=0.0.1-SNAPSHOT
ECHO Current version is set to %JAR_VERSION%

ECHO Removing old containers
docker stop infrastructure-master
docker stop proxy
docker stop admin

ECHO.
ECHO Waiting for containers to exit
TIMEOUT 10

ECHO.
ECHO Removing old containers
docker rm infrastructure-master
docker rm proxy
docker rm admin
docker system prune -f
 
ECHO.
ECHO Starting infrastructure 
docker run --name infrastructure-master -v D:/Config:/config -tidp 8000:8000 viktorkob/portfolio-infrastructure-service:%JAR_VERSION%
docker run --name proxy -v D:/Config:/config -tidp 80:80 viktorkob/portfolio-proxy-service:%JAR_VERSION%
docker run --name admin -v D:/Config:/config -tidp 8001:8001 viktorkob/portfolio-admin-service:%JAR_VERSION%

ECHO.
ECHO All done, press any key or wait for timeout to exit
TIMEOUT 60