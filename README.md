# Kotlin Serialization implementation for Amazon Ion's serialization format

Serialization and deserialization to a flat binary file, using Kotlin's serialization framework. 
The binary file doesn't contain the object structure, only the raw data.

## Usage

```kotlin

@Serializable
data class Storage(private val id: String, private val name: String, private val size: Int?)

...

private val ion = Ion()
private val storage = Storage("uuid", "Storage00", null)

// Encode
val file = File(...)
ion.encode(storage, file.outputStream())

// Decode
val file = File(...)
val decoded = Ion.decode<Storage>(file.inputStream())
```
