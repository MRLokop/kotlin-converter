package io.mrlokop.kotlin.utils.conventer.enities

class TopLevelEntity : AbstractEntity("toplevel-entity") {
    var declarations = mutableListOf<DeclarationEntity>()
}