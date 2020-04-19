package io.mrlokop.kotlin.utils.conventer.enities.expression

import io.mrlokop.kotlin.utils.conventer.enities.FieldEntity

class LambdaExpression : ExpressionEntity() {
    val statements = mutableListOf<StatementEntity>()
    val parameters = mutableListOf<FieldEntity>()

    init {
        type = "lambda-expression"
    }
}