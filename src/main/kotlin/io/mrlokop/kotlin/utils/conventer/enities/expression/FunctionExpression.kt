package io.mrlokop.kotlin.utils.conventer.enities.expression

import io.mrlokop.kotlin.utils.conventer.enities.PrimitiveEntity

class FunctionExpression : ExpressionEntity() {
    init {
        type = "functionInvoke"
    }
    var member = "";
    var functionName = ""
    var args = mutableListOf<ExpressionEntity>()
}