import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.graalvm.buildtools.native") version "0.10.1"
    id("io.freefair.lombok") version "8.6"
}

group = "es.lavanda"
version = "0.0.28"
val dockerLibrary = "lavandadelpatio"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.apache.commons:commons-csv:1.10.0")
    runtimeOnly("org.glassfish.jaxb:jaxb-runtime")
    runtimeOnly("com.thoughtworks.xstream:xstream:1.4.20")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.mockito:mockito-junit-jupiter:2.28.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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

