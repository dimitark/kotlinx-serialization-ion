plugins {
    `java-library`
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
}

group = "org.example"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.1")
    implementation("com.amazon.ion:ion-java:1.8.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
