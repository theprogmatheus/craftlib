plugins {
    id("java")
    id("com.gradleup.shadow")
}

group = "com.github.theprogmatheus.craftlib"
version = "1.0.0-SNAPSHOT"

val lombokVersion = "1.18.38"

dependencies {
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    implementation("org.apache.maven.resolver:maven-resolver-impl:1.9.15")
    implementation("org.apache.maven.resolver:maven-resolver-api:1.9.15")
    implementation("org.apache.maven.resolver:maven-resolver-util:1.9.15")
    implementation("org.apache.maven.resolver:maven-resolver-connector-basic:1.9.15")
    implementation("org.apache.maven.resolver:maven-resolver-transport-http:1.9.15")
    implementation("org.apache.maven.resolver:maven-resolver-transport-file:1.9.15")
    implementation("org.apache.maven.resolver:maven-resolver-supplier:1.9.15")
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
