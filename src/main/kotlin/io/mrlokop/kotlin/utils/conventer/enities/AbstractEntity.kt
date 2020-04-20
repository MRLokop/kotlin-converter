package io.mrlokop.kotlin.utils.conventer.enities

import io.mrlokop.kotlin.utils.conventer.scopes.EntryScope


abstract class AbstractEntity(val entName: String) {
    lateinit var scope: EntryScope
}