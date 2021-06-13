package dk.thelazydev.kotlix.serialization.ion.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Person {
    val id: String
    val name: String
    val age: Int?
}

@Serializable
@SerialName("student")
data class Student(override val id: String, override val name: String, override val age: Int?, private val course: String) : Person

@Serializable
@SerialName("employee")
data class Employee(override val id: String, override val name: String, override val age: Int?, private val position: EmployeePosition) : Person

enum class EmployeePosition { Junior, Senior }
