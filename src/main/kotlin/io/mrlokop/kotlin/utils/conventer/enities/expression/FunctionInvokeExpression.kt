package io.mrlokop.kotlin.utils.conventer.enities.expression

class FunctionInvokeExpression : ExpressionEntity() {
    init {
        type = "functionInvoke"
    }

    var isMember = false
    var isDotAccessor = false
    var parent: ExpressionEntity? = null
    var name = ""
    var args = mutableListOf<ExpressionEntity>()
}