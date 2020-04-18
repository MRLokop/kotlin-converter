package io.mrlokop.kotlin.utils.conventer.utils

class ScriptBuilder {
    var script: String = ""
    var spacer = "    "
    var spacers = 0

    fun set(script: String) {
        this.script = script
    }

    operator fun inc(): ScriptBuilder {
        spacers++
        return this
    }

    operator fun dec(): ScriptBuilder {
        spacers--
        return this
    }

    fun spacer(): String = spacer.repeat(spacers)

    fun wrap(cb: ScriptBuilder.() -> Unit) {
        inc()
        cb(this)
        dec()
    }

    fun ln(): ScriptLine {
        unaryPlus()
        return ScriptLine(this)
    }

    fun ln(ln: String): ScriptBuilder {
        this + ln; return this
    }

    operator fun plus(s: String): ScriptBuilder {
        script += spacer() + s + "\n"
        return this
    }

    operator fun unaryPlus() {
        this + ""
    }

    operator fun String.unaryPlus(): ScriptLine {
        ln(this)
        return ScriptLine(this@ScriptBuilder)
    }

    override fun toString(): String {
        return script
    }

    class ScriptLine(var script: ScriptBuilder) {
        operator fun plus(s: String): ScriptLine {
            script.script = script.script.substringBeforeLast("\n") + s + "\n"
            return this
        }

        fun sub(i: Int): ScriptLine {
            var b = get()
            var a = script.script.substringBeforeLast("\n")

            script.script = a.substring(0, a.length - i) + "\n"
            debug("Sub from $b to ${get()}")
            return this
        }

        fun get(): String {
            return script.script.substringBeforeLast("\n").substringAfterLast("\n")
        }

        fun remove(): ScriptBuilder {
            script.script = script.script.substringBeforeLast("\n").substringBeforeLast("\n") + "\n"
            return script
        }
    }
}