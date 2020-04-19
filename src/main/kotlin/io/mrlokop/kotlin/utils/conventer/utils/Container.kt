package io.mrlokop.kotlin.utils.conventer.utils

import io.mrlokop.kotlin.utils.conventer.enities.ClassEntity
import io.mrlokop.kotlin.utils.conventer.enities.FieldEntity
import io.mrlokop.kotlin.utils.conventer.enities.FunctionEntity

@Deprecated("May be deleted")
class PackageContainer {
    val packages = mutableMapOf<String, PackageContainer>()
    val functions = mutableListOf<FunctionEntity>()
    val classes = mutableListOf<ClassEntity>()
    val fields = mutableListOf<FieldEntity>()


    fun resolveFunctionByName(function: String): MutableList<FunctionEntity> {
        val results = mutableListOf<FunctionEntity>()
        functions.forEach {
            if (it.name == function)
                results.add(it)

        }
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

}