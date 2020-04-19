package io.mrlokop.kotlin.utils.conventer.enities.expression

class JumpExpression : ExpressionEntity() {
    var jumpType = ""
    var expression: ExpressionEntity = ExpressionEntity()

    init {
        type = "jump-expression"
    }
}