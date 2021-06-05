# See https://docs.docker.com/engine/userguide/eng-image/multistage-build/

FROM maven as builder
WORKDIR /build
RUN git clone https://github.com/veraPDF/veraPDF-rest.git
RUN cd veraPDF-rest && git checkout integration && mvn clean package


FROM openjdk:8-jre-alpine

ENV VERAPDF_REST_VERSION=0.1.0-SNAPSHOT

# Since this is a running network service we'll create an unprivileged account
# which will be used to perform the rest of the work and run the actual service:

# Debian:
# RUN useradd --system --user-group --home-dir=/opt/verapdf-rest verapdf-rest
# Alpine / Busybox:
RUN install -d -o root -g root -m 755 /opt && adduser -h /opt/verapdf-rest -S verapdf-rest
USER verapdf-rest
WORKDIR /opt/verapdf-rest

COPY --from=builder /build/veraPDF-rest/target/verapdf-rest-${VERAPDF_REST_VERSION}.jar /opt/verapdf-rest/

EXPOSE 8080
ENTRYPOINT java -jar /opt/verapdf-rest/verapdf-rest-${VERAPDF_REST_VERSION}.jar server server.yml
