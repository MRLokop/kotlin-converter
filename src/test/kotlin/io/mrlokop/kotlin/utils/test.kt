package io.mrlokop.kotlin.utils

import io.mrlokop.kotlin.utils.conventer.Converter
import io.mrlokop.kotlin.utils.conventer.enities.EntryEntity
import io.mrlokop.kotlin.utils.conventer.enities.IntPrimitiveEntity
import io.mrlokop.kotlin.utils.conventer.enities.expression.ConstantExpression
import io.mrlokop.kotlin.utils.conventer.enities.expression.StringExpression
import org.jetbrains.kotlin.spec.grammar.tools.parseKotlinCode
import org.jetbrains.kotlin.spec.grammar.tools.tokenizeKotlinCode
import org.junit.Assert.assertEquals
import org.junit.Test


class TestKotlinConverter {


    val file = """
package kotlin.converter.test;

val constInteger = 1;
var stringVariable = "Hello";

val typedConst: String = ""

inline fun main() {
    
}
"""

    private var entry: EntryEntity

    init {

        val tokens = tokenizeKotlinCode(file)
        val parseTree = parseKotlinCode(tokens)

        entry = Converter(parseTree).parse()

    }

    @Test
    fun testPackage() {
        assertEquals("kotlin.converter.test", entry.packageName)
    }

    @Test
    fun mainMethodExists() {
        var check = false
        entry.topLevels.forEach {
            it.declarations.forEach {
                it.functions.forEach {
                    if (it.name == "main")
                        check = true
                }
            }
        }

        assert(check)
    }

    @Test
    fun mainMethodInlineModifier() {
        var check = false
        entry.topLevels.forEach {
            it.declarations.forEach {
                it.functions.forEach {
                    it.mods.forEach {
                        if (it == "inline")
                            check = true
                    }
                }
            }
        }

        assert(check)
    }

    @Test
    fun intVariableIsVal() {
        var check = false
        entry.topLevels.forEach {
            it.declarations.forEach {
                it.fields.forEach {
                    if (it.name == "constInteger")
                        check = it.decType == "val"
                }
            }
        }

        assert(check)
    }

    @Test
    fun stringVariableIsVar() {
        var check = false
        entry.topLevels.forEach {
            it.declarations.forEach {
                it.fields.forEach {
                    if (it.name == "stringVariable")
                        check = it.decType == "var"
                }
            }
        }

        assert(check)
    }

    @Test
    fun stringVariableEquals() {
        var check = false
        entry.topLevels.forEach {
            it.declarations.forEach {
                it.fields.forEach {
                    if (it.name == "stringVariable") {
                        assert(it.expression is StringExpression)
                        assertEquals("Hello", (it.expression as StringExpression).get())
                        check = true
                    }
                }
            }
        }

        assert(check)
    }

    @Test
    fun intVariableEquals() {
        var check = false
        entry.topLevels.forEach {
            it.declarations.forEach {
                it.fields.forEach {
                    if (it.name == "constInteger") {
                        assert(it.expression is ConstantExpression)
                        assert((it.expression as ConstantExpression).const is IntPrimitiveEntity)
                        assertEquals(1, ((it.expression as ConstantExpression).const as IntPrimitiveEntity).get())
                        check = true
                    }
                }
            }
        }

        assert(check)
    }

    // Mark as test
    fun typeVariableTypeName() {
        var check = false
        entry.topLevels.forEach {
            it.declarations.forEach {
                it.fields.forEach {
                    if (it.name == "typedConst") {
                        assert(it.expression is StringExpression)
                        assert(it.type != null)
                        assertEquals("String", it.type!!.name)
                        check = true
                    }
                }
            }
        }
        assert(check)
    }
}