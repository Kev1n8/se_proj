FROM openjdk:17-jdk

RUN apt-get update && apt-get install -y \
  xargs

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew

COPY . .

RUN ./gradlew build

EXPOSE 8080

CMD ["java", "-jar", "./build/libs/gradle-0.0.1-SNAPSHOT.jar"]
