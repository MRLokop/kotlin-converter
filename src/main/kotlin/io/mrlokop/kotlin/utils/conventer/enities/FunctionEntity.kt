package io.mrlokop.kotlin.utils.conventer.enities

class FunctionEntity : AbstractEntity("function-entity") {
    var name: String = ""
    val params = mutableListOf<FieldEntity>()
    var mods = mutableListOf<String>()
    var body = FunctionBodyEntity()
    var visibility = "public"
}