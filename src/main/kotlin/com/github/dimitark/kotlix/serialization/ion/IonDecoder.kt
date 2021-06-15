package com.github.dimitark.kotlix.serialization.ion

import com.amazon.ion.IonReader
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder

@ExperimentalSerializationApi
class IonDecoder(private val reader: IonReader, config: IonConfig) : AbstractDecoder() {
    private val objectPool = mutableListOf<Any>()

    override val serializersModule = config.serializersModule

    override fun decodeBoolean(): Boolean = reader.readField { booleanValue() }
    override fun decodeByte(): Byte = reader.readField { intValue().toByte() }
    override fun decodeShort(): Short = reader.readField { intValue().toShort() }
    override fun decodeInt(): Int = reader.readField { intValue() }
    override fun decodeLong(): Long = reader.readField { longValue() }
    override fun decodeFloat(): Float = reader.readField { doubleValue().toFloat() }
    override fun decodeDouble(): Double = reader.readField { doubleValue() }
    override fun decodeChar(): Char = reader.readField { stringValue().single() }
    override fun decodeString(): String = reader.readField { stringValue() }
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = reader.readField { intValue() }

    // Not called for sequential decoders
    override fun decodeElementIndex(descriptor: SerialDescriptor) = 0
    override fun decodeNotNullMark(): Boolean = decodeBoolean()
    override fun decodeSequentially(): Boolean = true
    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = decodeInt()

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>) = when(deserializer.descriptor.kind) {
        is StructureKind.CLASS,
        is StructureKind.OBJECT,
        is SerialKind.CONTEXTUAL,
        is PolymorphicKind.SEALED,
        is PolymorphicKind.OPEN -> decodeObject(deserializer)
        else -> super.decodeSerializableValue(deserializer)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> decodeObject(deserializer: DeserializationStrategy<T>): T {
        // Check to see if we are reading the whole object or just a reference
        val referenceFlag = decodeBoolean().toReferenceFlag()

        // Find the reference in the pool and return that
        if (referenceFlag == ReferenceFlag.ObjectReference) {
            return objectPool[decodeInt()] as T
        }

        // Deserialize and put it in the pool
        val value = super.decodeSerializableValue(deserializer)
        objectPool.add(value as Any)
        return value
    }

    private fun <T> IonReader.readField(block: IonReader.() -> T): T {
        next()
        return block.invoke(this)
    }
}
