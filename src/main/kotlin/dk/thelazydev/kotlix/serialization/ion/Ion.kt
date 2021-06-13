package dk.thelazydev.kotlix.serialization.ion

import com.amazon.ion.system.IonBinaryWriterBuilder
import com.amazon.ion.system.IonReaderBuilder
import com.amazon.ion.system.IonTextWriterBuilder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import java.io.InputStream
import java.io.OutputStream


@ExperimentalSerializationApi
fun Ion(builder: (IonConfig.() -> Unit)? = null): IonSerializer {
    val config = IonConfig()
    builder?.let { config.apply(it) }
    return IonSerializer(config)
}

@ExperimentalSerializationApi
class IonConfig(var serializersModule: SerializersModule = EmptySerializersModule)

@ExperimentalSerializationApi
class IonSerializer(private val config: IonConfig) {

    inline fun <reified T> encodeBinary(value: T, outputStream: OutputStream) = encode(serializer(), value, outputStream, EncodingType.Binary)
    inline fun <reified T> encodeJson(value: T, outputStream: OutputStream) = encode(serializer(), value, outputStream, EncodingType.Json)
    inline fun <reified T> decode(inputStream: InputStream): T = decode(serializer(), inputStream)

    fun <T> encode(serializer: SerializationStrategy<T>, value: T, outputStream: OutputStream, type: EncodingType) {
        val ionWriter = when(type) {
            EncodingType.Binary -> IonBinaryWriterBuilder.standard().build(outputStream)
            EncodingType.Json -> IonTextWriterBuilder.standard().build(outputStream)
        }

        ionWriter.use { writer ->
            val encoder = IonEncoder(writer, config)
            encoder.encodeSerializableValue(serializer, value)
        }
    }

    fun <T> decode(deserializer: DeserializationStrategy<T>, inputStream: InputStream): T {
        IonReaderBuilder.standard().build(inputStream).use { reader ->
            reader.next()
            val decoder = IonDecoder(reader, config)
            return decoder.decodeSerializableValue(deserializer)
        }
    }

    enum class EncodingType { Json, Binary }
}
