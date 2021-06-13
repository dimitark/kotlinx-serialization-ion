package dk.thelazydev.kotlix.serialization.ion

import dk.thelazydev.kotlix.serialization.ion.model.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class SimpleNullableTest {

    private val storage = Storage("uuid", "Storage00", null)
    private val ion = Ion()

    @Test
    fun `Test nullable JSON encode`() {
        val file = tempFile()
        ion.encodeJson(storage, file.outputStream())
        val decoded = ion.decode<Storage>(file.inputStream())

        assertEquals(storage, decoded)
    }

    @Test
    fun `Test nullable Binary encode`() {
        val file = tempFile()
        ion.encodeBinary(storage, file.outputStream())
        val decoded = ion.decode<Storage>(file.inputStream())

        assertEquals(storage, decoded)
    }
}
