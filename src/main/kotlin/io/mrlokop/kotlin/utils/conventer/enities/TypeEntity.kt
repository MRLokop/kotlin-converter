package io.mrlokop.kotlin.utils.conventer.enities

class TypeEntity : AbstractEntity("type-entity") {
    var name: String = ""
    var subTypes = mutableListOf<TypeEntity>()

}