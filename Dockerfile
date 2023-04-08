FROM eclipse-temurin:19-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/senshi-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
