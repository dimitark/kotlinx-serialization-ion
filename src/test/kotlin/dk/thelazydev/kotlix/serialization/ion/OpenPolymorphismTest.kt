package dk.thelazydev.kotlix.serialization.ion

import dk.thelazydev.kotlix.serialization.ion.model.Employee
import dk.thelazydev.kotlix.serialization.ion.model.EmployeePosition
import dk.thelazydev.kotlix.serialization.ion.model.Person
import dk.thelazydev.kotlix.serialization.ion.model.Student
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
    fun `Test open polymorphism single object encode`() {
        val file = tempFile()
        ion.encode(student, file.outputStream())
        val decoded = ion.decode<Person>(file.inputStream())

        assertEquals(student, decoded)
    }

    @Test
    fun `Test open polymorphism list of objects encode`() {
        val file = tempFile()
        ion.encode(persons, file.outputStream())
        val decoded = ion.decode<List<Person>>(file.inputStream())

        assertEquals(persons, decoded)
    }
}
