#!/usr/bin/env bash
# Staging environment: 31.171.247.162
# Private key for ssh: /opt/keypairs/ditas-testbed-keypair.pem

# TODO state management? We are killing without careing about any operation the conainer could be doing.

ssh -i /opt/keypairs/ditas-testbed-keypair.pem cloudsigma@31.171.247.162 << 'ENDSSH'
# Ensure that a previously running instance is stopped (-f stops and removes in a single step)
# || true - "docker stop" failt with exit status 1 if image doen't exists, what makes the Pipeline fail. the "|| true" forces the command to exit with 0.
sudo docker stop --time 20 vdc-resolution-engine || true
sudo docker rm --force vdc-resolution-engine || true
sudo docker pull ditas/vdc-resolution-engine:latest
# SET THE PORT MAPPING
sudo docker run -p 50011:8080 -d --name vdc-resolution-engine ditas/vdc-resolution-engine:latest
ENDSSH