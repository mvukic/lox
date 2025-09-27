package env

import error.LoxRuntimeError
import model.LoxToken

class LoxEnvironment(val enclosing: LoxEnvironment? = null) {

    private val values = mutableMapOf<String, Any?>()

    fun define(key: String, value: Any?) {
        values[key] = value
    }

    fun assign(name: LoxToken, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
            return
        }

        if (enclosing != null) {
            enclosing.assign(name, value)
            return
        }

        throw LoxRuntimeError(name, "Undefined variable \"${name.lexeme}\"")
    }

    fun get(name: LoxToken): Any? {
        if (values.containsKey(name.lexeme)) return values[name.lexeme]

        if (enclosing != null) return enclosing.get(name)

        throw LoxRuntimeError(name, "Undefined variable \"${name.lexeme}\"")
    }

}