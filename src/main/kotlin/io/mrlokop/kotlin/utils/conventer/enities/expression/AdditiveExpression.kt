package io.mrlokop.kotlin.utils.conventer.enities.expression

class AdditiveExpression : ExpressionEntity() {
    init {
        type = "additive-expression"
    }

    val operations = mutableListOf<AdditiveOperation>()
}

abstract class AdditiveOperation

class AdditiveData(val data: ExpressionEntity) : AdditiveOperation()
class AdditiveOperator(val data: String) : AdditiveOperation()