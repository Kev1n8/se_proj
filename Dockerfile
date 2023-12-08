FROM openjdk:17-jdk

RUN apk --no-cache add findutils

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew

COPY . .

RUN ./gradlew build

EXPOSE 8080

CMD ["java", "-jar", "./build/libs/gradle-0.0.1-SNAPSHOT.jar"]
