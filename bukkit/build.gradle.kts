plugins {
    id("java")
    id("com.gradleup.shadow")
}

group = "com.github.theprogmatheus.craftlib"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "spigot-api"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

val lombokVersion = "1.18.38"

dependencies {
    implementation(project(":core"))
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT@jar")


    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
}

tasks.shadowJar {
    archiveBaseName.set("CraftLib")
    archiveClassifier.set("Bukkit")
    archiveVersion.set(project.version.toString())

    dependsOn(":core:jar")
}
