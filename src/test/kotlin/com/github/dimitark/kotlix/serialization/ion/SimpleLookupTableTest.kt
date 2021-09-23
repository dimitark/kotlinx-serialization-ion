package com.github.dimitark.kotlix.serialization.ion

import com.github.dimitark.kotlix.serialization.ion.model.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

@ExperimentalStdlibApi
@ExperimentalSerializationApi
class SimpleLookupTableTest {
    private val sharedObject = Storage("shared", "Shared Storage", 100)
    private val storages = listOf(
        sharedObject,
        Storage("uuid", "Storage00", null),
        sharedObject,
        Storage("uuid-2", "Storage01", 42)
    )

    private val ion = Ion()

    @Test
    fun `Test a simple lookup table`() {
        val file = tempFile()
        ion.encode(storages, file.outputStream())
        val decoded = ion.decode<List<Storage>>(file.inputStream())

        assertEquals(storages, decoded)

        assertSame(decoded[0], decoded[2])
        assertNotSame(decoded[0], decoded[1])
        assertNotSame(decoded[0], decoded[3])

        assertNotSame(decoded[1], decoded[2])
        assertNotSame(decoded[1], decoded[3])

        assertNotSame(decoded[2], decoded[3])
    }
}
