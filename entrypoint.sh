#!/bin/sh

# Run the application
java -jar /app/Resolution-Engine-0.0.1-SNAPSHOT.jar --eshost=$DOCKER_HOST_IP --rphost=$DOCKER_HOST_IP
