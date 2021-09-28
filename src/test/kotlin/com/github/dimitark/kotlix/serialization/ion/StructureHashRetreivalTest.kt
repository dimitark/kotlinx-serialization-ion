package com.github.dimitark.kotlix.serialization.ion

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi
@ExperimentalSerializationApi
class StructureHashRetrievalTest {

    @Test
    fun `Test structure hash retrieval`() {
        val ion = Ion()
        val hash = ion.structureHash<SimpleStorage>()
        assertEquals(405909462, hash)
    }

    @Test
    fun `Test structure hash with polymorphism`() {
        val ion = Ion {
            serializersModule = SerializersModule {
                polymorphic(Person::class) {
                    subclass(Student::class)
                    subclass(Employee::class)
                }
            }
        }

        val hash = ion.structureHash<PolymorphicDB>()
        assertEquals(-1831096579, hash)
    }
}

@Serializable
class SimpleStorage(val first: String, val second: String)


