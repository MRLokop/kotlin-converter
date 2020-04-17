package io.mrlokop.kotlin.utils.conventer

import io.mrlokop.kotlin.utils.conventer.enities.EntryEntity
import io.mrlokop.kotlin.utils.conventer.parsers.parsePackage
import io.mrlokop.kotlin.utils.conventer.parsers.parseTopLevel
import io.mrlokop.kotlin.utils.conventer.utils.ConverterScope
import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTreeNodeType
import java.lang.reflect.Field

open class Converter(val tree: KotlinParseTree) {
    val converterScope = ConverterScope();
    fun parse(): EntryEntity {
        println(tree)
        return parse(KPS(tree), 0)
    }

    fun parse(kpt: KPS, iterations: Int): EntryEntity {
        val entry = EntryEntity();
        kpt.children.map {
            return@map parseRule(it, iterations, entry)
        }
        return entry;
    }

    private fun parseRule(kp: KPS, iterations: Int, entry: EntryEntity): EntryEntity {
        println("DEBUG Parsing: ${kp.token}")
        when (kp.token) {
            "packageHeader" -> {
                entry.packageName = parsePackage(kp);
                println("ParsedPackage: ${entry.packageName}")
            }
            "topLevelObject" -> {
                val t = parseTopLevel(kp);
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
        return entry;
    }


    companion object {
        lateinit var nameField: Field;
        lateinit var textField: Field;

        init {

            mutableListOf(*KotlinParseTree::class.java.fields, *KotlinParseTree::class.java.declaredFields).forEach {
                when (it.name) {
                    "text" -> textField = it;
                    "name" -> nameField = it;
                }
            }

            if (nameField == null) {
                throw NullPointerException("Failed to get KotlinParseTree.name field")
            }
            if (textField == null) {
                throw NullPointerException("Failed to get KotlinParseTree.text field")
            }
            nameField.isAccessible = true;
            textField.isAccessible = true;
            println("[Converters] Reflection ok")
        }
    }
}

class KPS(private val kps: KotlinParseTree) {
    var peek = -1;

    fun peek(name: String, cb: (ksp: KPS) -> Unit) : Boolean {
        if ((peek + 1) < children.size) {
            val c = children[peek + 1]
            if (c.token == name) {
                cb(c)
                peek++;
                return true
            }
            return false
        }
        return false
    }
    fun peek(name: String) : KPS? {
        if ((peek + 1) < children.size) {
            val c = children[peek + 1]
            if (c.token == name) {
                peek++;
                return c
            }
        }
        return null
    }

    fun get(name: String, cb: (it: KPS) -> Unit) {
        children.subList(peek, children.size - peek).forEach {
            if (it.token == name) {
                cb(it)
            }
        }
    }

    val children = kps.children.map {
        return@map KPS(it)
    }
    val type: KotlinParseTreeNodeType = kps.type

    val token: String
        get() {
            return Converter.nameField.get(kps) as String
        }
    val text: String
        get() {
            return Converter.textField.get(kps) as String
        }

    var forEach = children::forEach

    override fun toString(): String {
        return kps.toString();
    }

    fun getOne(name: String): KPS {
        for (child in children) {
            if (child.token === name) {
                return child
            }
        }
        throw java.lang.NullPointerException("Failed to found '$name' in \n\n-> ${token}:\n $this")
    }

    fun peekIfExists(name: String): Boolean {
        if (!peek(name) {}) {
            peek++
            return false;
        }
        return true;
    }

    fun has(s: String): Boolean {
        for (child in children) {
            if (child.token == s)
                return true
        }
        return false;
    }

}


fun debug(vararg any: Any) {
    print("DEBUG  ")
    any.forEach {
        print(it)
        print(" ")
    }
    println()
}

fun warn(vararg any: Any) {
    print("WARN   ")
    any.forEach {
        print(it)
        print(" ")
    }
    println()
}

fun info(vararg any: Any) {
    print("INFO   ")
    any.forEach {
        print(it)
        print(" ")
    }
    println()
}