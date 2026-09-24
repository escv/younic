# build it with: docker build -t escv/younic .
# run it with: docker run -p 127.0.0.1:8080:8080 -it escv/younic

# Stage 1: extract the distribution and build a trimmed JRE with the modules
# the bundles actually use (computed via jdeps over bundle/*.jar + embedded libs)
FROM eclipse-temurin:21-jdk-alpine AS build
RUN apk add --no-cache unzip
WORKDIR /x
COPY younic.zip .
RUN unzip -q younic.zip && rm younic.zip
RUN jlink \
      --add-modules java.base,java.compiler,java.desktop,java.instrument,java.logging,java.management,java.management.rmi,java.naming,java.security.jgss,java.sql,java.xml,jdk.jdi,jdk.unsupported,jdk.crypto.ec \
      --strip-debug --no-man-pages --no-header-files --compress=zip-6 \
      --output /javaruntime

# Stage 2: minimal runtime. git is required for runtime content sync (start.sh clone + git pull)
FROM alpine:3.20
LABEL maintainer="Andre Albert <andre.albert82@googlemail.com>"
RUN apk add --no-cache git
WORKDIR /opt/younic
COPY --from=build /javaruntime /opt/java
COPY --from=build /x/ /opt/younic/
ENV JAVA_HOME=/opt/java PATH="/opt/java/bin:${PATH}"
EXPOSE 8080
ENTRYPOINT ["./start.sh"]