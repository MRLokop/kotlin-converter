package io.mrlokop.kotlin.utils.conventer.parsers

import io.mrlokop.kotlin.utils.conventer.utils.TreeNode


fun parsePackage(kp: TreeNode): String {
    return kp.children.map {
        return@map it.children.map {
            return@map parseString(it).joinToString("")
        }.joinToString("")
    }.joinToString("")
}

fun parseImports(importList: TreeNode): List<String> {
    return importList.children.map {
        return@map it.children.map {
            return@map it.children.map {
                return@map parseString(it).joinToString("")
            }.joinToString("")
        }.joinToString("")
    }
}