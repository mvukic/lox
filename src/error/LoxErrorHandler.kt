package error

import model.LoxToken
import model.TokenType


object LoxErrorHandler {
    var hadError = false
    var hadRuntimeError = false

    fun error(line: Int, message: String) {
        report(line, "", message)
    }

    fun error(token: LoxToken, message: String) {
        if (token.type === TokenType.EOF) {
            report(token.line, " at end", message)
        } else {
            report(token.line, " at '" + token.lexeme + "'", message)
        }
    }

    fun error(error: LoxRuntimeError) {
        println("${error.message} \n[line ${error.token.line}]")
        hadRuntimeError = true
    }

    fun error(error: LoxParseError) {
        println("${error.message} \n[line ${error.token.line}]")
    }

    private fun report(line: Int, where: String, message: String) {
        System.err.println("[line $line] Error $where: $message")
        hadError = true
    }
}