package io.mrlokop.kotlin.utils.conventer.enities

import io.mrlokop.kotlin.utils.conventer.utils.ConverterScope

abstract class AbstractEntity(val entName: String) {
    lateinit var scope: ConverterScope.EntryScope
}