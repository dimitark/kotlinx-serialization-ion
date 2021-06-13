package dk.thelazydev.kotlix.serialization.ion

import com.amazon.ion.IonType
import com.amazon.ion.IonWriter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import java.util.*

@ExperimentalSerializationApi
class IonEncoder(private val writer: IonWriter, config: IonConfig) : AbstractEncoder() {

    private var structureDescriptors = Stack<Structure>()

    override val serializersModule = config.serializersModule

    override fun encodeBoolean(value: Boolean) = writer.writeField { writeBool(value) }
    override fun encodeByte(value: Byte) = writer.writeField { writeInt(value.toLong()) }
    override fun encodeShort(value: Short) = writer.writeField { writeInt(value.toLong()) }
    override fun encodeInt(value: Int) = writer.writeField { writeInt(value.toLong()) }
    override fun encodeLong(value: Long) = writer.writeField { writeInt(value) }
    override fun encodeFloat(value: Float) = writer.writeField { writeFloat(value.toDouble()) }
    override fun encodeDouble(value: Double) = writer.writeField { writeFloat(value) }
    override fun encodeChar(value: Char) = writer.writeField { writeString(value.toString()) }
    override fun encodeString(value: String) = writer.writeField { writeString(value) }
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = writer.writeField { writeInt(index.toLong()) }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        // If we are already in a struct, we need to set the field name
        structureDescriptors.peekOrNull()?.also {
            writer.setFieldName(it.descriptor.getElementName(it.elementIndex))
            it.elementIndex++
        }

        writer.stepIn(IonType.STRUCT)
        structureDescriptors.add(Structure(descriptor))

        writer.setFieldName("s")
        writer.writeInt(collectionSize.toLong())

        writer.setFieldName("elements")
        writer.stepIn(IonType.LIST)
        return this
    }

    override fun encodeNull() = encodeBoolean(false)
    override fun encodeNotNullMark() = writer.writeField(false) { writeBool(true) }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {

        // If we are already in a struct, we need to set the field name
        structureDescriptors.peekOrNull()?.also {
            if (it.descriptor.kind == StructureKind.CLASS || it.descriptor.kind == PolymorphicKind.OPEN) {
                writer.setFieldName(it.descriptor.getElementName(it.elementIndex))
                it.elementIndex++
            }
        }

        writer.stepIn(IonType.STRUCT)
        structureDescriptors.add(Structure(descriptor))

        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        structureDescriptors.pop()

        writer.stepOut()
        if (descriptor.kind == StructureKind.LIST) { writer.stepOut() }
    }

    private fun IonWriter.writeField(increaseElementIndex: Boolean = true, block: IonWriter.() -> Unit) {
        structureDescriptors.peekOrNull()?.also {
            if (it.descriptor.kind == StructureKind.CLASS || it.descriptor.kind == PolymorphicKind.OPEN) {
                setFieldName(it.descriptor.getElementName(it.elementIndex))
                if (increaseElementIndex) { it.elementIndex++ }
            }
        }

        block.invoke(this)
    }
}
