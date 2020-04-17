package io.mrlokop.kotlin.utils.conventer.parsers

import io.mrlokop.kotlin.utils.conventer.KPS
import io.mrlokop.kotlin.utils.conventer.debug
import io.mrlokop.kotlin.utils.conventer.enities.MultiLineExpression
import io.mrlokop.kotlin.utils.conventer.enities.MultiLineText
import io.mrlokop.kotlin.utils.conventer.enities.MultilineStringLiteralEntity
import io.mrlokop.kotlin.utils.conventer.enities.expression.*
import java.lang.IllegalArgumentException
import java.sql.Statement

fun parseExpression(expr: KPS): ExpressionEntity {
    debug("Parsing: ${expr.token}")
    when (expr.token) {
        "postfixUnaryExpression" -> {
            if (expr.has("postfixUnarySuffix")) {
                if (!expr.getOne("primaryExpression").has("stringLiteral")) {
                    val func = FunctionExpression();
                    func.functionName =
                        parseString(expr.getOne("primaryExpression").getOne("simpleIdentifier")).joinToString("")
                    expr.forEach {
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
                    return parseExpression(expr.children[1]);
                }
            } else {

                return parseExpression(expr.children[0]);
            }
        }
        "expression", "disjunction", "conjunction", "equality", "comparison", "infixOperation", "elvisExpression", "infixFunctionCall", "rangeExpression",
        "additiveExpression", "multiplicativeExpression", "multiLineStringLiteral", "asExpression", "assignableExpression", "prefixUnaryExpression", "primaryExpression"
        -> {
            return parseExpression(expr.children[0]);
        }
        "functionLiteral" -> {

        }
        "literalConstant" -> {
            val const = ConstantExpression();
            const.const = parsePrimitive(expr)
            return const;
        }
        "postfixUnarySuffix" -> {}
        "stringLiteral" -> {
            val string = StringExpression(
                parseString(
                    expr.getOne("lineStringLiteral").getOne("lineStringContent")
                ).joinToString()
            );
            debug("ParsedString: " + string.get())
            return string
        }
        "declaration" -> {
            val r = DeclarationExpression()
            r.field = parsePropertyDeclaration(expr.getOne("propertyDeclaration"))
            return r;
        }
        "multiLineStringLiteral" -> {
            val obj = MultilineStringLiteralEntity();
            expr.forEach {
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
        "NL" -> {

        }
        "SEMICOLON" -> {}
        "simpleIdentifier" -> {
            val id =  IdentifierExpression()
            id.identifier = parseString(expr).joinToString("");
            return id;
        }
        "assignment" -> {
            val assigment = AssigmentExpression();
            expr.forEach {
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
            throw IllegalArgumentException("Not supported expression\n\n-> ${expr.token}\n${expr}")
        }
    }

    return ExpressionEntity();
}

fun parseStatement(statement: KPS): StatementEntity {
    val ent = StatementEntity()
    statement.forEach {
        ent.expressions.add(parseExpression(it))
    }
    return ent
}