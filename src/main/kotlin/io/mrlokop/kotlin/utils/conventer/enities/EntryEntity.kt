package io.mrlokop.kotlin.utils.conventer.enities

class EntryEntity : AbstractEntity("entry-entity") {
    private var _id: String? = null

    val id: String
        get() {
            if (_id == null)
                _id = packageName + " ... (${ids++})"
            return _id!!
        }

    var imports = mutableListOf<String>()
    var fileName = "undefined"
    var packageName = ""
    val topLevels = mutableListOf<TopLevelEntity>()


    companion object {
        private var ids = 0
    }
}