FROM openjdk:17

COPY build/micro micro.jar

CMD ["java", "-jar", ".jar"]