# See https://docs.docker.com/engine/userguide/eng-image/multistage-build/
# First build the app on a maven open jdk 11 container
FROM maven:3-eclipse-temurin-11-alpine AS app-builder
# We need git to check out the source
RUN apk add --no-cache git

WORKDIR /build

# A specifc git branch, tag or commit to build from, defaults to master (release build)
ARG GH_CHECKOUT
ENV GH_CHECKOUT=${GH_CHECKOUT:-arlington-master}

# Clone the repo, checkout the revision and build the application
RUN git clone https://github.com/veraPDF/veraPDF-rest.git

WORKDIR /build/veraPDF-rest
RUN git checkout ${GH_CHECKOUT} && mvn clean package

# Now build a Java JRE for the Alpine application image
# https://github.com/docker-library/docs/blob/master/eclipse-temurin/README.md#creating-a-jre-using-jlink
FROM eclipse-temurin:11-jdk-alpine AS jre-builder

# Create a custom Java runtime
RUN "$JAVA_HOME/bin/jlink" \
         --add-modules java.base,java.logging,java.xml,java.management,java.sql,java.desktop,jdk.crypto.ec \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

# Now the final application image
FROM alpine:3

# Specify the veraPDF REST version if you want to (to be used in build automation), default is 1.28.1
ARG VERAPDF_REST_VERSION
ENV VERAPDF_REST_VERSION=${VERAPDF_REST_VERSION:-1.28.1}

# Set up dumb-init for process safety: https://github.com/Yelp/dumb-init
ADD --link https://github.com/Yelp/dumb-init/releases/download/v1.2.5/dumb-init_1.2.5_x86_64 /usr/local/bin/dumb-init 
RUN chmod +x /usr/local/bin/dumb-init

# Copy the JRE from the previous stage
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-builder /javaruntime $JAVA_HOME

# Since this is a running network service we'll create an unprivileged account
# which will be used to perform the rest of the work and run the actual service:
RUN addgroup -S verapdf-rest && adduser --system -D --home /opt/verapdf-rest -G verapdf-rest verapdf-rest
RUN mkdir --parents /var/opt/verapdf-rest/logs && chown -R verapdf-rest:verapdf-rest /var/opt/verapdf-rest

USER verapdf-rest
WORKDIR /opt/verapdf-rest

# Copy the application from the previous stage
COPY --from=app-builder /build/veraPDF-rest/target/verapdf-rest-arlington-${VERAPDF_REST_VERSION}.jar /opt/verapdf-rest/verapdf-rest-arlington.jar
# Copy the default configuration file
COPY --from=app-builder /build/veraPDF-rest/server.yml /var/opt/verapdf-rest/config/
COPY --from=app-builder /build/veraPDF-rest/config /opt/verapdf-rest/config/

VOLUME /var/opt/verapdf-rest
EXPOSE 8080

ENTRYPOINT [ "dumb-init", "--" ]
CMD [ "java", "-Djava.awt.headless=true", "-jar", "/opt/verapdf-rest/verapdf-rest-arlington.jar", "server", "/var/opt/verapdf-rest/config/server.yml"]
