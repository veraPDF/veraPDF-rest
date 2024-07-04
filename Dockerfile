# See https://docs.docker.com/engine/userguide/eng-image/multistage-build/
# First build the app on a maven open jdk 11 container
FROM maven:3.8.6-openjdk-11 as app-builder
WORKDIR /build

# A specifc git branch, tag or commit to build from, defaults to master (release build)
ARG GH_CHECKOUT
ENV GH_CHECKOUT=${GH_CHECKOUT:-master}

# Clone the repo, checkout the revision and build the application
RUN git clone https://github.com/veraPDF/veraPDF-rest.git

WORKDIR /build/veraPDF-rest
RUN git checkout ${GH_CHECKOUT} && mvn clean package

# Install dumb-init for process safety
# https://github.com/Yelp/dumb-init
RUN wget -O /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.5/dumb-init_1.2.5_x86_64

# Now build a Java JRE for the Alpine application image
# https://github.com/docker-library/docs/blob/master/eclipse-temurin/README.md#creating-a-jre-using-jlink
FROM eclipse-temurin:11 as jre-builder

# Create a custom Java runtime
RUN "$JAVA_HOME/bin/jlink" \
         --add-modules java.base,java.logging,java.xml,java.management,java.sql,java.desktop,jdk.crypto.ec \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

# Now the final application image
FROM debian:bullseye-slim

# Set for additional arguments passed to the java run command, no default
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
# Specify the veraPDF REST version if you want to (to be used in build automation), default is 1.27.1
ARG VERAPDF_REST_VERSION
ENV VERAPDF_REST_VERSION=${VERAPDF_REST_VERSION:-1.27.1}

COPY --from=app-builder /usr/local/bin/dumb-init /usr/local/bin/dumb-init
RUN chmod +x /usr/local/bin/dumb-init

# Copy the JRE from the previous stage
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-builder /javaruntime $JAVA_HOME

# Since this is a running network service we'll create an unprivileged account
# which will be used to perform the rest of the work and run the actual service:
RUN useradd --system --user-group --home-dir=/opt/verapdf-rest verapdf-rest
RUN mkdir --parents /var/opt/verapdf-rest/logs && chown -R verapdf-rest:verapdf-rest /var/opt/verapdf-rest

USER verapdf-rest
WORKDIR /opt/verapdf-rest

# Copy the application from the previous stage
COPY --from=app-builder /build/veraPDF-rest/target/verapdf-rest-arlington-${VERAPDF_REST_VERSION}.jar /opt/verapdf-rest/
# Copy the default configuration file
COPY --from=app-builder /build/veraPDF-rest/server.yml /var/opt/verapdf-rest/config/
COPY --from=app-builder /build/veraPDF-rest/config /opt/verapdf-rest/config/

VOLUME /var/opt/verapdf-rest
EXPOSE 8080

ENTRYPOINT dumb-init java $JAVA_OPTS -Djava.awt.headless=true -jar /opt/verapdf-rest/verapdf-rest-arlington-${VERAPDF_REST_VERSION}.jar server /var/opt/verapdf-rest/config/server.yml
