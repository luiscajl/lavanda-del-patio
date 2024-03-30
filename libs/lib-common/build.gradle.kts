plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.gradle.plugin-publish") version "0.15.0"
    id("io.freefair.lombok") version "8.6"
}

group = "es.lavanda"
version = "0.0.73"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}
repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.github.com/Lavanda-del-Patio/lib-common") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework:spring-context")
    implementation("commons-io:commons-io:2.14.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.3")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

springBoot {
    buildInfo()
}

// Si usas el plugin de release de Gradle o necesitas configurar la integración SCM, aquí es donde agregarías esa configuración.
// Por ejemplo, la configuración SCM se manejaría fuera de este archivo en Gradle, a menudo mediante propiedades del sistema o variables de entorno.
