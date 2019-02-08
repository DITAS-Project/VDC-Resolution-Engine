#!/usr/bin/env bash
# Production environment: 178.22.69.83
# Private key for ssh: /opt/keypairs/ditas-testbed-keypair.pem

# This file is part of VDC-Resolution-Engine.
# 
# VDC-Resolution-Engine is free software: you can redistribute it 
# and/or modify it under the terms of the GNU General Public License as 
# published by the Free Software Foundation, either version 3 of the License, 
# or (at your option) any later version.
# 
# VDC-Resolution-Engine is distributed in the hope that it will be 
# useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with VDC-Resolution-Engine.  
# If not, see <https://www.gnu.org/licenses/>.
# 
# VDC-Resolution-Engine is being developed for the
# DITAS Project: https://www.ditas-project.eu/

# TODO state management? We are killing without careing about any operation the conainer could be doing.

ssh -i /opt/keypairs/ditas-testbed-keypair.pem cloudsigma@178.22.69.83 << 'ENDSSH'
# Ensure that a previously running instance is stopped (-f stops and removes in a single step)
# || true - "docker stop" failt with exit status 1 if image doen't exists, what makes the Pipeline fail. the "|| true" forces the command to exit with 0.
sudo docker stop --time 20 vdc-resolution-engine || true
sudo docker rm --force vdc-resolution-engine || true
sudo docker pull ditas/vdc-resolution-engine:production

# Get the host IP
HOST_IP="$(ip route get 8.8.8.8 | awk '{print $NF; exit}')"


# SET THE PORT MAPPING
sudo docker run -p 50011:8080 -e DOCKER_HOST_IP=$HOST_IP --restart unless-stopped -d --name vdc-resolution-engine ditas/vdc-resolution-engine:production
ENDSSH