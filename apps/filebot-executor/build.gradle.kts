import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.graalvm.buildtools.native") version "0.10.1"
    id("io.freefair.lombok") version "8.6"
}

group = "es.lavanda"
version = "0.0.133"
val dockerLibrary = "lavandadelpatio"


java {
    sourceCompatibility = JavaVersion.VERSION_21
}
dependencies {
    implementation("es.lavanda:lib-common:0.1.3")
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

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("${dockerLibrary}/${project.name}:${version}")
//    builder.set("paketobuildpacks/builder-jammy-base:latest")
//    runImage.set("luiscajl/run-jammy-base-with-filebot:latest")
    createdDate = "now"
    environment = mapOf(
            "BP_NATIVE_IMAGE" to "true",
            "TZ" to "Europe/Madrid",
            "PUID" to "568",
            "PGID" to "568",
            "PGUSER" to "apps",
            "PGROUP" to "apps",
            "HOME" to "/home/apps"
    )
}


