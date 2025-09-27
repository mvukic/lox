package parser

import error.LoxErrorHandler
import error.LoxParseError
import model.*

class LoxParser(val tokens: List<LoxToken>) {

    private var current = 0

    fun parse(): LoxExpression? {
        return try {
            expression()
        } catch (e: LoxParseError) {
            LoxErrorHandler.error(e)
            null
        }
    }

    private fun expression(): LoxExpression {
        return equality()
    }

    private fun equality(): LoxExpression {
        var expression = comparison()

        while (match(TokenType.BANG, TokenType.EQUAL)) {
            val operator = previous()
            val right = comparison()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    private fun comparison(): LoxExpression {
        var expression = term()
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    private fun term(): LoxExpression {
        var expression = factor()

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right = unary()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    private fun factor(): LoxExpression {
        var expression = unary()
        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    private fun unary(): LoxExpression {
        if (match(TokenType.MINUS, TokenType.BANG)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }

        return primary()
    }

    private fun primary(): LoxExpression {
        if (match(TokenType.FALSE)) return Literal(false)
        if (match(TokenType.TRUE)) return Literal(true)
        if (match(TokenType.NIL)) return Literal(null)

        if (match(TokenType.NUMBER, TokenType.STRING)) return Literal(previous().literal)

        if (match(TokenType.LEFT_PAREN)) {
            val expression = expression()
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression")
            return Grouping(expression)
        }

        throw error(peek(), "Expect expression.");
    }

    private fun consume(type: TokenType, message: String): LoxToken {
        if (check(type)) return advance()
        throw error(peek(), message)
    }

    private fun error(token: LoxToken, message: String): LoxParseError {
        LoxErrorHandler.error(token, message)
        return LoxParseError(token, message)
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun advance(): LoxToken {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun check(type: TokenType) = if (isAtEnd()) false else peek().type == type
    private fun peek() = tokens[current]
    private fun previous() = tokens[current - 1]
    private fun isAtEnd() = peek().type == TokenType.EOF

    private fun synchronize() {
        advance()

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return

            when (peek().type) {
                TokenType.CLASS -> return
                TokenType.FUN -> return
                TokenType.VAR -> return
                TokenType.FOR -> return
                TokenType.IF -> return
                TokenType.WHILE -> return
                TokenType.PRINT -> return
                TokenType.RETURN -> return
                else -> {}
            }

            advance()
        }
    }

}