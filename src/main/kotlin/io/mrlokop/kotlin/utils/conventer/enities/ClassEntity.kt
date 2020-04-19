package io.mrlokop.kotlin.utils.conventer.enities

class ClassEntity : AbstractEntity("class-entity") {
    var packagePath: String = ""
    var name: String = ""
    var methods = mutableListOf<FunctionEntity>()
    var fields = mutableListOf<FieldEntity>()
}