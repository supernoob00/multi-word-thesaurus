#
# Build stage
#
FROM maven:3.8-openjdk-18-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:18-jre-slim
COPY --from=build /home/app/target/multi-word-thesaurus-1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/app.jar"]