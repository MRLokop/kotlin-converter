package io.mrlokop.kotlin.utils.conventer.scopes

import io.mrlokop.kotlin.utils.conventer.enities.*
import io.mrlokop.kotlin.utils.conventer.utils.PackageContainer
import io.mrlokop.kotlin.utils.conventer.utils.debug
import io.mrlokop.kotlin.utils.conventer.utils.warn

abstract class Scope() {
    private var ____Scope: EntryScope? = null
    var entryScope: EntryScope
        get() {
            if (____Scope != null) {
                return ____Scope!!
            }
            throw NullPointerException("|> EntryScope i")
        }
        set(value) {
            ____Scope = value
            container = value.scope.getPackage(value.entry.packageName)
            entry = value.entry
        }

    constructor(scope: EntryScope) : this() {
        entryScope = scope

    }

    lateinit var container: PackageContainer
    lateinit var entry: EntryEntity

    val functions = mutableListOf<FunctionEntity>()
    val classes = mutableListOf<ClassEntity>()
    val fields = mutableListOf<FieldEntity>()
    val links = mutableMapOf<String, MutableList<Any>>()


    abstract fun resolveLocalFunctionByName(function: String, includeVariables: Boolean = true): List<FunctionEntity>
    abstract fun resolveLocalClassByName(`class`: String): List<ClassEntity>
    abstract fun resolveLocalVariableByName(variable: String): List<FieldEntity>


    fun export(data: FieldEntity) {
        registerScope(data)
        debug("[${entry.id}] -> Exported field: ${data.name} (${entry.packageName})")
        container.fields.add(data)
    }

    fun export(data: ClassEntity) {
        registerScope(data)
        debug("[${entry.id}] -> Exported class: ${data.name} (${entry.packageName})")
        container.classes.add(data)
    }

    fun export(data: FunctionEntity) {
        registerScope(data)
        debug("[${entry.id}] -> Exported function: ${data.name} (${entry.packageName})")
        container.functions.add(data)
    }

    /**
     * Recursive register scope in entities
     */
    fun registerScope(ent: AbstractEntity) {
        ent.scope = this.entryScope
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
            is DeclarationEntity -> {
                ent.functions.forEach {
                    registerScope(it)
                }
                ent.fields.forEach {
                    registerScope(it)
                }
            }
            is TopLevelEntity -> {
                ent.declarations.forEach {
                    registerScope(it)
                }
            }
            else -> {
                warn("Unknown entity: ${ent.javaClass.simpleName}")
            }
        }
    }

    companion object {
        val EmptyScope = EntryScope(EntryEntity(), ConverterScope())
    }
}