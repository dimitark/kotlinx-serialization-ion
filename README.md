# Kotlin Serialization implementation for Amazon Ion's serialization format

## Usage

```kotlin

@Serializable
data class Storage(private val id: String, private val name: String, private val size: Int?)

...

private val storage = Storage("uuid", "Storage00", null)

// Encode JSON
val file = File(...)
Ion.encodeJson(storage, file.outputStream())

// Encode Binary
val file = File(...)
Ion.encodeBinary(storage, file.outputStream())

// Decode
val file = File(...)
val decoded = Ion.decode<Storage>(file.inputStream())
```