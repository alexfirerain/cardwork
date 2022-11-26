FROM openjdk:latest
EXPOSE 5500
ADD build/libs/cardwork-0.0.1-SNAPSHOT.jar cardwork-app
ENTRYPOINT ["java","-jar","/cardwork-app"]