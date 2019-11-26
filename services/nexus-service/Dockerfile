FROM openjdk:12-jdk-alpine
ARG JAR_FILE
COPY target/${JAR_FILE} .
ENV JAR_FILE ${JAR_FILE}
ENTRYPOINT ["/bin/ash", "-c", "java -Xmx500M -jar /${JAR_FILE}" ]