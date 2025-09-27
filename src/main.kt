import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    when (args.size) {
        0 -> runPrompt()
        1 -> runFile(args[0])
        else -> printHelp()
    }
}

private fun runPrompt() {
    println("Running in interpreter mode")

    while (true) {
        print("> ")
        val source = readlnOrNull() ?: break
        runSource(source)
        LoxErrorHandler.hadError = false
    }
}

private fun runFile(path: String) {
    val source = Files.readString(Paths.get(path))
    runSource(source)
    if (LoxErrorHandler.hadError) exitProcess(1)
}

private fun runSource(source: String) {
    println("Running source file")
    val scanner = LoxScanner(source)
    val tokens = scanner.scanTokens()

    for (token in tokens) {
        println(token)
    }
}

private fun printHelp() {
    println("Usage: klox [script]")
    exitProcess(0)
}
