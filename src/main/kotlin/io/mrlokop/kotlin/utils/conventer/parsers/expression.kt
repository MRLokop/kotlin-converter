package io.mrlokop.kotlin.utils.conventer.parsers

import io.mrlokop.kotlin.utils.conventer.enities.MultiLineExpression
import io.mrlokop.kotlin.utils.conventer.enities.MultiLineText
import io.mrlokop.kotlin.utils.conventer.enities.MultilineStringLiteralEntity
import io.mrlokop.kotlin.utils.conventer.enities.expression.*
import io.mrlokop.kotlin.utils.conventer.utils.TreeNode
import io.mrlokop.kotlin.utils.conventer.utils.debug

fun parseExpression(expression: TreeNode): ExpressionEntity {
    when (expression.token) {
        "postfixUnaryExpression" -> {
            if (expression.has("postfixUnarySuffix")) {
                if (!expression.getOne("primaryExpression").has("stringLiteral")) {
                    val func = FunctionExpression()
                    func.functionName =
                        parseString(expression.getOne("primaryExpression").getOne("simpleIdentifier")).joinToString("")
                    expression.forEach {
                        debug(
                            "   -> " + func.member,
                            func.functionName,
                            " --> ",
                            it.token
                        )
                        if (!it.has("callSuffix")) {
                            if (it.token == "primaryExpression") {
                                func.functionName = parseString(it.getOne("simpleIdentifier")).joinToString("")
                            } else if (it.token == "navigationSuffix") {
                                func.member += "." + func.functionName
                                func.functionName = parseString(it.getOne("simpleIdentifier")).joinToString("")
                            } else {
                                func.member += "." + func.functionName
                                func.functionName = parseString(
                                    it.getOne("navigationSuffix").getOne("simpleIdentifier")
                                ).joinToString("")
                            }

                        } else {
                            func.args.addAll(parseArguments(it.getOne("callSuffix").getOne("valueArguments")))

                        }
                        //func.args.addAll(parseArguments(
                    }//
                    if (func.member.isNotEmpty()) {
                        func.member = func.member.substring(1)
                    }
                    return func
                } else {
                    return parseExpression(expression.children[1])
                }
            } else {

                return parseExpression(expression.children[0])
            }
        }
        "additiveExpression" -> {
            if (expression.has("additiveOperator")) {
                // additive operation like 1+1 or 1-1
                val ent = AdditiveExpression()
                expression.forEach {
                    when (it.token) {
                        "multiplicativeExpression" -> {
                            ent.operations.add(AdditiveData(parseExpression(it)))
                        }
                        "additiveOperator" -> {
                            ent.operations.add(AdditiveOperator(it[0].text))
                        }
                    }
                }
                return ent
            } else {
                // not any operation go next
                return parseExpression(expression.children[0])
            }
        }
        "multiplicativeExpression" -> {
            if (expression.has("multiplicativeOperator")) {
                // additive operation like 1+1 or 1-1
                val ent = MultiplicativeExpression()
                expression.forEach {
                    when (it.token) {
                        "asExpression" -> {
                            ent.operations.add(MultiplicativeData(parseExpression(it)))
                        }
                        "multiplicativeOperator" -> {
                            ent.operations.add(MultiplicativeOperator(it[0].text))
                        }
                    }
                }
                return ent
            } else {
                // not any operation go next
                return parseExpression(expression.children[0])
            }
        }

        "expression", "disjunction", "conjunction", "equality", "comparison", "infixOperation", "elvisExpression", "infixFunctionCall", "rangeExpression", "asExpression", "assignableExpression", "prefixUnaryExpression", "primaryExpression"
        -> {
            return parseExpression(expression.children[0])
        }

        "functionLiteral" -> {
            if (expression.has("lambdaLiteral")) {
                return parseExpression(expression.children[0])
            }
            throw IllegalArgumentException("Unsupported expression\n${expression}")
        }
        "lambdaLiteral" -> {
            val ent = LambdaExpression()
            expression.getOne("statements").forEach {
                ent.statements.add(parseStatement(it))
            }
            return ent
        }
        "literalConstant" -> {
            val const = ConstantExpression()
            const.const = parsePrimitive(expression)
            return const
        }
        "postfixUnarySuffix" -> {
        }
        "stringLiteral" -> {
            if (expression.has("lineStringLiteral") && expression.getOne("lineStringLiteral")
                    .has("lineStringContent")
            ) {
                val string = StringExpression(
                    parseString(
                        expression.getOne("lineStringLiteral").getOne("lineStringContent")
                    ).joinToString()
                )
                return string
            }

            val string = StringExpression("")
            return string
        }
        "declaration" -> {
            val r = DeclarationExpression()
            r.field = parsePropertyDeclaration(expression.getOne("propertyDeclaration"))
            return r
        }
        "multiLineStringLiteral" -> {
            val obj = MultilineStringLiteralEntity()
            expression.forEach {
                when (it.token) {
                    "multiLineStringLiteral" -> {
                        val a = MultiLineText()
                        a.text = it.getOne("MultiLineText").text
                        obj.data.add(a)
                    }
                    "multiLineStringExpression" -> {
                        val a = MultiLineExpression()
                        a.expression = parseExpression(it.getOne("expression"))
                        obj.data.add(a)
                    }
                    "multiLineStringContent" -> {
                        it.forEach {
                            when (it.token) {
                                "MultiLineStringQuote" -> {
                                    val a = MultiLineText()
                                    a.text = it.getOne("MultiLineText").text
                                    obj.data.add(a)
                                }
                            }
                        }
                    }
                }
            }
        }
        "NL", "SEMICOLON" -> {
        }
        "simpleIdentifier" -> {
            val id = IdentifierExpression()
            id.identifier = parseString(expression).joinToString("")
            return id
        }
        "parenthesizedExpression" -> {
            return parseExpression(expression.getOne("expression"))
        }
        "assignment" -> {
            val assigment = AssigmentExpression()
            expression.forEach {
                when (it.token) {
                    "assignableExpression" -> {
                        assigment.field.name = (parseExpression(it) as IdentifierExpression).identifier
                    }
                    "expression" -> {
                        assigment.field.expression = parseExpression(it)
                    }
                    else -> {
                        it.forEach {
                            assigment.field.decType = it.text
                        }
                    }
                }
            }
        }
        else -> {
            throw IllegalArgumentException("Unsupported expression\n\n-> ${expression.token}\n${expression}")
        }
    }

    return ExpressionEntity()
}

fun parseStatement(statement: TreeNode): StatementEntity {
    val ent = StatementEntity()
    statement.forEach {
        ent.expressions.add(parseExpression(it))
    }
    return ent
}