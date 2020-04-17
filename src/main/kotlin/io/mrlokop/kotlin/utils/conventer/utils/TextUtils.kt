package io.mrlokop.kotlin.utils.conventer.utils

import java.lang.StringBuilder

fun String.prefix(prefix: String) : String {
    val a = StringBuilder();
    split("\n").forEach {
        a.appendln(prefix + it)
    }
    return a.toString()
}