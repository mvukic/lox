import runner.LoxRunner
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val lox = LoxRunner()
    when (args.size) {
        0 -> lox.repl()
        1 -> lox.file(args[0])
        else -> help()
    }
}

private fun help() {
    println("Usage: klox [script]")
    exitProcess(0)
}
