package dk.thelazydev.kotlix.serialization.ion

import kotlinx.serialization.descriptors.SerialDescriptor

class Structure(val descriptor: SerialDescriptor, var elementIndex: Int = 0)
