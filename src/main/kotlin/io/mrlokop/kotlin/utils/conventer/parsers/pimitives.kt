package io.mrlokop.kotlin.utils.conventer.parsers

import io.mrlokop.kotlin.utils.conventer.KPS
import io.mrlokop.kotlin.utils.conventer.enities.IntPrimitiveEntity
import io.mrlokop.kotlin.utils.conventer.enities.PrimitiveEntity
import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTreeNodeType

fun parseString(kpt: KPS): List<String> {
    when (kpt.token) {
        "simpleIdentifier" -> {
            return kpt.children.map {
                return parseString(it);
            }
        }
        "Identifier" -> {
            if (kpt.type == KotlinParseTreeNodeType.TERMINAL) {
                val r = kpt.text;
                return listOf(r!!)
            } else {
                return parseString(kpt)
            }
        }
        "DOT" -> {
            return listOf(kpt.text)
        }
        "lineStringContent" -> {
            if (kpt.has("LineStrText")) {
                return listOf(kpt.getOne("LineStrText").text)
            } else {
                return listOf(kpt.getOne("LineStrEscapedChar").text)
            }
        }
        else -> {
            //println("Unknown: ${getTreeType(kpt)}")
        }
    }
    return listOf()
}

fun parsePrimitive(primitive: KPS) : PrimitiveEntity {
    return when (primitive.token) {
        "literalConstant" -> {
            parsePrimitive(primitive.children[0])
        }
        "IntegerLiteral" -> {
            IntPrimitiveEntity(primitive.text);
        }
        else -> {
            PrimitiveEntity(primitive.text);
        }
    }
}
