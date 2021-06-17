package com.github.dimitark.kotlix.serialization.ion

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class HandWrittenCompositeTest {

    val storage = HandWrittenStorage(HandWrittenColor(42))
    val ion = Ion()

    @Test
    fun `Test hand written composite serialization`() {
        val file = tempFile()
        ion.encode(storage, file.outputStream())
        val decoded = ion.decode<HandWrittenStorage>(file.inputStream())

        assertEquals(storage, decoded)
    }
}

@Serializable
data class HandWrittenStorage(val mainColor: HandWrittenColor)

@Serializable(with = ColorAsObjectSerializer::class)
data class HandWrittenColor(val rgb: Int)

object ColorAsObjectSerializer: KSerializer<HandWrittenColor> {

    override val descriptor = buildClassSerialDescriptor("Color") {
        element<Int>("r")
        element<Int>("g")
        element<Int>("b")
    }

    override fun serialize(encoder: Encoder, value: HandWrittenColor) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, (value.rgb shr 16) and 0xff)
            encodeIntElement(descriptor, 1, (value.rgb shr 8) and 0xff)
            encodeIntElement(descriptor, 2, value.rgb and 0xff)
        }
    }

    override fun deserialize(decoder: Decoder): HandWrittenColor {
        var r = -1
        var g = -1
        var b = -1

        decoder.decodeStructure(descriptor) {
            r = decodeIntElement(descriptor, 0)
            g = decodeIntElement(descriptor, 1)
            b = decodeIntElement(descriptor, 2)
        }

        return HandWrittenColor((r shl 16) or (g shl 8) or b)
    }
}
