package io.mrlokop.kotlin.utils.conventer.parsers

import io.mrlokop.kotlin.utils.conventer.KPS
import io.mrlokop.kotlin.utils.conventer.debug
import io.mrlokop.kotlin.utils.conventer.enities.MultiLineExpression
import io.mrlokop.kotlin.utils.conventer.enities.MultiLineText
import io.mrlokop.kotlin.utils.conventer.enities.MultilineStringLiteralEntity
import io.mrlokop.kotlin.utils.conventer.enities.expression.*
import java.lang.IllegalArgumentException

fun parseExpression(expression: KPS): ExpressionEntity {
    return parseExpression_(expression)
}
private fun parseExpression_(expression: KPS): ExpressionEntity {
    when (expression.token) {
        "postfixUnaryExpression" -> {
            if (expression.has("postfixUnarySuffix")) {
                if (!expression.getOne("primaryExpression").has("stringLiteral")) {
                    val func = FunctionExpression();
                    func.functionName =
                        parseString(expression.getOne("primaryExpression").getOne("simpleIdentifier")).joinToString("")
                    expression.forEach {
                        debug("   -> " + func.member, func.functionName, " --> ", it.token)
                        if (!it.has("callSuffix")) {
                            if (it.token == "primaryExpression") {
                                func.functionName = parseString(it.getOne("simpleIdentifier")).joinToString("")
                            } else if (it.token == "navigationSuffix") {
                                func.member += "." + func.functionName;
                                func.functionName = parseString(it.getOne("simpleIdentifier")).joinToString("")
                            } else {
                                func.member += "." + func.functionName;
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
                    return func;
                } else {
                    return parseExpression_(expression.children[1]);
                }
            } else {

                return parseExpression_(expression.children[0]);
            }
        }
        "additiveExpression" -> {
            if (expression.has("additiveOperator")) {
                // additive operation like 1+1 or 1-1
                val ent = AdditiveExpression();
                expression.forEach {
                    when (it.token) {
                        "multiplicativeExpression" -> {
                            ent.operations.add(AdditiveData(parseExpression_(it)))
                        }
                        "additiveOperator" -> {
                            ent.operations.add(AdditiveOperator(it[0].text))
                        }
                    }
                }
                return ent;
            } else {
                // not any operation go next
                return parseExpression_(expression.children[0]);
            }
        }
        "expression", "disjunction", "conjunction", "equality", "comparison", "infixOperation", "elvisExpression", "infixFunctionCall", "rangeExpression",
        "multiplicativeExpression", "asExpression", "assignableExpression", "prefixUnaryExpression", "primaryExpression"
        -> {
            return parseExpression_(expression.children[0]);
        }
        "functionLiteral" -> {

        }
        "literalConstant" -> {
            val const = ConstantExpression();
            const.const = parsePrimitive(expression)
            return const;
        }
        "postfixUnarySuffix" -> {
        }
        "stringLiteral" -> {
            if (expression.has("lineStringLiteral") && expression.getOne("lineStringLiteral").has("lineStringContent")) {
                val string = StringExpression(
                    parseString(
                        expression.getOne("lineStringLiteral").getOne("lineStringContent")
                    ).joinToString()
                );
                return string
            }

            val string = StringExpression("");
            return string
        }
        "declaration" -> {
            val r = DeclarationExpression()
            r.field = parsePropertyDeclaration(expression.getOne("propertyDeclaration"))
            return r;
        }
        "multiLineStringLiteral" -> {
            val obj = MultilineStringLiteralEntity();
            expression.forEach {
                when (it.token) {
                    "multiLineStringLiteral" -> {
                        val a = MultiLineText()
                        a.text = it.getOne("MultiLineText").text
                        obj.data.add(a)
                    }
                    "multiLineStringExpression" -> {
                        val a = MultiLineExpression()
                        a.expression = parseExpression_(it.getOne("expression"))
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
        "NL" -> {

        }
        "SEMICOLON" -> {
        }
        "simpleIdentifier" -> {
            val id = IdentifierExpression()
            id.identifier = parseString(expression).joinToString("");
            return id;
        }
        "parenthesizedExpression" -> {
            return parseExpression_(expression.getOne("expression"))
        }
        "assignment" -> {
            val assigment = AssigmentExpression();
            expression.forEach {
                when (it.token) {
                    "assignableExpression" -> {
                        assigment.field.name = (parseExpression_(it) as IdentifierExpression).identifier
                    }
                    "expression" -> {
                        assigment.field.expression = parseExpression_(it)
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
            throw IllegalArgumentException("Not supported expression\n\n-> ${expression.token}\n${expression}")
        }
    }

    return ExpressionEntity();
}

fun parseStatement(statement: KPS): StatementEntity {
    val ent = StatementEntity()
    statement.forEach {
        ent.expressions.add(parseExpression_(it))
    }
    return ent
}