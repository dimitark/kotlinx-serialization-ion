package com.github.dimitark.kotlix.serialization.ion

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

@ExperimentalStdlibApi
@ExperimentalSerializationApi
class ComplexDataTest {
    private val parentItem1 = ParentItem("parent1", 1)
    private val parentItem2 = ParentItem("parent2", null)

    private val childItem1 = ChildItem(parentItem1, 1)
    private val childItem2 = ChildItem(parentItem1, 2)
    private val childItem3 = ChildItem(parentItem2, 1)

    private val otherItem = OtherItem("other")

    private val storage = DataStorage(
        arrayOf(
            Container(childItem1, otherItem),
            Container(childItem2, otherItem),
            Container(parentItem2, otherItem),
            Container(childItem3, otherItem),
            Container(parentItem1, otherItem)
        )
    )

    private val ion = Ion {
        serializersModule = SerializersModule {
            polymorphic(Item::class) {
                subclass(ParentItem::class)
                subclass(ChildItem::class)
            }
        }
    }

    @Test
    fun `Test complex data structure`() {
        val file = tempFile()
        ion.encode(storage, file.outputStream())
        val decoded = ion.decode<DataStorage>(file.inputStream())

        assertEquals(storage, decoded)

        assertSame(decoded.items[0].other, decoded.items[1].other)
        assertSame(decoded.items[0].other, decoded.items[2].other)
        assertSame(decoded.items[0].other, decoded.items[3].other)
        assertSame(decoded.items[0].other, decoded.items[4].other)

        assertSame((decoded.items[0].item as ChildItem).parent, (decoded.items[1].item as ChildItem).parent)
        assertNotSame((decoded.items[0].item as ChildItem).parent, (decoded.items[3].item as ChildItem).parent)
    }
}

@Serializable
data class DataStorage(val items: Array<Container>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataStorage

        if (!items.contentEquals(other.items)) return false

        return true
    }

    override fun hashCode(): Int {
        return items.contentHashCode()
    }
}

@Serializable
data class Container(val item: Item, val other: OtherItem)

@Serializable
data class OtherItem(val id: String)

interface Item {
    val id: String
    val nullable: Int?
}

@Serializable
data class ParentItem(override val id: String, override val nullable: Int?): Item

@Serializable
data class ChildItem(val parent: Item, private val index: Int): Item {
    override val id = "${parent.id}_$index"
    override val nullable = parent.nullable
}
