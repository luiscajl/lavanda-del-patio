#!/usr/bin/env bash
echo "Going to push image to docker registry"
echo $1
docker push "$1"