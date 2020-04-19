package io.mrlokop.kotlin.utils.conventer.parsers

import io.mrlokop.kotlin.utils.conventer.enities.MultiLineExpression
import io.mrlokop.kotlin.utils.conventer.enities.MultiLineText
import io.mrlokop.kotlin.utils.conventer.enities.MultilineStringLiteralEntity
import io.mrlokop.kotlin.utils.conventer.enities.expression.*
import io.mrlokop.kotlin.utils.conventer.utils.TreeNode

fun parseExpression(expression: TreeNode): ExpressionEntity {
    when (expression.token) {
        "postfixUnaryExpression" -> {
            if (expression.has("postfixUnarySuffix")) {
                var ent = FunctionInvokeExpression()
                expression.forEach {
                    when (it.token) {
                        "primaryExpression" -> {
                            ent.functionName = parseString(it.getOne("simpleIdentifier")).joinToString("")
                        }
                        "postfixUnarySuffix" -> {
                            if (it.has("callSuffix")) {
                                val old = ent
                                ent = FunctionInvokeExpression()
                                ent.parent = old
                                val callSuffix = it.getOne("callSuffix")
                                if (callSuffix.has("valueArguments")) {
                                    if (callSuffix.getOne("valueArguments").has("LPAREN")) {
                                        if (ent.parent != null && ent.parent is FunctionInvokeExpression)
                                            (ent.parent!! as FunctionInvokeExpression).isMember = true
                                    }
                                    callSuffix.getOne("valueArguments").forEach {
                                        when (it.token) {
                                            "valueArgument" -> {
                                                (ent.parent!! as FunctionInvokeExpression).args.add(
                                                    parseExpression(
                                                        it.getOne(
                                                            "expression"
                                                        )
                                                    )
                                                )
                                            }
                                        }
                                    }
                                } else if (callSuffix.has("annotatedLambda")) {
                                    ent.args.add(
                                        parseExpression(
                                            callSuffix.getOne("annotatedLambda").getOne("lambdaLiteral")
                                        )
                                    )
                                }
                            }
                            if (it.has("navigationSuffix")) {
                                val old = ent
                                ent = FunctionInvokeExpression()
                                ent.parent = old
                                ent.isDotAccessor = true
                                ent.functionName = parseString(
                                    it.getOne("navigationSuffix").getOne("simpleIdentifier")
                                ).joinToString("")
                            }
                        }
                    }
                }
                return ent
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
            if (expression.has("lambdaParameters")) {
                expression.getOne("lambdaParameters").forEach {
                    if (it.token != "COMMA") {
                        ent.parameters.add(parsePropertyDeclaration(it))
                    }
                }
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
            if (expression.has("functionDeclaration")) {
                val r = FunctionDeclarationExpression()
                r.function = parseFunctionDeclaration(expression.getOne("functionDeclaration"))
                return r

            } else if (expression.has("propertyDeclaration")) {
                val r = PropertyDeclarationExpression()
                r.field = parsePropertyDeclaration(expression.getOne("propertyDeclaration"))
                return r
            } else {
                throw IllegalArgumentException("Unsupported expression\n\n${expression}")
            }
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
        "jumpExpression" -> {
            val ext = JumpExpression()
            expression.forEach {
                when (it.token) {
                    "RETURN" -> {
                        ext.jumpType = "return"
                    }
                    "expression" -> {
                        ext.expression = parseExpression(it)
                    }
                    else -> {
                        throw IllegalArgumentException("Unsupported expression\n\n-> ${expression}")
                    }
                }
            }
            return ext
        }
        else -> {
            throw IllegalArgumentException("Unsupported expression\n\n${expression}")
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