package io.mrlokop.kotlin.utils.conventer.enities

class EntryEntity {
    var fileName = "undefined"
    var packageName = ""
    val topLevels = mutableListOf<TopLevelEntity>()
}