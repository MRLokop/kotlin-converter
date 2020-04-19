package io.mrlokop.kotlin.utils.conventer.enities.expression

class StringExpression(val data: String) : ExpressionEntity() {
    init {
        type = "string"
    }

    fun get(): String {
        return data
    }
}