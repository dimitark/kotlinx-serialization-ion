package com.github.dimitark.kotlix.serialization.ion

import com.amazon.ion.system.IonBinaryWriterBuilder
import com.amazon.ion.system.IonReaderBuilder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.serializer
import java.io.InputStream
import java.io.OutputStream
import kotlin.reflect.typeOf


@ExperimentalStdlibApi
@ExperimentalSerializationApi
class Ion(builder: (IonConfig.() -> Unit)? = null) {
    private val config = IonConfig().apply {
        builder?.let { apply(it) }
    }

    inline fun <reified T> encode(value: T, outputStream: OutputStream) = encode(serializer(), value, outputStream)
    inline fun <reified T> decode(inputStream: InputStream): T = decode(serializer(), inputStream)
    inline fun <reified T> structureHash(): Int = structureHash(serializer(typeOf<T>()) as DeserializationStrategy<T>)

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

    fun <T> structureHash(deserializer: DeserializationStrategy<T>): Int = calculateStructureHash(deserializer.descriptor)

    private fun calculateStructureHash(descriptor: SerialDescriptor): Int = descriptor.hashCode()
}
