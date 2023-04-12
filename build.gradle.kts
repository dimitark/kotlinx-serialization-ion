plugins {
    `java-library`
    id("maven")
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
}

group = "com.github.dimitark"
version = "0.1.12"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.amazon.ion:ion-java:1.8.1")

    api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
