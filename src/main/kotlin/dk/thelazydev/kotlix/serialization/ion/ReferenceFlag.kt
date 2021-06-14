package dk.thelazydev.kotlix.serialization.ion

enum class ReferenceFlag(val flag: Boolean) {
    WholeObject(true),
    ObjectReference(false);

    companion object {
        fun fromBoolean(value: Boolean) = when(value) {
            true -> WholeObject
            false -> ObjectReference
        }
    }
}
