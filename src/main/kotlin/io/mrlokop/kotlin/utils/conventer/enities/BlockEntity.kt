package io.mrlokop.kotlin.utils.conventer.enities

import io.mrlokop.kotlin.utils.conventer.enities.expression.StatementEntity

class BlockEntity : AbstractEntity("block-entity") {

    val statements = mutableListOf<StatementEntity>()
}