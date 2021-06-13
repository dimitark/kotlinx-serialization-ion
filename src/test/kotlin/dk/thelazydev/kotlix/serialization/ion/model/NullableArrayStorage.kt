package dk.thelazydev.kotlix.serialization.ion.model

import kotlinx.serialization.Serializable

@Serializable
data class NullableArrayStorage(private val id: String, private val numbers: Array<Int?>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NullableArrayStorage

        if (id != other.id) return false
        if (!numbers.contentEquals(other.numbers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + numbers.contentHashCode()
        return result
    }
}
