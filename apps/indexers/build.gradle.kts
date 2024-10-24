plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.google.cloud.tools.jib") version "3.4.3"
    id("io.freefair.lombok") version "8.6"
}

version = "0.0.41"
group = "es.lavanda"
val dockerLibrary = "lavandadelpatio"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("es.lavanda:lib-common:0.1.8")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("commons-io:commons-io:2.16.1")
    implementation("org.modelmapper:modelmapper:3.2.0")
    implementation("io.minio:minio:8.5.13")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-junit-jupiter:5.6.0")
    compileOnly("org.projectlombok:lombok:1.18.34")

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
jib {
    to {
        image = "lavandadelpatio/indexers"
        tags = setOf("$version")
        auth {
            username = System.getenv("DOCKERHUB_USERNAME")
            password = System.getenv("DOCKERHUB_TOKEN")
        }
    }
    container {
        user = "568"
    }
}
