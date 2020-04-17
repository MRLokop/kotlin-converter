package io.mrlokop.kotlin.utils.conventer.enities.expression

class MultiplicativeExpression : ExpressionEntity() {
    init {
        type = "multiplicative-expression"
    }

    val operations = mutableListOf<MultiplicativeOperation>()
}

abstract class MultiplicativeOperation

class MultiplicativeData(val data: ExpressionEntity) : MultiplicativeOperation()
class MultiplicativeOperator(val data: String) : MultiplicativeOperation()