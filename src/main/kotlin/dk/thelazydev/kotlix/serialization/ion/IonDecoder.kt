package dk.thelazydev.kotlix.serialization.ion

import com.amazon.ion.IonReader
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder

@ExperimentalSerializationApi
class IonDecoder(private val reader: IonReader, private val config: IonConfig, var elementsCount: Int = 0) : AbstractDecoder() {
    override val serializersModule = config.serializersModule

    override fun decodeBoolean(): Boolean = reader.readField { booleanValue() }
    override fun decodeByte(): Byte = reader.readField {intValue(). toByte() }
    override fun decodeShort(): Short = reader.readField {intValue(). toShort() }
    override fun decodeInt(): Int = reader.readField { intValue() }
    override fun decodeLong(): Long = reader.readField { longValue() }
    override fun decodeFloat(): Float = reader.readField {doubleValue(). toFloat() }
    override fun decodeDouble(): Double = reader.readField { doubleValue() }
    override fun decodeChar(): Char = reader.readField {stringValue(). single() }
    override fun decodeString(): String = reader.readField { stringValue() }
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = reader.readField { intValue() }

    // Not called for sequential decoders
    override fun decodeElementIndex(descriptor: SerialDescriptor) = 0
    override fun decodeNotNullMark(): Boolean = decodeBoolean()
    override fun decodeSequentially(): Boolean = true

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = IonDecoder(reader, config, descriptor.elementsCount)
    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = decodeInt().also { elementsCount = it }

    private fun <T> IonReader.readField(block: IonReader.() -> T): T {
        next()
        return block.invoke(this)
    }
}
