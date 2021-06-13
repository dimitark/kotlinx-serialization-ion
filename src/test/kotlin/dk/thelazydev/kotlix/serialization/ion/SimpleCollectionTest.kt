package dk.thelazydev.kotlix.serialization.ion

import dk.thelazydev.kotlix.serialization.ion.model.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class SimpleCollectionTest {

    private val storages = listOf(Storage("uuid", "Storage00", null), Storage("uuid-2", "Storage01", 42))
    private val ion = Ion()

    @Test
    fun `Test nullable JSON encode`() {
        val file = tempFile()
        ion.encodeJson(storages, file.outputStream())
        val decoded = ion.decode<List<Storage>>(file.inputStream())

        assertEquals(storages, decoded)
    }

    @Test
    fun `Test nullable Binary encode`() {
        val file = tempFile()
        ion.encodeBinary(storages, file.outputStream())
        val decoded = ion.decode<List<Storage>>(file.inputStream())

        assertEquals(storages, decoded)
    }
}
