package io.mrlokop.kotlin.utils.conventer.utils

import io.mrlokop.kotlin.utils.conventer.enities.*

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

    class EntryScope(val entry: EntryEntity, val scope: ConverterScope) {

        val container = scope.getPackage(entry.packageName)

        val functions = mutableListOf<FunctionEntity>()
        val classes = mutableListOf<ClassEntity>()
        val fields = mutableListOf<FieldEntity>()
        val links = mutableMapOf<String, MutableList<Any>>()

        fun resolveFunctionByName(function: String): MutableList<FunctionEntity> {
            val results = mutableListOf<FunctionEntity>()
            functions.forEach {
                if (it.name == function)
                    results.add(it)

            }
            return results
        }

        fun resolveLocalFunctionByName(function: String): MutableList<FunctionEntity> {
            debug("[${entry.id}] => Resolving: $function...")
            val results = mutableListOf<FunctionEntity>()
            functions.forEach {
                if (it.name == function)
                    results.add(it)
            }
            if (links[function] != null) {
                debug("[${entry.id}] --> Found: $function")

                links[function]!!.forEach {
                    if (it is FunctionEntity) {
                        if (it.name == function)
                            results.add(it)
                    }
                }
            }
            debug("[${entry.id}] --> Resolved: $function: ${results.size}")
            return results
        }


        fun resolveFieldByName(function: String): MutableList<FieldEntity> {
            val results = mutableListOf<FieldEntity>()
            fields.forEach {
                if (it.name == function)
                    results.add(it)

            }
            return results
        }

        fun resolveByName(name: String): MutableList<Any> {
            val results = mutableListOf<Any>()
            results.addAll(resolveFunctionByName(name))
            results.addAll(resolveFieldByName(name))
            return results
        }

        init {
            // Parsing entry...
            entry.scope = this
            debug("[${entry.id}] -> Compiling scope: ${entry.packageName} (${entry.fileName})")
            entry.topLevels.forEach {
                registerScope(it)
                it.declarations.forEach {
                    it.fields.forEach {
                        fields.add(it)
                        export(it)
                    }
                }
            }
            entry.topLevels.forEach {
                registerScope(it)
                it.declarations.forEach {
                    it.functions.forEach {
                        functions.add(it)
                        export(it)
                    }
                }
            }
        }

        fun processImports() {

            debug("[${entry.id}] -> Processing imports...")
            entry.imports.forEach { key ->
                val name = key.substringAfterLast(".")
                val results =
                    scope.getPackage(key.substringBeforeLast(".")).resolveFunctionByName(key.substringAfterLast("."))
                if (links.containsKey(name).not())
                    links[name] = mutableListOf()
                debug("[${entry.id}] -> Imported: $key (" + results.size + ")")
                links[name]!!.addAll(results)
            }


        }

        fun export(data: FieldEntity) {
            registerScope(data)
            debug("[${entry.id}] -> Exported field: ${data.name} (${entry.packageName})")
            container.fields.add(data)
        }

        fun export(data: FunctionEntity) {
            registerScope(data)
            debug("[${entry.id}] -> Exported function: ${data.name} (${entry.packageName})")
            container.functions.add(data)
        }

        fun registerScope(ent: AbstractEntity) {
            ent.scope = this
            when (ent) {
                is FunctionEntity -> {
                    registerScope(ent.body)
                    ent.params.forEach {
                        registerScope(it)
                    }
                }
                is FieldEntity -> {
                    registerScope(ent.expression!!)
                    if (ent.type != null) {
                        registerScope(ent.type!!)
                    }
                }
                is TypeEntity -> {
                    ent.subTypes.forEach {
                        registerScope(it)
                    }
                }
                else -> {
                    warn("Unknown entity: ${ent.javaClass.simpleName}")
                }
            }
        }
    }
}