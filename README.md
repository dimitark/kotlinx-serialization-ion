# Kotlin Serialization implementation for Amazon Ion

Serialization and deserialization to a flat binary file, using Amazon Ion.

The binary file doesn't contain the object structure, only the raw data. 

This implementation preserves the objects' references.

For example, if you have a list, where all the elements are the same object, that object will be serialized only once,
and when deserializing, only one instance of that object will exist. 

For more info, check the `dk.thelazydev.kotlix.serialization.ion.SimpleLookupTableTest` test.

## Adding the dependencies to your Gradle project

```kotlin
plugins {
    // ....
    kotlin("plugin.serialization") version "1.5.10"
    // ...
}

repositories {
    // ...
    maven { url = uri("https://jitpack.io") }
    // ...
}

dependencies {
    // ...
    implementation("com.github.dimitark:kotlinx-serialization-ion:0.1.6")
    // ...
}

```

## Usage

```kotlin

@Serializable
data class Storage(private val id: String, private val name: String, private val size: Int?)

// ...

private val ion = Ion()
private val storage = Storage("uuid", "Storage00", null)

// Encode
val file = File("...")
ion.encode(storage, file.outputStream())

// Decode
val file = File("...")
val decoded = try { ion.decode<Storage>(file.inputStream()) } catch(e: IntegrityCheckException) { null }
```
