package dk.thelazydev.kotlix.serialization.ion

import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
fun Ion(builder: (IonConfig.() -> Unit)? = null): IonSerializer {
    val config = IonConfig()
    builder?.let { config.apply(it) }
    return IonSerializer(config)
}
