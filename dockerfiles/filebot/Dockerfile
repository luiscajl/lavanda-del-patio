FROM rednoah/filebot:latest

# Instalar sudo
RUN apt-get update && apt-get install -y sudo

# Crear grupo y usuario 'apps'
RUN groupadd -r -g 568 apps && useradd --no-log-init -r -u 568 -g apps apps

# Crear directorio home para 'apps' y asignar permisos
RUN mkdir -p /home/apps && chown 568:568 /home/apps

# Configurar sudoers para permitir al usuario 'apps' usar sudo sin contraseña
RUN echo 'apps ALL=(ALL) NOPASSWD: ALL' >> /etc/sudoers

# Cambiar al usuario 'apps'
USER 568:568