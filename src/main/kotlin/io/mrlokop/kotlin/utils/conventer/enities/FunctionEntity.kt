package io.mrlokop.kotlin.utils.conventer.enities

class FunctionEntity {
    var name: String = ""
    val params = mutableListOf<FieldEntity>()
    var mods = mutableListOf<String>()
    var body = FunctionBodyEntity()
}