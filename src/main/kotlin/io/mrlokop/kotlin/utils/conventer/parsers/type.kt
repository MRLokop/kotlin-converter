package io.mrlokop.kotlin.utils.conventer.parsers

import io.mrlokop.kotlin.utils.conventer.enities.FieldEntity
import io.mrlokop.kotlin.utils.conventer.enities.TypeEntity
import io.mrlokop.kotlin.utils.conventer.utils.TreeNode


fun parseModifier(mod: TreeNode): String {
    return mod.children[0].children[0].text
}

fun parseParameter(param: TreeNode): FieldEntity {
    val field = FieldEntity()
    assert(param.peek("simpleIdentifier") {
        field.name = parseString(it).joinToString("")
    })
    if (param.peekIfExists("COLON")) {
    }
    assert(param.peek("type") {
        field.type = parseType(it)
    })
    return field
}

fun parseType(type: TreeNode): TypeEntity {
    val ent = TypeEntity()

    when (type.token) {
        "type" -> {
            return parseType(type.getOne("typeReference"))
        }
        "typeReference" -> {
            return parseType(type.getOne("userType"))
        }
        "userType" -> {
            return parseType(type.getOne("simpleUserType"))
        }
        "simpleUserType" -> {
            ent.name = parseString(type.getOne("simpleIdentifier")).joinToString("")
            if (type.has("typeArguments")) {
                type.getOne("typeArguments").forEach {
                    if (it.token == "typeProjection") {
                        ent.subTypes.add(parseType(it.getOne("type")))
                    }
                }
            }
        }
    }
    return ent
}