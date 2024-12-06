import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.graalvm.buildtools.native") version "0.10.1"
    id("io.freefair.lombok") version "8.6"
}

group = "es.lavanda"
version = "0.0.78"
val dockerLibrary = "lavandadelpatio"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("es.lavanda:lib-common:0.1.5")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.telegram:telegrambots-spring-boot-starter:6.9.7.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.6.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.modelmapper:modelmapper:3.2.2") {
        exclude(group = "com.h2database", module = "h2")
        exclude(group = "org.testng", module = "testng")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("${dockerLibrary}/${project.name}:${version}")
    environment = mapOf("BP_NATIVE_IMAGE" to "true")
    environment = mapOf("TZ" to "Europe/Madrid")
}

