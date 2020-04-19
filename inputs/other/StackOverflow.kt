package io.mrlokop.kotlin.utils.conventer.utils

fun main() {
    fun echo(text: String) {
        return {
            console.log(text)
            echo(text)()
        }
    }
    echo("Hello")()
}