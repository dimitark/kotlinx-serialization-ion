package dk.thelazydev.kotlix.serialization.ion

import com.amazon.ion.IonWriter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder

@ExperimentalSerializationApi
class IonEncoder(private val writer: IonWriter, config: IonConfig) : AbstractEncoder() {
    override val serializersModule = config.serializersModule

    override fun encodeBoolean(value: Boolean) = writer.writeBool(value)
    override fun encodeByte(value: Byte) = writer.writeInt(value.toLong())
    override fun encodeShort(value: Short) = writer.writeInt(value.toLong())
    override fun encodeInt(value: Int) = writer.writeInt(value.toLong())
    override fun encodeLong(value: Long) = writer.writeInt(value)
    override fun encodeFloat(value: Float) = writer.writeFloat(value.toDouble())
    override fun encodeDouble(value: Double) = writer.writeFloat(value)
    override fun encodeChar(value: Char) = writer.writeString(value.toString())
    override fun encodeString(value: String) = writer.writeString(value)
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = writer.writeInt(index.toLong())

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        encodeInt(collectionSize)
        return this
    }

    override fun encodeNull() = encodeBoolean(false)
    override fun encodeNotNullMark() = encodeBoolean(true)
}
