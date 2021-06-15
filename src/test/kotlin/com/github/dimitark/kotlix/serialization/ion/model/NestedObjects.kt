package com.github.dimitark.kotlix.serialization.ion.model

import kotlinx.serialization.Serializable

@Serializable
data class Root(val id: String, val children: List<Child>)

@Serializable
data class Child(val id: Int)
