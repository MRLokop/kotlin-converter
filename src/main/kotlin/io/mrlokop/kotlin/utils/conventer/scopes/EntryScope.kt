package io.mrlokop.kotlin.utils.conventer.scopes

import io.mrlokop.kotlin.utils.conventer.enities.ClassEntity
import io.mrlokop.kotlin.utils.conventer.enities.DeclarationEntity
import io.mrlokop.kotlin.utils.conventer.enities.EntryEntity
import io.mrlokop.kotlin.utils.conventer.enities.FunctionEntity
import io.mrlokop.kotlin.utils.conventer.utils.debug


class EntryScope(entry: EntryEntity, val scope: ConverterScope) : SimpleScope() {


    fun resolveFunctionByName(function: String): MutableList<FunctionEntity> {
        val results = mutableListOf<FunctionEntity>()
        functions.forEach {
            if (it.name == function)
                results.add(it)

        }
        return results
    }

    init {
        this.entry = entry
        entryScope = this
        entry.scope = this
    }

    fun compile() {

        debug("[${entry.id}] -> Compiling scope: ${entry.packageName} (${entry.fileName})")
        entry.topLevels.forEach {
            registerScope(it)
            it.declarations.forEach {
                parseDeclaration(it)
            }
        }

    }

    fun parseDeclaration(declaration: DeclarationEntity) {
        declaration.fields.forEach {
            fields.add(it)
            export(it)
        }

        declaration.functions.forEach {
            functions.add(it)
            export(it)
        }

        declaration.classes.forEach {
            parseClassDeclaration(it)
        }
    }

    fun parseClassDeclaration(clazz: ClassEntity) {
        classes.add(clazz)
        export(clazz)
    }

    fun processImports() {

        debug("[${entry.id}] -> Processing imports...")
        entry.imports.forEach { key ->
            val name = key.substringAfterLast(".")
            val results =
                scope.getPackage(key.substringBeforeLast(".")).resolveFunctionByName(key.substringAfterLast("."))
            if (links.containsKey(name).not())
                links[name] = mutableListOf()
            debug("[${entry.id}] ->> Imported: $key (" + results.size + ")")
            links[name]!!.addAll(results)
        }


    }
}