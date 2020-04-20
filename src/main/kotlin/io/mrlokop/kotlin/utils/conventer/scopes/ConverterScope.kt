package io.mrlokop.kotlin.utils.conventer.scopes

import io.mrlokop.kotlin.utils.conventer.enities.EntryEntity
import io.mrlokop.kotlin.utils.conventer.enities.FieldEntity
import io.mrlokop.kotlin.utils.conventer.enities.FunctionEntity
import io.mrlokop.kotlin.utils.conventer.utils.PackageContainer


class ConverterScope {

    val packages = mutableMapOf<String, PackageContainer>()
    val entries = mutableMapOf<String, EntryScope>()
    fun getPackage(pack: String): PackageContainer {
        if (packages.containsKey(pack).not()) {
            packages[pack] = PackageContainer()
        }
        return packages[pack]!!
    }

    fun makeEntryScope(entry: EntryEntity): EntryScope {
        if (!entries.containsKey(entry.id))
            entries[entry.id] = EntryScope(entry, this)

        return entries[entry.id]!!
    }

    fun resolveField(pack: String, field: String): FieldEntity? {
        getPackage(pack).fields.forEach {
            if (it.name == field)
                return it
        }
        return null
    }

    fun resolveFunction(pack: String, function: String): FunctionEntity? {
        getPackage(pack).functions.forEach {
            if (it.name == function)
                return it
        }
        return null
    }

}