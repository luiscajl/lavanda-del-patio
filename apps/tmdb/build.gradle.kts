plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.graalvm.buildtools.native") version "0.10.1"
}

group = "es.lavanda"
version = "0.0.37"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("es.lavanda:lib-common:0.0.69")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.0")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")
    compileOnly("org.projectlombok:lombok:1.18.30")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-junit-jupiter:5.7.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}


//jib {
//    from {
//        image = "amazoncorretto:17-alpine"
//        platforms {
//            platform {
//                architecture = "amd64"
//                os = "linux"
//            }
//            platform {
//                architecture = "arm64"
//                os = "linux"
//            }
//        }
//    }
//    to {
//        image = "registry-1.docker.io/lavandadelpatio/tmdb"
//        tags = setOf(version.toString(), "\${version}-\${TRIMMED_SHA}", "latest") // Necesitarás ajustar la sustitución de variables
//    }
//    container {
//        environment = mapOf("TZ" to "Europe/Madrid")
//        user = "1000:1000"
//    }
//}
