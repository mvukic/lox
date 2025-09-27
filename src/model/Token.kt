package model

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int
) {
    override fun toString() = buildString {
        append("Token")
        append("[ ")
        append("type=$type")
        if (lexeme.isNotEmpty()) append(" lexeme=$lexeme")
        if (literal != null) append(" literal=$literal")
        append(" ]")

    }
}