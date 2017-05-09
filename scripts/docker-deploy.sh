#!/bin/bash

VERSION=`cat pom.xml | grep "^    <version>.*</version>$" | awk -F'[><]' '{print $3}'`

echo "Deploying Docker image for $VERSION"

docker build -t apnic/rdap-conformance:$VERSION \
             -t apnic/rdap-conformance:latest .

docker login -u "$DOCKER_USERNAME" -p "$DOCKER_PASSWORD"
docker push apnic/rdap-conformance:$VERSION
docker push apnic/rdap-conformance:latest
