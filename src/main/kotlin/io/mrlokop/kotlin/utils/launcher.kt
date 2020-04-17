package io.mrlokop.kotlin.utils

import com.google.gson.Gson
import io.mrlokop.kotlin.utils.conventer.Converter
import io.mrlokop.kotlin.utils.conventer.JSConverter
import io.mrlokop.kotlin.utils.conventer.PHPConverter
import org.apache.commons.io.FileUtils
import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTreeNodeType
import org.jetbrains.kotlin.spec.grammar.tools.parseKotlinCode
import org.jetbrains.kotlin.spec.grammar.tools.tokenizeKotlinCode
import java.io.File
import java.nio.charset.Charset

fun main() {

    val tokens = tokenizeKotlinCode(
        FileUtils.readFileToString(File("index.kt"), Charset.defaultCharset())
    )
    val parseTree = parseKotlinCode(tokens)

    val entry = Converter(parseTree).parse()

    val converter = JSConverter(entry);
    FileUtils.writeStringToFile(File("output.js"), converter.convert(), Charset.defaultCharset());
    FileUtils.writeStringToFile(File("AST.json"), Gson().toJson(entry), Charset.defaultCharset());

    println();
    println();
    println(" Starting ... ");

    val b = ProcessBuilder()
        .inheritIO()
        .directory(File("."))
        .command("/usr/bin/bash", "/home/mrlokop/IdeaProjects/KotlinUtils/run.sh")
    b.environment()
        .putAll(System.getenv())
    b.start()

}
