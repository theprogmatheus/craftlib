plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.8" apply false
}

group = "com.github.theprogmatheus.craftlib"
version = "1.0-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

allprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}