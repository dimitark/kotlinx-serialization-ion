package com.github.dimitark.kotlix.serialization.ion

import com.amazon.ion.system.IonBinaryWriterBuilder
import com.amazon.ion.system.IonReaderBuilder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.serializer
import java.io.InputStream
import java.io.OutputStream
import java.util.*

@ExperimentalSerializationApi
class Ion(builder: (IonConfig.() -> Unit)? = null) {
    private val contextualDescriptors = setOf(SerialKind.CONTEXTUAL, PolymorphicKind.OPEN)

    private val config = IonConfig().apply {
        builder?.let { apply(it) }
    }

    inline fun <reified T> encode(value: T, outputStream: OutputStream) = encode(serializer(), value, outputStream)
    inline fun <reified T> decode(inputStream: InputStream): T = decode(serializer(), inputStream)

    fun <T> encode(serializer: SerializationStrategy<T>, value: T, outputStream: OutputStream) {
        IonBinaryWriterBuilder.standard().build(outputStream).use { writer ->
            // Write the structure hash first, so we can check the integrity on decode
            val structureHash = calculateStructureHash(serializer.descriptor)
            writer.writeInt(structureHash.toLong())

            val encoder = IonEncoder(writer, config)
            encoder.encodeSerializableValue(serializer, value)
        }
    }

    fun <T> decode(deserializer: DeserializationStrategy<T>, inputStream: InputStream): T {
        IonReaderBuilder.standard().build(inputStream).use { reader ->
            // Check if the type's T structure hash matches with the one in the file
            // If not - throw an exception
            val structureHash = calculateStructureHash(deserializer.descriptor)

            reader.next()
            val fileStructureHash = reader.intValue()

            if (structureHash != fileStructureHash) {
                throw IntegrityCheckException()
            }

            val decoder = IonDecoder(reader, config)
            return decoder.decodeSerializableValue(deserializer)
        }
    }

    private fun calculateStructureHash(descriptor: SerialDescriptor): Int {
        // String representation of the descriptors
        val descriptors = Stack<String>()

        // Once we visit a contextual descriptor, we put it here, so we don't re-visit it
        // because we might end up in a infinite recursion
        val visitedContextualDescriptors = mutableSetOf<SerialDescriptor>()

        visitDescriptors(descriptor, descriptors, visitedContextualDescriptors)
        return descriptors.hashCode()
    }

    private fun visitDescriptors(descriptor: SerialDescriptor, descriptors: Stack<String>, visitedContextualDescriptors: MutableSet<SerialDescriptor>) {
        descriptors.push(descriptor.toString())

        // For Context descriptor, we need to additionally get the child descriptors from the context
        if (descriptor.kind in contextualDescriptors && descriptor !in visitedContextualDescriptors) {
            visitedContextualDescriptors.add(descriptor)

            config.serializersModule.getContextualDescriptor(descriptor)?.let { visitDescriptors(it, descriptors, visitedContextualDescriptors) }
            config.serializersModule.getPolymorphicDescriptors(descriptor).forEach { visitDescriptors(it, descriptors, visitedContextualDescriptors) }
        }

        for (i in 0 until descriptor.elementsCount) {
            visitDescriptors(descriptor.getElementDescriptor(i), descriptors, visitedContextualDescriptors)
        }
    }
}
