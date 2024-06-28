#!/usr/bin/env bash

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


