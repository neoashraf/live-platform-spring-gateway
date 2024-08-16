FROM ktanim90/ims-00-jdk:1.0
EXPOSE 8080
WORKDIR /app
COPY ./build/libs/api-gateway-1.0.jar .
CMD ["java", "-jar", "api-gateway-1.0.jar"]
