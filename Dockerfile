FROM amazoncorretto:17-alpine-jdk
MAINTAINER demo
COPY target/demo-0.0.1-SNAPSHOT.jar demo-app.jar
ENTRYPOINT  ["java","-jar","/demo-app.jar"]
