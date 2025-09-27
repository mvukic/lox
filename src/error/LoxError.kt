package error

import model.LoxToken

class LoxParseError(val token: LoxToken, message: String) : RuntimeException(message)
class LoxRuntimeError(val token: LoxToken, message: String) : RuntimeException(message)