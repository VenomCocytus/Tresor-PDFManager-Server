FROM openjdk:17
RUN mvn clean package
ADD target/tresor-seen.jar tresor-seen.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","tresor-seen.jar"]