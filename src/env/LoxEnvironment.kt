package env

import error.LoxRuntimeError
import model.LoxToken

class LoxEnvironment {

    private val values = mutableMapOf<String, Any?>()

    fun define(key: String, value: Any?) {
        values[key] = value
    }

    fun get(name: LoxToken): Any? {
        if (values.containsKey(name.lexeme)) return values[name.lexeme]

        throw LoxRuntimeError(name, "Undefined variable \"${name.lexeme}\"")
    }

}