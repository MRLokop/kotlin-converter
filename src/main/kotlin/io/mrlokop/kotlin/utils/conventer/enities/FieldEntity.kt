package io.mrlokop.kotlin.utils.conventer.enities

import io.mrlokop.kotlin.utils.conventer.enities.expression.ExpressionEntity

class FieldEntity : AbstractEntity("field-entity") {
    var decType = "var"
    var name = ""
        set(value) {
            field = value
            customName = value
        }
    var type: TypeEntity? = null
    var expression: ExpressionEntity? = null
    var customName = ""
    var visibility = "public"
}