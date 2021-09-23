package com.github.dimitark.kotlix.serialization.ion

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi
@ExperimentalSerializationApi
class StructureHashRetrievalTest {

    @Test
    fun `Test structure hash retrieval`() {
        val ion = Ion()
        val hash = ion.structureHash<SimpleStorage>()
        assertEquals(2069011805, hash)
    }
}

@Serializable
class SimpleStorage(val first: String, val second: String)
