import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.google.cloud.tools.jib") version "3.4.2"
    id("io.freefair.lombok") version "8.6"
}

group = "es.lavanda"
version = "0.0.136"
val dockerLibrary = "lavandadelpatio"


java {
    sourceCompatibility = JavaVersion.VERSION_21
}
dependencies {
    implementation("es.lavanda:lib-common:0.1.4")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("org.glassfish.jaxb:jaxb-runtime")
    runtimeOnly("com.thoughtworks.xstream:xstream:1.4.20")
    testImplementation("org.mockito:mockito-junit-jupiter:5.6.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.kubernetes:client-java:20.0.1")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
jib {
    to {
        image = "lavandadelpatio/filebot-executor"
        tags = setOf("$version")
        auth {
            username = System.getenv("DOCKERHUB_USERNAME")
            password = System.getenv("DOCKERHUB_TOKEN")
        }
    }

}