plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("maven-publish")
    id("java-library")
    id("io.freefair.lombok") version "8.6"
}

group = "es.lavanda"
version = "0.1.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

tasks.named("bootJar") {
    enabled = false
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/luiscajl/lavanda-del-patio/")
            credentials {
                username = "luiscajl"
                password = (System.getenv("GITHUB_TOKEN") ?: findProperty("GITHUB_TOKEN")).toString()
            }
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework:spring-context")
    implementation("commons-io:commons-io:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

