package com.github.dimitark.kotlix.serialization.ion

import com.github.dimitark.kotlix.serialization.ion.model.ZonedDateTimeStorage
import kotlinx.serialization.ExperimentalSerializationApi
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi
@ExperimentalSerializationApi
class CustomSerializerTest {

    private val ion = Ion()

    @Test
    fun `Test custom serializer non-null encode`() {
        val storage = ZonedDateTimeStorage(ZonedDateTime.now())
        val file = tempFile()
        ion.encode(storage, file.outputStream())
        val decoded = ion.decode<ZonedDateTimeStorage>(file.inputStream())

        assertEquals(storage, decoded)
    }

    @Test
    fun `Test custom serializer nullable encode`() {
        val storage = ZonedDateTimeStorage(null)
        val file = tempFile()
        ion.encode(storage, file.outputStream())
        val decoded = ion.decode<ZonedDateTimeStorage>(file.inputStream())

        assertEquals(storage, decoded)
    }
}
