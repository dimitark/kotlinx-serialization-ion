package com.github.dimitark.kotlix.serialization.ion

import com.amazon.ion.IonWriter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import java.util.*

@ExperimentalSerializationApi
class IonEncoder(private val writer: IonWriter, config: IonConfig) : AbstractEncoder() {
    private val referenceMap = IdentityHashMap<Any, Int>()

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

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) = when(serializer.descriptor.kind) {
        is StructureKind.CLASS,
        is StructureKind.OBJECT -> encodeObject(serializer, value)
        else -> super.encodeSerializableValue(serializer, value)
    }

    private fun <T> encodeObject(serializer: SerializationStrategy<T>, value: T) {
        val existing = referenceMap[value]

        // If we already serialized this object
        // write just the info that we are writing just a reference and the index of the reference
        if (existing != null) {
            encodeBoolean(ReferenceFlag.ObjectReference.flag)
            encodeInt(existing)
            return
        }

        // If it doesn't exist, write the info that we are writing the whole object,
        // delegate the serialization and save the object to the map
        encodeBoolean(ReferenceFlag.WholeObject.flag)
        super.encodeSerializableValue(serializer, value)
        referenceMap[value] = referenceMap.size
    }
}
