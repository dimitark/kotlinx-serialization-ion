plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
}

group = "com.github.dimitark"
version = "0.1.14"

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.dimitark"
            artifactId = "kotlinx-serialization-ion"
            version = "0.1.14"

            from(components["java"])
        }
    }
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

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "17"
    targetCompatibility = "17"
}
