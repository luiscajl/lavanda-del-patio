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
# Guarda la salida y los errores del comando en una variable, pero también imprime en tiempo real.
OUTPUT=$(gradle "$APP":bootBuildImage 2>&1 | tee /dev/stderr)

# Ejecuta el comando bootBuildImage y filtra la salida para obtener el nombre de la imagen
IMAGE_NAME=$(echo "$OUTPUT" | grep "Successfully built image" | sed -n -e 's/^.*Successfully built image '\''\(.*\)'\''.*$/\1/p')

# Verifica si IMAGE_NAME no está vacío
if [ -n "$IMAGE_NAME" ]; then
    # Exporta el nombre de la imagen a una variable de entorno
    export DOCKER_IMAGE_NAME=$IMAGE_NAME
    echo "Exported DOCKER_IMAGE_NAME=$DOCKER_IMAGE_NAME"
    echo "DOCKER_IMAGE_NAME=$DOCKER_IMAGE_NAME" >> $GITHUB_OUTPUT
else
    echo "No se pudo encontrar el nombre de la imagen."
fi

