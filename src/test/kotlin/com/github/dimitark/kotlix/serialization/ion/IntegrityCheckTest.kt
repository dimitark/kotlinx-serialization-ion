package com.github.dimitark.kotlix.serialization.ion

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.test.Test
import kotlin.test.assertTrue

@ExperimentalSerializationApi
class IntegrityCheckTest {

    @Test
    fun `Test simple integrity check`() {
        val ion = Ion()
        val db = SimpleDB("First", "Second")

        val file = tempFile()
        ion.encode(db, file.outputStream())

        val integrityFailed = try {
            ion.decode<FlippedSimpleDB>(file.inputStream())
            false
        } catch (e: IntegrityCheckException) {
            true
        }

        assertTrue(integrityFailed)
    }

    @Test
    fun `Test the integrity check with polymorphism`() {
        val serializationIon = Ion {
            serializersModule = SerializersModule {
                polymorphic(Person::class) {
                    subclass(Student::class)
                    subclass(Employee::class)
                }
            }
        }

        val deserializationIon = Ion {
            serializersModule = SerializersModule {
                polymorphic(Person::class) {
                    subclass(FlippedParamsStudent::class)
                    subclass(Employee::class)
                }
            }
        }

        val db = PolymorphicDB(arrayOf(
            Student("s00", "First name", "Last name"),
            Employee(Student("s01", "First Name", "Last name"), 0)
        ))

        val file = tempFile()
        serializationIon.encode(db, file.outputStream())

        val integrityFailed = try {
            deserializationIon.decode<PolymorphicDB>(file.inputStream())
            false
        } catch (e: IntegrityCheckException) {
            true
        }

        assertTrue(integrityFailed)
    }
}

@Serializable
class PolymorphicDB(val people: Array<Person>)

interface Person {
    val id: String
    val firstName: String
    val lastName: String
}

@Serializable
@SerialName("student")
data class Student(override val id: String, override val firstName: String, override val lastName: String): Person

@Serializable
@SerialName("student")
data class FlippedParamsStudent(override val id: String, override val lastName: String, override val firstName: String): Person

@Serializable
data class Employee(val student: Person, private val index: Int): Person {
    override val id = student.id
    override val firstName = student.firstName
    override val lastName = student.lastName
}

@Serializable
class SimpleDB(val first: String, val second: String)

@Serializable
class FlippedSimpleDB(val second: String, val first: String)
