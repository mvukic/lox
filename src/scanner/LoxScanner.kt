package scanner

import constants.LOX_KEYWORDS
import error.LoxErrorHandler
import model.LoxToken
import model.TokenType

class LoxScanner(val source: String) {
    private val tokens = mutableListOf<LoxToken>()
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): List<LoxToken> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }
        tokens.add(LoxToken(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        when (val c = advance()) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '/' -> when {
                match('/') -> while (peek() != '\n' && !isAtEnd()) advance()
                else -> addToken(TokenType.SLASH)
            }

            ' ', '\r', '\t' -> {}
            '\n' -> line++
            '"' -> string()
            'o' -> when {
                match('r') -> addToken(TokenType.OR)
            }

            else -> when {
                isNumeric(c) -> number()
                isAlpha(c) -> identifier()
                else -> LoxErrorHandler.error(line, "Unexpected character '$c'")
            }
        }
    }

    private fun peek() = if (isAtEnd()) null else source[current]
    private fun peekNext() = if (current + 1 >= source.length) null else source[current + 1]
    private fun isAtEnd() = current >= source.length
    private fun isNumeric(c: Char?) = c?.isDigit() == true
    private fun isAlpha(c: Char?) = c?.isLetterOrDigit() == true || c == '_'
    private fun isAlphanumeric(c: Char?) = isAlpha(c) || isNumeric(c)
    private fun advance() = source[current++]
    private fun addToken(type: TokenType) = addToken(type, null)

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(LoxToken(type, text, literal, line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()) {
            LoxErrorHandler.error(line, "Unterminated string")
            return
        }

        advance()

        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun number() {
        while (isNumeric(peek())) advance()
        if (peek() == '.' && isNumeric(peekNext())) advance()
        while (isNumeric(peek())) advance()

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun identifier() {
        while (isAlphanumeric(peek())) advance()

        val text = source.substring(start, current)
        val type = LOX_KEYWORDS[text] ?: TokenType.IDENTIFIER
        addToken(type)
    }

}