FROM maven AS build
WORKDIR /app
COPY src ./src
COPY pom.xml ./pom.xml
RUN mvn package
FROM openjdk:23-slim
WORKDIR /app
COPY  --from=build /app/target/saleChatBot-1.0-SNAPSHOT.jar /app/saleChatBot.jar
ENTRYPOINT ["java", "-jar", "saleChatBot.jar"]