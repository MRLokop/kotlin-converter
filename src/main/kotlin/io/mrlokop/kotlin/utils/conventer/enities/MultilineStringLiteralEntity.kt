package io.mrlokop.kotlin.utils.conventer.enities

import io.mrlokop.kotlin.utils.conventer.enities.expression.ExpressionEntity

class MultilineStringLiteralEntity {
    val data = mutableListOf<MultiLineObject>()
}
abstract class MultiLineObject
class MultiLineText : MultiLineObject(){
    var text = ""
}
class MultiLineExpression : MultiLineObject( ){
    var expression: ExpressionEntity = ExpressionEntity()
}