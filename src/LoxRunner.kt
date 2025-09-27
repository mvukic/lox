import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess


class LoxRunner {

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
    }

    private fun run(source: String) {
        val scanner = LoxScanner(source)
        val tokens = scanner.scanTokens()

        for (token in tokens) {
            println(token)
        }
    }

}