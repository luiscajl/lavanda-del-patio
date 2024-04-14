import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.graalvm.buildtools.native") version "0.10.1"
    id("io.freefair.lombok") version "8.6"
}

group = "es.lavanda"
version = "0.0.21"
val dockerLibrary = "lavandadelpatio"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("es.lavanda:lib-common:0.1.4")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("org.glassfish.jaxb:jaxb-runtime")
    runtimeOnly("com.thoughtworks.xstream:xstream:1.4.20")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-junit-jupiter:2.28.2")
    compileOnly("org.projectlombok:lombok:1.18.30")
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

