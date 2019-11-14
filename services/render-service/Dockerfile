FROM openjdk:12-jdk-alpine
ARG JAR_FILE
COPY target/${JAR_FILE} .
ENV JAR_FILE ${JAR_FILE}
ENTRYPOINT ["/bin/ash", "-c", "cp /config/${JAR_FILE}.yml /application.yml;java -Xmx100M -jar /${JAR_FILE}" ]