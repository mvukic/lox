package runner

import error.LoxErrorHandler
import interpreter.LoxInterpreter
import parser.LoxParser
import scanner.LoxScanner
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

class LoxRunner {
    private val interpreter = LoxInterpreter()

    fun repl() {
        println("REPL mode")

        while (true) {
            print("> ")
            val source = readlnOrNull() ?: break
            run(source)
            LoxErrorHandler.hadError = false
        }
    }

    fun file(path: String) {
        println("Executing source file: $path")
        val source = Files.readString(Paths.get(path))
        run(source)
        if (LoxErrorHandler.hadError) exitProcess(1)
        if (LoxErrorHandler.hadRuntimeError) exitProcess(1)
    }

    private fun run(source: String) {
        val tokens = LoxScanner(source).scanTokens()
        val statements = LoxParser(tokens).parse()

        if (LoxErrorHandler.hadError) return

        interpreter.interpret(statements)
    }

}