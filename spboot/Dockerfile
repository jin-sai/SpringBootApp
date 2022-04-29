FROM openjdk:11
ADD target/users-mysql.jar users-mysql.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "users-mysql.jar", "com.saikumar.spboot.spboot.SpbootApplication"]