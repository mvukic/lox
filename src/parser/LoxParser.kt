package parser

import error.LoxErrorHandler
import error.LoxParseError
import model.*

class LoxParser(val tokens: List<LoxToken>) {

    private var current = 0

    fun parse(): List<LoxStatement> {
        val statements = mutableListOf<LoxStatement>()
        while (!isAtEnd()) {
            val decl = declaration()
            if (decl != null) {
                statements.add(decl)
            }
        }
        return statements
    }

    private fun declaration(): LoxStatement? {
        return try {
            if (match(TokenType.VAR)) return varDeclaration()
            statement()
        } catch (error: LoxParseError) {
            synchronize()
            null
        }
    }

    private fun varDeclaration(): LoxStatement {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name.")

        var initializer: LoxExpression? = null
        if (match(TokenType.EQUAL)) {
            initializer = expression()
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.")
        return VarStatement(name, initializer)
    }

    private fun statement(): LoxStatement {
        if (match(TokenType.PRINT)) return printStatement()
        if (match(TokenType.LEFT_BRACE)) return BlockStatement(block())
        return expressionStatement()
    }

    private fun block(): List<LoxStatement> {
        val statements = mutableListOf<LoxStatement>()

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            val decl = declaration()
            if (decl != null) {
                statements.add(decl)
            }
        }

        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");

        return statements
    }

    private fun printStatement(): LoxStatement {
        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after value.")
        return PrintStatement(value)
    }

    private fun expressionStatement(): LoxStatement {
        val expr = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return ExpressionStatement(expr)
    }

    private fun expression(): LoxExpression {
        return assignment()
    }

    private fun assignment(): LoxExpression {
        val expression = equality()

        if (match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assignment()

            if (expression is VarExpression) {
                return AssignExpression(expression.name, value)
            }

            error(equals, "Invalid assignment target.")
        }

        return expression
    }

    private fun equality(): LoxExpression {
        var expression = comparison()

        while (match(TokenType.BANG, TokenType.EQUAL)) {
            val operator = previous()
            val right = comparison()
            expression = BinaryExpression(expression, operator, right)
        }

        return expression
    }

    private fun comparison(): LoxExpression {
        var expression = term()
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expression = BinaryExpression(expression, operator, right)
        }

        return expression
    }

    private fun term(): LoxExpression {
        var expression = factor()

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right = unary()
            expression = BinaryExpression(expression, operator, right)
        }

        return expression
    }

    private fun factor(): LoxExpression {
        var expression = unary()
        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expression = BinaryExpression(expression, operator, right)
        }

        return expression
    }

    private fun unary(): LoxExpression {
        if (match(TokenType.MINUS, TokenType.BANG)) {
            val operator = previous()
            val right = unary()
            return UnaryExpression(operator, right)
        }

        return primary()
    }

    private fun primary(): LoxExpression {
        if (match(TokenType.FALSE)) return LiteralExpression(false)
        if (match(TokenType.TRUE)) return LiteralExpression(true)
        if (match(TokenType.NIL)) return LiteralExpression(null)

        if (match(TokenType.NUMBER, TokenType.STRING)) return LiteralExpression(previous().literal)

        if (match(TokenType.IDENTIFIER)) return VarExpression(previous())

        if (match(TokenType.LEFT_PAREN)) {
            val expression = expression()
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression")
            return GroupingExpression(expression)
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