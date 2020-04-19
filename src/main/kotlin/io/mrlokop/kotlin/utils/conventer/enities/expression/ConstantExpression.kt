package io.mrlokop.kotlin.utils.conventer.enities.expression

import io.mrlokop.kotlin.utils.conventer.enities.PrimitiveEntity

class ConstantExpression : ExpressionEntity() {
    init {
        type = "primitive-constant"
    }
    var const: PrimitiveEntity? = null
}