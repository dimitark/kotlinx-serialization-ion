package com.github.dimitark.kotlix.serialization.ion

import com.amazon.ion.system.IonBinaryWriterBuilder
import com.amazon.ion.system.IonReaderBuilder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import java.io.InputStream
import java.io.OutputStream

@ExperimentalSerializationApi
class Ion(builder: (com.github.dimitark.kotlix.serialization.ion.IonConfig.() -> Unit)? = null) {

    private val config = com.github.dimitark.kotlix.serialization.ion.IonConfig().apply {
        builder?.let { apply(it) }
    }

    inline fun <reified T> encode(value: T, outputStream: OutputStream) = encode(serializer(), value, outputStream)
    inline fun <reified T> decode(inputStream: InputStream): T = decode(serializer(), inputStream)

    fun <T> encode(serializer: SerializationStrategy<T>, value: T, outputStream: OutputStream) {
        IonBinaryWriterBuilder.standard().build(outputStream).use { writer ->
            val encoder = com.github.dimitark.kotlix.serialization.ion.IonEncoder(writer, config)
            encoder.encodeSerializableValue(serializer, value)
        }
    }

    fun <T> decode(deserializer: DeserializationStrategy<T>, inputStream: InputStream): T {
        IonReaderBuilder.standard().build(inputStream).use { reader ->
            val decoder = com.github.dimitark.kotlix.serialization.ion.IonDecoder(reader, config)
            return decoder.decodeSerializableValue(deserializer)
        }
    }
}