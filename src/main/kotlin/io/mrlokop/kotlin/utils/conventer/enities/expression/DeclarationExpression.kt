package io.mrlokop.kotlin.utils.conventer.enities.expression

import io.mrlokop.kotlin.utils.conventer.enities.FieldEntity
import io.mrlokop.kotlin.utils.conventer.enities.FunctionEntity

open class DeclarationExpression : ExpressionEntity() {
    init {
        type = "declaration-expression"
    }
}

class PropertyDeclarationExpression : DeclarationExpression() {
    var field: FieldEntity = FieldEntity()
}

class FunctionDeclarationExpression : DeclarationExpression() {
    var function = FunctionEntity()
}