package dk.thelazydev.kotlix.serialization.ion

import dk.thelazydev.kotlix.serialization.ion.model.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSerializationApi
class OpenPolymorphismTest {
    private val persons = listOf(
        Student("student00", "Student", 23, "Course 101"),
        Employee("employee00", "Employee", null, EmployeePosition.Senior)
    )

    private val student: Person = Student("student01", "Lonely Student", 25, "Course 202")

    private val module = SerializersModule {
        polymorphic(Person::class) {
            subclass(Student::class)
            subclass(Employee::class)
        }
    }

    private val ion = Ion { serializersModule = module }

    @Test
    fun `Test open polymorphism single object JSON encode`() {
        val file = tempFile()
        ion.encodeJson(student, file.outputStream())
        val decoded = ion.decode<Person>(file.inputStream())

        assertEquals(student, decoded)
    }

    @Test
    fun `Test open polymorphism JSON encode`() {
        val file = tempFile()
        ion.encodeJson(persons, file.outputStream())
        val decoded = ion.decode<List<Person>>(file.inputStream())

        assertEquals(persons, decoded)
    }

    @Test
    fun `Test open polymorphism Binary encode`() {
        val file = tempFile()
        ion.encodeBinary(persons, file.outputStream())
        val decoded = ion.decode<List<Person>>(file.inputStream())

        assertEquals(persons, decoded)
    }
}
