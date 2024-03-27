#!/usr/bin/env bash

#
# Builds a Docker native image using gradle and graalvm.
#
# Environment variables:
#
#   CONTAINER_REGISTRY - The hostname of your container registry.
#   VERSION - The version number to tag the images with.
#
# Parameters:
#
#   1 - The path to the code for the image and the name of the image.
#
# Usage:
#
#       ./scripts/build-image.sh <image-name>
#
# Example command line usage:
#
#       ./scripts/build-image.sh apps:demo
#

#set -u # or set -o nounset
#: "$1"
#: "$CONTAINER_REGISTRY"
#: "$VERSION"

export APP=$1
#gradle "$APP":bootBuildImage

# Ejecuta el comando bootBuildImage y filtra la salida para obtener el nombre de la imagen
IMAGE_NAME=$(gradle "$APP":bootBuildImage | tee /dev/tty | grep "Successfully built image" | sed -n -e 's/^.*Successfully built image '\''\(.*\)'\''.*$/\1/p')

# Verifica si IMAGE_NAME no está vacío
if [ -n "$IMAGE_NAME" ]; then
    # Exporta el nombre de la imagen a una variable de entorno
    export DOCKER_IMAGE_NAME=$IMAGE_NAME
    echo "Exported DOCKER_IMAGE_NAME=$DOCKER_IMAGE_NAME"
else
    echo "No se pudo encontrar el nombre de la imagen."
fi