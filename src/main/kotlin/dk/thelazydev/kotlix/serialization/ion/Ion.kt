package dk.thelazydev.kotlix.serialization.ion

import com.amazon.ion.system.IonBinaryWriterBuilder
import com.amazon.ion.system.IonReaderBuilder
import com.amazon.ion.system.IonTextWriterBuilder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import java.io.InputStream
import java.io.OutputStream

@ExperimentalSerializationApi
object Ion {
    inline fun <reified T> encodeBinary(value: T, outputStream: OutputStream) = encode(serializer(), value, outputStream, EncodingType.Binary)
    inline fun <reified T> encodeJson(value: T, outputStream: OutputStream) = encode(serializer(), value, outputStream, EncodingType.Json)
    inline fun <reified T> decode(inputStream: InputStream): T = decode(serializer(), inputStream)

    fun <T> encode(serializer: SerializationStrategy<T>, value: T, outputStream: OutputStream, type: EncodingType) {
        val ionWriter = when(type) {
            EncodingType.Binary -> IonBinaryWriterBuilder.standard().build(outputStream)
            EncodingType.Json -> IonTextWriterBuilder.standard().build(outputStream)
        }

        ionWriter.use { writer ->
            val encoder = IonEncoder(writer)
            encoder.encodeSerializableValue(serializer, value)
        }
    }

    fun <T> decode(deserializer: DeserializationStrategy<T>, inputStream: InputStream): T {
        IonReaderBuilder.standard().build(inputStream).use { reader ->
            reader.next()
            val decoder = IonDecoder(reader)
            return decoder.decodeSerializableValue(deserializer)
        }
    }

    enum class EncodingType { Json, Binary }
}
