package io.mrlokop.kotlin.utils

import io.mrlokop.kotlin.utils.conventer.Converter
import io.mrlokop.kotlin.utils.conventer.enities.EntryEntity
import org.jetbrains.kotlin.spec.grammar.tools.parseKotlinCode
import org.jetbrains.kotlin.spec.grammar.tools.tokenizeKotlinCode
import org.junit.Assert.assertEquals
import org.junit.Test


public class TestKotlinConverter() {


    val file = """
package kotlin.converter.test;
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
        var check = false;
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
        var check = false;
        entry.topLevels.forEach {
            it.declarations.forEach {
                it.functions.forEach {
                    it.mods.forEach {
                        if (it == "inline")
                            check = true;
                    }
                }
            }
        }

        assert(check)
    }
}