FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar tresor-seen.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","tresor-seen.jar"]