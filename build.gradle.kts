
allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/luiscajl/*")
            credentials {
                username = "luiscajl"
                password = (System.getenv("GITHUB_TOKEN") ?: findProperty("GITHUB_TOKEN")).toString()
            }
        }
    }
}