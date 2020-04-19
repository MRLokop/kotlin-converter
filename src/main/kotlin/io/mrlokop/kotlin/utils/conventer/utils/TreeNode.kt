package io.mrlokop.kotlin.utils.conventer.utils

import io.mrlokop.kotlin.utils.conventer.parsers.KParser
import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTreeNodeType

class TreeNode(private val kps: KotlinParseTree) {
    var peek = -1

    fun peek(name: String, cb: (ksp: TreeNode) -> Unit): Boolean {
        if ((peek + 1) < children.size) {
            val c = children[peek + 1]
            if (c.token == name) {
                cb(c)
                peek++
                return true
            }
            return false
        }
        return false
    }

    fun peek(name: String): TreeNode? {
        if ((peek + 1) < children.size) {
            val c = children[peek + 1]
            if (c.token == name) {
                peek++
                return c
            }
        }
        return null
    }

    operator fun get(name: String, cb: (it: TreeNode) -> Unit) {
        children.subList(peek, children.size - peek).forEach {
            if (it.token == name) {
                cb(it)
            }
        }
    }

    val children = kps.children.map {
        return@map TreeNode(it)
    }
    val type: KotlinParseTreeNodeType = kps.type

    val token: String
        get() {
            return KParser.nameField.get(kps) as String
        }
    val text: String
        get() {
            return KParser.textField.get(kps) as String
        }
    val textSafe: String?
        get() {
            return KParser.textField.get(kps) as String?
        }
    var forEach = children::forEach

    override fun toString(): String {
        return "\n-> " + token + (if (textSafe != null) " ($text) " else "") + "\n" + kps.toString().prefix("    ")
    }

    fun getOne(name: String): TreeNode {
        for (child in children) {
            if (child.token === name) {
                return child
            }
        }
        throw java.lang.NullPointerException("Failed to found '$name' in \n\n$this")
    }

    fun peekIfExists(name: String): Boolean {
        if (!peek(name) {}) {
            peek++
            return false
        }
        return true
    }

    fun has(s: String): Boolean {
        for (child in children) {
            if (child.token == s)
                return true
        }
        return false
    }

    operator fun get(i: Int): TreeNode {
        return children[i]
    }

}

