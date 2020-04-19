package io.mrlokop.kotlin.utils.conventer.exceptions

class UnresolvedReferenceException(val reference: String) : Throwable("Unresolved reference: '${reference}'")