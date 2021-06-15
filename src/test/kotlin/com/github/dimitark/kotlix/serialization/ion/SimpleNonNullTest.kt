package com.github.dimitark.kotlix.serialization.ion

import com.github.dimitark.kotlix.serialization.ion.model.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class SimpleNonNullTest {

    private val storage = Storage("uuid", "Storage00", 42)
    private val ion = Ion()

    @Test
    fun `Test non-null encode`() {
        val file = tempFile()
        ion.encode(storage, file.outputStream())
        val decoded = ion.decode<Storage>(file.inputStream())

        assertEquals(storage, decoded)
    }
}
