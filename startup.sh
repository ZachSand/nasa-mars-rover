#!/bin/bash

cd ./nasa-mars-rover-server
gradle docker 
docker-compose up -d