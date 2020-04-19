package io.mrlokop.kotlin.utils.conventer.exceptions

class UnresolvedReference(val reference: String) : Throwable("Unresolved reference: '${reference}'")