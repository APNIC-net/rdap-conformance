
FROM openjdk:8-jdk

ENV MAVEN_VERSION=3.2.5 \
    M2_HOME=/m2

RUN cd /tmp && \
    wget http://www-eu.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    tar xf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    mv apache-maven-$MAVEN_VERSION $M2_HOME && \
    rm -f apache-maven-$MAVEN_VERSION-bin.tar.gz

COPY pom.xml /build/

COPY src/ /build/src/
COPY repo/ /build/repo/
RUN cd /build && \
    $M2_HOME/bin/mvn verify -DskipDocker && \
    mkdir /app && \
    cp target/*.jar /app && \
    cp target/docker-extras/entrypoint.sh /app/ && \
    chmod 0744 /app/entrypoint.sh && \
    cd / && \
    rm -rf /build ${M2_HOME}

WORKDIR /app
RUN useradd -MrU conformance && \
    mkdir -p /rdap-config && \
    chown -R conformance /app && \
    chown -R conformance /rdap-config

EXPOSE 8080
USER conformance
ENTRYPOINT ["/app/entrypoint.sh", "/rdap-config/rdap-configuration.json"]
