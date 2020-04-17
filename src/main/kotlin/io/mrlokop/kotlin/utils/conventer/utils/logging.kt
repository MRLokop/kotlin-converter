package io.mrlokop.kotlin.utils.conventer.utils


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