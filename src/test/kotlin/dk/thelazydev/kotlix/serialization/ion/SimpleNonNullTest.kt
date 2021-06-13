package dk.thelazydev.kotlix.serialization.ion

import dk.thelazydev.kotlix.serialization.ion.model.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class SimpleNonNullTest {

    private val storage = Storage("uuid", "Storage00", 42)

    @Test
    fun `Test non-null JSON encode`() {
        val file = tempFile()
        Ion.encodeJson(storage, file.outputStream())
        val decoded = Ion.decode<Storage>(file.inputStream())

        assertEquals(storage, decoded)
    }

    @Test
    fun `Test non-null Binary encode`() {
        val file = tempFile()
        Ion.encodeBinary(storage, file.outputStream())
        val decoded = Ion.decode<Storage>(file.inputStream())

        assertEquals(storage, decoded)
    }
}
