package dk.thelazydev.kotlix.serialization.ion.model

import kotlinx.serialization.Serializable

@Serializable
data class Storage(val id: String, private val name: String, private val size: Int?)
