import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.graalvm.buildtools.native") version "0.10.1"
    id("io.freefair.lombok") version "8.6"
}

group = "es.lavanda"
version = "0.0.43"
val dockerLibrary = "lavandadelpatio"


java {
    sourceCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("es.lavanda:lib-common:0.1.4")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.1")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")
    compileOnly("org.projectlombok:lombok:1.18.34")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-junit-jupiter:5.7.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("${dockerLibrary}/${project.name}:${version}")
    createdDate = "now"
    environment = mapOf(
            "BP_NATIVE_IMAGE" to "true",
            "TZ" to "Europe/Madrid",
            "LANG" to "en_US.UTF-8"
    )
}

