package com.github.dimitark.kotlix.serialization.ion

import com.github.dimitark.kotlix.serialization.ion.model.Child
import com.github.dimitark.kotlix.serialization.ion.model.Root
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi
@ExperimentalSerializationApi
class NestedObjectsTest {
    private val root = Root("root", listOf(Child(0), Child(1), Child(2)))
    private val ion = Ion()

    @Test
    fun `Simple nested objects encode`() {
        val file = tempFile()
        ion.encode(root, file.outputStream())
        val decoded = ion.decode<Root>(file.inputStream())

        assertEquals(root, decoded)
    }
}
