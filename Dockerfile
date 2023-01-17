FROM openjdk:11-jre-slim

ARG JAR_FILE=target/*.jar

RUN mkdir /opt/app-dir

COPY ${JAR_FILE} /opt/app-dir/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/opt/app-dir/app.jar"]