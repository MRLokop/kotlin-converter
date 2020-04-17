package io.mrlokop.kotlin.utils.conventer.enities

import io.mrlokop.kotlin.utils.conventer.enities.expression.ExpressionEntity

class FieldEntity {
    var decType = "var"
    var name = ""
    var type: TypeEntity? = null
    var value: Any? = null
    var expression: ExpressionEntity? = null
}