package dk.thelazydev.kotlix.serialization.ion

import dk.thelazydev.kotlix.serialization.ion.model.ArrayStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class SimpleNonNullArrayTest {

    private val storage = ArrayStorage("my-uuid", intArrayOf(23, 42))
    private val ion = Ion()

    @Test
    fun `Simple non-null JSON array`() {
        val file = tempFile()
        ion.encodeJson(storage, file.outputStream())
        val decoded = ion.decode<ArrayStorage>(file.inputStream())

        assertEquals(storage, decoded)
    }

    @Test
    fun `Simple non-null Binary array`() {
        val file = tempFile()
        ion.encodeBinary(storage, file.outputStream())
        val decoded = ion.decode<ArrayStorage>(file.inputStream())

        assertEquals(storage, decoded)
    }
}