package com.github.dimitark.kotlix.serialization.ion

import com.github.dimitark.kotlix.serialization.ion.model.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi
@ExperimentalSerializationApi
class SimpleCollectionTest {

    private val storages = listOf(Storage("uuid", "Storage00", null), Storage("uuid-2", "Storage01", 42))
    private val ion = Ion()

    @Test
    fun `Test nullable Binary encode`() {
        val file = tempFile()
        ion.encode(storages, file.outputStream())
        val decoded = ion.decode<List<Storage>>(file.inputStream())

        assertEquals(storages, decoded)
    }
}
