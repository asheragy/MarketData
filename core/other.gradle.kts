val generateKarmaConfig = tasks.register("generateKarmaConfig") {
    val outputFile = file("karma.config.d/karma.config.generated.js")

    outputs.file(outputFile)

    outputFile.printWriter().use { writer ->
        writer.println("var PROJECT_PATH = '${projectDir.absolutePath.replace("\\", "\\\\")}';")
    }
}

