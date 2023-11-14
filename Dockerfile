#
# Build stage
#
FROM maven:3.6.3 AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:17
COPY --from=build /target/tresor-seen.jar tresor-seen.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","tresor-seen.jar"]