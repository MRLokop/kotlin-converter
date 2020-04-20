package io.mrlokop.kotlin.utils.conventer.utils

var minimumLoggingLevel = 2

fun debug(vararg any: Any) {
    if (minimumLoggingLevel > 1) return
    print("DEBUG  ")
    any.forEach {
        print(it)
        print(" ")
    }
    println()
}

fun trace(vararg any: Any) {
    if (minimumLoggingLevel > 0) return
    print("TRACE  ")
    any.forEach {
        print(it)
        print(" ")
    }
    println()
}

fun warn(vararg any: Any) {
    if (minimumLoggingLevel > 3) return
    print("WARN   ")
    any.forEach {
        print(it)
        print(" ")
    }
    println()
}

fun info(vararg any: Any) {
    if (minimumLoggingLevel > 2) return
    print("INFO   ")
    any.forEach {
        print(it)
        print(" ")
    }
    println()
}