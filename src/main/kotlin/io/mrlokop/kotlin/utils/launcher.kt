package io.mrlokop.kotlin.utils

import com.google.gson.Gson
import io.mrlokop.kotlin.utils.conventer.JSConverter
import io.mrlokop.kotlin.utils.conventer.enities.EntryEntity
import io.mrlokop.kotlin.utils.conventer.parsers.KParser
import io.mrlokop.kotlin.utils.conventer.utils.minimumLoggingLevel
import io.mrlokop.kotlin.utils.conventer.utils.trace
import io.mrlokop.kotlin.utils.conventer.utils.warn
import org.apache.commons.io.FileUtils
import org.jetbrains.kotlin.spec.grammar.tools.parseKotlinCode
import org.jetbrains.kotlin.spec.grammar.tools.tokenizeKotlinCode
import java.io.File
import java.nio.charset.Charset

fun main(args: Array<String>) {
    println()
    println("  <> TRACE LOGS ARE ENABLED <>")
    println()
    minimumLoggingLevel = 0

    var dir = "inputs"
    if (args.isNotEmpty()) {
        dir = args[0]
    }
    trace("->> Entry directory: $dir")
    val entries = mutableListOf<EntryEntity>()
    val list = File(dir).listFiles()
    if (list.isEmpty()) {
        warn("No files in: $dir to be compiled")
        return
    }
    list.forEach {

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
