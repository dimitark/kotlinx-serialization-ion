package dk.thelazydev.kotlix.serialization.ion

import dk.thelazydev.kotlix.serialization.ion.model.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class SimpleNullableTest {

    private val storage = Storage("uuid", "Storage00", null)

    @Test
    fun `Test nullable JSON encode`() {
        val file = tempFile()
        Ion.encodeJson(storage, file.outputStream())
        val decoded = Ion.decode<Storage>(file.inputStream())

        assertEquals(storage, decoded)
    }

    @Test
    fun `Test nullable Binary encode`() {
        val file = tempFile()
        Ion.encodeBinary(storage, file.outputStream())
        val decoded = Ion.decode<Storage>(file.inputStream())

        assertEquals(storage, decoded)
    }
}
