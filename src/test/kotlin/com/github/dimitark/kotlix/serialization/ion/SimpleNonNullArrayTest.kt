package com.github.dimitark.kotlix.serialization.ion

import com.github.dimitark.kotlix.serialization.ion.model.ArrayStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi
@ExperimentalSerializationApi
class SimpleNonNullArrayTest {

    private val storage = ArrayStorage("my-uuid", intArrayOf(23, 42))
    private val ion = Ion()

    @Test
    fun `Simple non-null Binary array`() {
        val file = tempFile()
        ion.encode(storage, file.outputStream())
        val decoded = ion.decode<ArrayStorage>(file.inputStream())

        assertEquals(storage, decoded)
    }
}
