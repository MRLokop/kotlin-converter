package io.mrlokop.kotlin.utils.conventer.enities.expression

import io.mrlokop.kotlin.utils.conventer.enities.AbstractEntity

class StatementEntity : AbstractEntity("statement-entity") {
    val expressions = mutableListOf<ExpressionEntity>()
}