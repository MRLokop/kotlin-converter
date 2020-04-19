package io.mrlokop.kotlin.utils

import com.google.gson.Gson
import io.mrlokop.kotlin.utils.conventer.JSConverter
import io.mrlokop.kotlin.utils.conventer.enities.EntryEntity
import io.mrlokop.kotlin.utils.conventer.parsers.KParser
import org.apache.commons.io.FileUtils
import org.jetbrains.kotlin.spec.grammar.tools.parseKotlinCode
import org.jetbrains.kotlin.spec.grammar.tools.tokenizeKotlinCode
import java.io.File
import java.nio.charset.Charset

fun main(args: Array<String>) {
    var dir = "inputs"
    if (args.isNotEmpty()) {
        dir = args[0]
    }
    val entries = mutableListOf<EntryEntity>()
    File(dir).listFiles().forEach {

        val tokens = tokenizeKotlinCode(
            FileUtils.readFileToString(it, Charset.defaultCharset())
        )
        val parseTree = parseKotlinCode(tokens)
        entries.add(KParser(parseTree, it.name).parse())

    }

    FileUtils.writeStringToFile(File("AST.json"), Gson().toJson(entries), Charset.defaultCharset())
    val converter = JSConverter(entries)

    FileUtils.writeStringToFile(File("output.js"), converter.convert(), Charset.defaultCharset())

    println()
    println()
    println(" Starting ... ")
    println("  __ __ __ __ __ __ __ __ __ __ __ __ __ __")
    println()

    val b = ProcessBuilder()
        .inheritIO()
        .directory(File("."))
        .command("/usr/bin/bash", "/home/mrlokop/IdeaProjects/KotlinUtils/run.sh")
    b.environment()
        .putAll(System.getenv())
    b.start().waitFor()

    println()
    println("  __ __ __ __ __ __ __ __ __ __ __ __ __ __")
    println()

}
