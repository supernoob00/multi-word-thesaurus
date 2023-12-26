#
# Build stage
#
FROM maven:3.8-openjdk-18-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip

#
# Package stage
#
FROM eclipse-temurin:18-jre-alpine

ARG PGHOST
ARG PGPORT
ARG PGDATABASE
ARG PGUSER
ARG PGPASSWORD

ENV PGHOST = $PGHOST
ENV PGPORT = $PGPORT
ENV PGDATABASE = $PGDATABASE
ENV PGUSER = $PGUSER
ENV PGPASSWORD = $PGPASSWORD

COPY --from=build /home/app/target/multi-word-thesaurus-1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-DPGHOST=$PGHOST", "-DPGPORT=$PGPORT", "-DPGDATABASE=$PGDATABASE", "-DPGUSER=$PGUSER", "-DPGPASSWORD=$PGPASSWORD", "/app.jar"]