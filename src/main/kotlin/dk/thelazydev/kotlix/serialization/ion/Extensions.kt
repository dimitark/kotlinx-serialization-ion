package dk.thelazydev.kotlix.serialization.ion

import java.util.*

fun <T> Stack<T>.peekOrNull() = try { peek() } catch (e: EmptyStackException) { null }
