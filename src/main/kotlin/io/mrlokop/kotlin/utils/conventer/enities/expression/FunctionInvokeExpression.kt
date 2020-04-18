package io.mrlokop.kotlin.utils.conventer.enities.expression

class FunctionInvokeExpression : ExpressionEntity() {
    init {
        type = "functionInvoke"
    }

    var member = ""
    var functionName = ""
    var args = mutableListOf<ExpressionEntity>()
}