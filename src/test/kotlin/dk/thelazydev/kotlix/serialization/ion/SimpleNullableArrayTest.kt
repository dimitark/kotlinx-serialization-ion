package dk.thelazydev.kotlix.serialization.ion

import dk.thelazydev.kotlix.serialization.ion.model.NullableArrayStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class SimpleNullableArrayTest {

    private val storage = NullableArrayStorage("my-uuid", arrayOf(23, null, 42))

    @Test
    fun `Simple non-null JSON array`() {
        val file = tempFile()
        Ion.encodeJson(storage, file.outputStream())
        val decoded = Ion.decode<NullableArrayStorage>(file.inputStream())

        assertEquals(storage, decoded)
    }

    @Test
    fun `Simple non-null Binary array`() {
        val file = tempFile()
        Ion.encodeBinary(storage, file.outputStream())
        val decoded = Ion.decode<NullableArrayStorage>(file.inputStream())

        assertEquals(storage, decoded)
    }
}