package dk.thelazydev.kotlix.serialization.ion

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@ExperimentalSerializationApi
class IonConfig(var serializersModule: SerializersModule = EmptySerializersModule)
