# Etapa de construcción
FROM ubuntu as builder

# Instalar herramientas necesarias y descargar FileBot
RUN apt update && apt install -y wget software-properties-common
RUN wget https://get.filebot.net/filebot/FileBot_5.1.3/FileBot_5.1.3_amd64.deb -O filebot.deb && \
    dpkg -i filebot.deb || apt-get install -f -y

RUN apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* /filebot.deb
RUN whereis "filebot"

# Etapa final
FROM paketobuildpacks/run-jammy-base:latest as runner

# Copiar FileBot del constructor al corredor
# Aquí es donde las cosas pueden complicarse un poco.
# Tendrías que copiar el binario de FileBot y sus dependencias, si es posible.
# Este ejemplo solo muestra la estructura; necesitarás ajustar los caminos según sea necesario.
COPY --from=builder /usr/bin/filebot /usr/local/bin/filebot
# Copia cualquier otra dependencia que FileBot pueda necesitar desde el 'builder' al 'runner'

# Configura cualquier variable de entorno necesaria o pasos finales
