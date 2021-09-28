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
import java.math.BigInteger
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import kotlin.reflect.typeOf


@ExperimentalStdlibApi
@ExperimentalSerializationApi
class Ion(builder: (IonConfig.() -> Unit)? = null) {
    private val contextualDescriptors = setOf(SerialKind.CONTEXTUAL, PolymorphicKind.OPEN)

    private val config = IonConfig().apply {
        builder?.let { apply(it) }
    }

    inline fun <reified T> encode(value: T, outputStream: OutputStream) = encode(serializer(), value, outputStream)
    inline fun <reified T> decode(inputStream: InputStream): T = decode(serializer(), inputStream)
    inline fun <reified T> structureHash(): String = structureHash(serializer(typeOf<T>()) as DeserializationStrategy<T>)

    fun <T> encode(serializer: SerializationStrategy<T>, value: T, outputStream: OutputStream) {
        IonBinaryWriterBuilder.standard().build(outputStream).use { writer ->
            // Write the structure hash first, so we can check the integrity on decode
            val structureHash = calculateStructureHash(serializer.descriptor)
            writer.writeString(structureHash)

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
            val fileStructureHash = reader.stringValue()

            if (structureHash != fileStructureHash) {
                throw IntegrityCheckException()
            }

            val decoder = IonDecoder(reader, config)
            return decoder.decodeSerializableValue(deserializer)
        }
    }

    fun <T> structureHash(deserializer: DeserializationStrategy<T>): String = calculateStructureHash(deserializer.descriptor)

    private fun calculateStructureHash(descriptor: SerialDescriptor): String {
        // String representation of the descriptors
        val descriptors = mutableListOf<String>()
        val contextDescriptors = TreeSet<String>()

        // Once we visit a contextual descriptor, we put it here, so we don't re-visit it
        // because we might end up in a infinite recursion
        val visitedContextualDescriptors = mutableSetOf<SerialDescriptor>()

        visitDescriptors(descriptor, descriptors, contextDescriptors, visitedContextualDescriptors, false)
        return (descriptors to contextDescriptors).md5()
    }

    private fun visitDescriptors(descriptor: SerialDescriptor,
                                 descriptors: MutableList<String>,
                                 contextDescriptors: MutableSet<String>,
                                 visitedContextualDescriptors: MutableSet<SerialDescriptor>,
                                 isContextualDescriptor: Boolean) {

        when(isContextualDescriptor) {
            true -> contextDescriptors.add(descriptor.toString())
            false -> descriptors.add(descriptor.toString())
        }

        // For Context descriptor, we need to additionally get the child descriptors from the context
        if (descriptor.kind in contextualDescriptors && descriptor !in visitedContextualDescriptors) {
            visitedContextualDescriptors.add(descriptor)

            config.serializersModule.getContextualDescriptor(descriptor)?.let {
                visitDescriptors(it, descriptors, contextDescriptors, visitedContextualDescriptors, true)
            }

            config.serializersModule.getPolymorphicDescriptors(descriptor).forEach {
                visitDescriptors(it, descriptors, contextDescriptors, visitedContextualDescriptors, true)
            }
        }

        for (i in 0 until descriptor.elementsCount) {
            visitDescriptors(descriptor.getElementDescriptor(i), descriptors, contextDescriptors, visitedContextualDescriptors, false)
        }
    }

    private fun Pair<List<String>, Set<String>>.md5(): String {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(byteArray())
        val md5 = digest.digest()

        return BigInteger(1, md5).toString(16)
    }

    private fun Pair<List<String>, Set<String>>.byteArray(): ByteArray {
        val stringBuffer = StringBuffer()
        first.forEach { stringBuffer.append(it) }
        second.forEach { stringBuffer.append(it) }

        val encoder = StandardCharsets.UTF_8.newEncoder()
        val charBuffer = CharBuffer.wrap(stringBuffer)
        val byteBuffer = encoder.encode(charBuffer)
        return byteBuffer.array()
    }
}
