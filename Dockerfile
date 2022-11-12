# See https://docs.docker.com/engine/userguide/eng-image/multistage-build/
# First build the app on a maven open jdk 11 container
FROM maven:3.8.6-openjdk-11 as app-builder
WORKDIR /build

# A specifc git branch, tag or commit to build from, defaults to integration (dev build)
ARG GH_CHECKOUT
ENV GH_CHECKOUT=${GH_CHECKOUT:-integration}

# Clone the repo, checkout the revision and build the application
RUN git clone https://github.com/veraPDF/veraPDF-rest.git
RUN cd veraPDF-rest && git checkout origin/${GH_CHECKOUT} && mvn clean package

# Now build a Java JRE for the Alpine application image
FROM eclipse-temurin:11 as jre-build

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base,java.logging,java.xml,java.management,java.sql,java.desktop \
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
# Specify the veraPDF REST version if you want to (to be used in build automation), default is 0.1.0-SNAPSHOT
ARG VERAPDF_REST_VERSION
ENV VERAPDF_REST_VERSION=${VERAPDF_REST_VERSION:-0.1.0-SNAPSHOT}

# Since this is a running network service we'll create an unprivileged account
# which will be used to perform the rest of the work and run the actual service:
RUN useradd --system --user-group --home-dir=/opt/verapdf-rest verapdf-rest
USER verapdf-rest

# Copy the JRE from the previous stage
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME

WORKDIR /opt/verapdf-rest
# Copy the application from the previous stage
COPY --from=app-builder /build/veraPDF-rest/target/verapdf-rest-${VERAPDF_REST_VERSION}.jar /opt/verapdf-rest/
# Copy the default configuration file
COPY server.yml /opt/verapdf-rest/

EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -Djava.awt.headless=true -jar /opt/verapdf-rest/verapdf-rest-${VERAPDF_REST_VERSION}.jar server server.yml
