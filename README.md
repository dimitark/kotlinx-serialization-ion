# Kotlin Serialization implementation for Amazon Ion

Serialization and deserialization to a flat binary file, using Kotlin's serialization framework. 

This implementation preserves the object references.
For example, if you have a list, where the all elements are the same object, that object will be serialized only once,
and when deserializing, it will have a single instance.

```kotlin
private val sharedObject = Storage("shared", "Shared Storage", 100)
private val storages = listOf(
    sharedObject,
    Storage("uuid", "Storage00", null),
    sharedObject,
    Storage("uuid-2", "Storage01", 42)
)

private val ion = Ion()

val file = File("...")
ion.encode(storages, file.outputStream())
val decoded = ion.decode<List<Storage>>(file.inputStream())

decoded[0] === decoded[2] // returns true
decoded[0] === decoded[1] // returns false
```


The binary file doesn't contain the object structure, only the raw data.

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
val decoded = Ion.decode<Storage>(file.inputStream())
```
