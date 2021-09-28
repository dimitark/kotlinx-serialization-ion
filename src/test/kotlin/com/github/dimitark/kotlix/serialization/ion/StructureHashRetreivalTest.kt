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
        assertEquals("cb5b67a7a1f22f8690e49f6b55fce022", hash)
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
        assertEquals("77f089a58b4483407ed3da22a9c37fdb", hash)
    }
}

@Serializable
class SimpleStorage(val first: String, val second: String)


