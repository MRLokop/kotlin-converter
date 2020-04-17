package io.mrlokop.kotlin.utils.conventer.parsers

import io.mrlokop.kotlin.utils.conventer.KPS


fun parsePackage(kp: KPS): String {
    return kp.children.map {
        return@map it.children.map {
            return@map parseString(it).joinToString("")
        }.joinToString("")
    }.joinToString("")
}
