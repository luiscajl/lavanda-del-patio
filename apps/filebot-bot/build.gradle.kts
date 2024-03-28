import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.graalvm.buildtools.native") version "0.10.1"
}

group = "es.lavanda"
version = "0.0.77"
val dockerLibrary = "lavandadelpatio"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/Lavanda-del-Patio/*") //FIXME: CHANGE TO LUISCAJL
        credentials {
            username = "luiscajl"
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("es.lavanda:lib-common:0.0.71")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.telegram:telegrambots:6.9.7.1")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor ("org.projectlombok:lombok:1.18.32")

    testImplementation("org.mockito:mockito-junit-jupiter:5.6.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.modelmapper:modelmapper:3.2.0") {
        exclude(group = "com.h2database", module = "h2")
        exclude(group = "org.testng", module = "testng")
    }
}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    kotlinOptions {
//        jvmTarget = "21"
//        freeCompilerArgs = listOf("-Amapstruct.defaultComponentModel=spring")
//    }
//}

//tasks.withType<JavaCompile> {
//    options.compilerArgs.add("-parameters")
//    options.annotationProcessorGeneratedSourcesDirectory.set(file("build/generated/sources/annotationProcessor/java/main"))
//}

//tasks.named("jib") {
//    dependsOn("build")
//    doLast {
//        // Jib plugin configuration can be done here or inside the jib block directly.
//    }
//}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("${dockerLibrary}/${project.name}:${version}")
    environment = mapOf("BP_NATIVE_IMAGE" to "true")
    environment = mapOf("TZ" to "Europe/Madrid")
}


//jib {
//    from {
//        image = "amazoncorretto:21-alpine"
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
//        image = "registry-1.docker.io/lavandadelpatio/filebot-bot"
//        tags = setOf(version.toString(), "${version}-${System.getenv("TRIMMED_SHA")}", "latest")
//    }
//    container {
//        environment = mapOf("TZ" to "Europe/Madrid")
//        user = "568:568"
//    }
//}
