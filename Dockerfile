# Stage 1: Build
FROM gradle:jdk17 AS builder
WORKDIR /workspace

COPY . /workspace/

RUN chmod +x gradlew

RUN gradle clean build -x test

# Extract the built JAR file into a directory for dependency management
RUN mkdir -p build/dependency && \
   JAR_FILE=$(ls build/libs/*.jar | head -n 1) && \
   cd build/dependency && \
   jar -xf ../libs/$(basename $JAR_FILE)

RUN echo $(ls -a build/dependency)

# Stage 2: Runtime
FROM openjdk:17
WORKDIR /workspace
ENV TZ=Asia/Dhaka

# Create a directory for application logs
RUN mkdir -p /var/log/max-live-spring-gateway

# Copy the dependencies from the build stage to the runtime stage
ARG DEPENDENCY=/workspace/build/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib app/lib
COPY --from=builder ${DEPENDENCY}/META-INF app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes app
ENTRYPOINT ["java","-cp","app:app/lib/*","net/max/live/com/SpringCloudGatewayApplication"]