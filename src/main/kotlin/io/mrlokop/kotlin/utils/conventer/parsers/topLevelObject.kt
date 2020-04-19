package io.mrlokop.kotlin.utils.conventer.parsers

import io.mrlokop.kotlin.utils.conventer.enities.*
import io.mrlokop.kotlin.utils.conventer.enities.expression.ExpressionEntity
import io.mrlokop.kotlin.utils.conventer.exceptions.UnknownDeclarationException
import io.mrlokop.kotlin.utils.conventer.utils.TreeNode


fun parseTopLevel(tpLevel: TreeNode): TopLevelEntity {
    val topLevel = TopLevelEntity()
    for (child in tpLevel.children) {
        when (child.token) {
            "declaration" -> {
                topLevel.declarations.add(parseDeclaration(child))
            }
        }
    }
    return topLevel
}


fun parseDeclaration(dec: TreeNode): DeclarationEntity {
    val declaration = DeclarationEntity()
    dec.children.forEach {
        when (it.token) {
            "functionDeclaration" -> {
                declaration.functions.add(parseFunctionDeclaration(it))
            }
            "propertyDeclaration" -> {
                declaration.fields.add(parsePropertyDeclaration(it))
            }
            "classDeclaration" -> {
                declaration.classes.add(parseClassDeclaration(it))
            }
            else -> {
                throw UnknownDeclarationException(it.token)
            }
        }
    }
    return declaration
}

fun parseClassDeclaration(tree: TreeNode): ClassEntity {
    var ent = ClassEntity()
    tree.forEach {
        when (it.token) {
            "simpleIdentifier" -> {
                ent.name = parseString(it).joinToString("")
            }
            "classBody" -> {
                it.getOne("classMemberDeclarations").forEach {
                    it.forEach {
                        ent.declarations.add(parseDeclaration(it))
                    }
                }
            }
        }
    }
    return ent
}

fun parsePropertyDeclaration(func: TreeNode): FieldEntity {
    val field = FieldEntity()

    for (child in func.children) {
        when (child.token) {
            "VAL" -> {
                field.decType = "val"
            }
            "ASSIGNMENT" -> {
            }
            "variableDeclaration" -> {
                field.name = parseString(child.getOne("simpleIdentifier")).joinToString("")
            }
            "expression" -> {
                field.expression = parseExpression(child)
            }
        }
    }

    return field

}

fun parseFunctionDeclaration(func: TreeNode): FunctionEntity {
    val function = FunctionEntity()

    for (child in func.children) {
        when (child.token) {
            "modifiers" -> {
                child.forEach {
                    if (it.token == "modifier") {
                        function.mods.add(parseModifier(it))
                    }
                }
            }
            "FUN" -> {
            }
            "simpleIdentifier" -> {
                if (function.name == "") {
                    function.name = parseString(child).joinToString("")
                } else {

                }
            }
            "functionValueParameters" -> {
                child.forEach {
                    when (it.token) {
                        "functionValueParameter" -> {
                            it.peek("parameter") {
                                function.params.add(parseParameter(it))
                            }
                        }
                        // (          )
                        "LPAREN", "RPAREN" -> {
                        }
                        else -> {
                            println("unexpected: ${it.token}")
                        }
                    }
                }
            }
            "functionBody" -> {
                function.body = parseBody(child)
            }
        }
    }

    return function
}

fun parseBody(body: TreeNode): FunctionBodyEntity {
    val ent = FunctionBodyEntity()
    when (body.token) {
        "functionBody" -> {
            val block = body.peek("block") ?: throw IllegalStateException("Non-block function not supported")
            ent.block = parseBlock(block)
        }
    }
    return ent
}

fun parseBlock(block: TreeNode): BlockEntity {
    val ent = BlockEntity()
    block.forEach {
        when (it.token) {
            "statements" -> {
                it.forEach { statement ->
                    ent.statements.add(parseStatement(statement))
                }
            }
        }
    }
    return ent
}

fun parseArguments(arguments: TreeNode): List<ExpressionEntity> {
    val res = mutableListOf<ExpressionEntity>()
    arguments.forEach {
        when (it.token) {
            "valueArgument" -> {
                res.add(parseExpression(it.getOne("expression")))
            }
        }
    }
    return res
}