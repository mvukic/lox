import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    if (args.size > 1) {
        printHelp()
    }
    if (args.size == 1) {
        runFile(args[0])
    }
    runPrompt()
}

private fun printHelp() {
    println("Usage: klox [script]")
    exitProcess(0)
}

private fun runFile(path: String) {
    val source = Files.readString(Paths.get(path))
    runSource(source)
    if (LoxErrorHandler.hadError) exitProcess(1)
}

private fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
        print("> ")
        val source = reader.readLine() ?: break
        runSource(source)
        LoxErrorHandler.hadError = false
    }
}

private fun runSource(source: String) {
    val scanner = LoxScanner(source)
    val tokens = scanner.scanTokens()

    for (token in tokens) {
        println(token)
    }
}
