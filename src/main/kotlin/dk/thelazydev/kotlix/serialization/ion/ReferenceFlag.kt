package dk.thelazydev.kotlix.serialization.ion

enum class ReferenceFlag(val flag: Boolean) {
    WholeObject(true),
    ObjectReference(false)
}

fun Boolean.toReferenceFlag() = if(this) ReferenceFlag.WholeObject else ReferenceFlag.ObjectReference
