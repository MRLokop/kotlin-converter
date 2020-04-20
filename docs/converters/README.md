## Your own converter


#### Note:
> For development recommendation set minimumLoggingLevel to 0 or 1 (2 default) 
> It give you access to see logs (for resolving, exports and imports)

#### Logic is simple:
 
1 - Create list of files, and parse it:
```kotlin

val entries = mutableListOf<EntryEntity>()
val files = dir.listFiles() // Or any another method getting files
files.forEach {

    // Tokenize kotlin code using
    // kotlin-grammar library
    val tokens = tokenizeKotlinCode(
        // FileUtils from apache commons io
        // Replace this method, if you dont have 
        // Apache`s commons io lib
        FileUtils.readFileToString(it, Charset.defaultCharset())
    )

    // Parse kotlin tokens into tree
    val parseTree = parseKotlinCode(tokens)

    // Parse tree into entities
    entries.add(KParser(parseTree, it.name).parse())

}
// Use entries
// ...code...
```
2 - Construct primary scope and register entries
```kotlin
// Create scope
val scope = ConverterScope()

// Register all entries
entries.forEach { entry ->
    scope.makeEntryScope(entry)
}

// Compile all statements
entries.forEach { entry ->
    // All entries already registered in previous foreach
    // And we getting already exists scopes
    val entryScope = scope.makeEntryScope(entry)
    entryScope.compile()
    // compile method is using for process all exports
}

// Process imports in entry
entries.forEach { entry ->
    val entryScope = scope.makeEntryScope(entry)
    entryScope.processImports()
}
```

