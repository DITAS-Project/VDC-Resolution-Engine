#!/bin/sh

# Run the application
java -jar /app/Resolution-Engine-0.0.1-SNAPSHOT.jar --spring.application.json='{"host":$DOCKER_HOST_IP}'