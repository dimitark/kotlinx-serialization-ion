package com.github.dimitark.kotlix.serialization.ion

import com.github.dimitark.kotlix.serialization.ion.model.NullableArrayStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi
@ExperimentalSerializationApi
class SimpleNullableArrayTest {

    private val storage = NullableArrayStorage("my-uuid", arrayOf(23, null, 42))
    private val ion = Ion()

    @Test
    fun `Simple non-null array`() {
        val file = tempFile()
        ion.encode(storage, file.outputStream())
        val decoded = ion.decode<NullableArrayStorage>(file.inputStream())

        assertEquals(storage, decoded)
    }
}
