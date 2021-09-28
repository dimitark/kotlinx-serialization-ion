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
        assertEquals("cb5b67a7a1f22f8690e49f6b55fce022", hash)
    }
}

@Serializable
class SimpleStorage(val first: String, val second: String)
