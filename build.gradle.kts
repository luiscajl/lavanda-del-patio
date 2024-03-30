
allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/Lavanda-del-Patio/*") //FIXME: CHANGE TO LUISCAJL
            credentials {
                username = "luiscajl"
                password = (System.getenv("GITHUB_TOKEN") ?: findProperty("GITHUB_TOKEN")).toString()
            }
        }
    }
}