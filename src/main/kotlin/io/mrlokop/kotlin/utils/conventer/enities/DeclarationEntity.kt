package io.mrlokop.kotlin.utils.conventer.enities

class DeclarationEntity : AbstractEntity("declaration-entity") {
    val fields = mutableListOf<FieldEntity>()
    var functions = mutableListOf<FunctionEntity>()
}