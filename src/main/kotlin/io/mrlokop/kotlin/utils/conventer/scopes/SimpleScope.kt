package io.mrlokop.kotlin.utils.conventer.scopes

import io.mrlokop.kotlin.utils.conventer.enities.ClassEntity
import io.mrlokop.kotlin.utils.conventer.enities.FieldEntity
import io.mrlokop.kotlin.utils.conventer.enities.FunctionEntity
import io.mrlokop.kotlin.utils.conventer.enities.expression.LambdaExpression
import io.mrlokop.kotlin.utils.conventer.utils.warn


open class SimpleScope : Scope() {


    override fun resolveLocalFunctionByName(function: String, includeVariables: Boolean): List<FunctionEntity> {

        val results = mutableListOf<FunctionEntity>()
        functions.forEach {
            if (it.name == function)
                results.add(it)
        }
        if (links[function] != null) {
            links[function]!!.forEach {
                if (it is FunctionEntity) {
                    if (it.name == function)
                        results.add(it)
                }
            }
        }
        if (includeVariables) {
            fields.forEach {
                if (it.name == function) {
                    if (it.expression is LambdaExpression) {
                        val lambda: LambdaExpression = it.expression as LambdaExpression
                        val func = FunctionEntity()
                        func.body.block.statements.addAll(lambda.statements)
                        func.params.addAll(lambda.parameters)
                        func.scope = entryScope
                        func.name = function
                        func.visibility = it.visibility
                        //func.mods = it.
                        results.add(func)
                    }
                }
            }
        }

        if (results.size == 0) {
            warn("[${entryScope.entry.id}] --> Failed resolving: $function: Not found")
        }

        return results
    }

    override fun resolveLocalClassByName(`class`: String): List<ClassEntity> {

        val results = mutableListOf<ClassEntity>()
        classes.forEach {
            if (it.name == `class`)
                results.add(it)

        }
        return results
    }

    override fun resolveLocalVariableByName(variable: String): List<FieldEntity> {

        val results = mutableListOf<FieldEntity>()
        fields.forEach {
            if (it.name == variable)
                results.add(it)

        }
        return results
    }


    fun resolveByName(name: String): MutableList<Any> {
        val results = mutableListOf<Any>()
        results.addAll(resolveLocalClassByName(name))
        results.addAll(resolveLocalFunctionByName(name))
        results.addAll(resolveLocalVariableByName(name))
        return results
    }


}
