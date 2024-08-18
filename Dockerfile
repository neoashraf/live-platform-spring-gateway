# FROM ktanim90/ims-00-jdk:1.0
# EXPOSE 8080
# WORKDIR /app
# COPY ./build/libs/api-gateway-1.0.jar .
# CMD ["java", "-jar", "api-gateway-1.0.jar"]


# Stage 1: Build
FROM gradle:8.4-jdk17 AS builder
WORKDIR /workspace

COPY . /workspace/

RUN chmod +x gradlew

RUN ./gradlew build -x test

# Verify the contents of the build/libs directory
RUN ls -la build/libs

# Extract the JAR file
RUN mkdir -p build/dependency && \
    JAR_FILE=$(ls build/libs/*.jar | head -n 1) && \
    cd build/dependency && \
    jar -xf ../libs/$(basename $JAR_FILE)

# Stage 2: Runtime
FROM openjdk:17
WORKDIR /workspace
ENV TZ=Asia/Dhaka
RUN mkdir -p /var/log/teenpatti

ARG DEPENDENCY=/workspace/build/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib app/lib
COPY --from=builder ${DEPENDENCY}/META-INF app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes app
ENTRYPOINT ["java","-cp","app:app/lib/*","net/max/live/com/SpringCloudGatewayApplication"]


