package io.mrlokop.kotlin.utils.conventer.parsers

import io.mrlokop.kotlin.utils.conventer.enities.EntryEntity
import io.mrlokop.kotlin.utils.conventer.utils.ConverterScope
import io.mrlokop.kotlin.utils.conventer.utils.TreeNode
import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import java.lang.reflect.Field

open class KParser(val tree: KotlinParseTree, var fileName: String = "undefined") {
    val converterScope = ConverterScope()
    fun parse(): EntryEntity {
        println(tree)
        return parse(TreeNode(tree), 0)
    }

    fun parse(kpt: TreeNode, iterations: Int): EntryEntity {
        val entry = EntryEntity()
        entry.fileName = fileName
        kpt.children.map {
            return@map parseRule(it, iterations, entry)
        }
        return entry
    }

    private fun parseRule(kp: TreeNode, iterations: Int, entry: EntryEntity): EntryEntity {
        println("DEBUG Parsing: ${kp.token}")
        when (kp.token) {
            "packageHeader" -> {
                entry.packageName = parsePackage(kp)
                println("ParsedPackage: ${entry.packageName}")
            }
            "topLevelObject" -> {
                val t = parseTopLevel(kp)
                entry.topLevels.add(t)
                //  println(entry.packageName);
            }
            "EOF" -> {
                println("Parsed")
            }
            else -> {
                parse(kp, iterations + 1)
            }
        }
        return entry
    }


    companion object {
        lateinit var nameField: Field
        lateinit var textField: Field

        init {

            mutableListOf(*KotlinParseTree::class.java.fields, *KotlinParseTree::class.java.declaredFields).forEach {
                when (it.name) {
                    "text" -> textField = it
                    "name" -> nameField = it
                }
            }

            if (nameField == null) {
                throw NullPointerException("Failed to get KotlinParseTree.name field")
            }
            if (textField == null) {
                throw NullPointerException("Failed to get KotlinParseTree.text field")
            }
            nameField.isAccessible = true
            textField.isAccessible = true
            println("[Converters] Reflection ok")
        }
    }
}
