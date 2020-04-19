package io.mrlokop.kotlin.utils.conventer.enities.expression

class IdentifierExpression : ExpressionEntity() {
    var identifier = ""

    init {
        type = "identifier"
    }
}