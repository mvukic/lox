package interpreter

import env.LoxEnvironment
import error.LoxErrorHandler
import error.LoxRuntimeError
import model.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class LoxInterpreter : ExpressionVisitor<Any?>, StatementVisitor<Unit> {
    private val environment = LoxEnvironment()

    fun interpret(statements: List<LoxStatement>) {
        try {
            for (statement in statements) {
                execute(statement)
            }
        } catch (e: LoxRuntimeError) {
            LoxErrorHandler.error(e)
        }
    }

    private fun execute(statement: LoxStatement) {
        statement.accept(this)
    }

    override fun visitBinaryExpr(expr: BinaryExpression): Any {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                return left - right
            }

            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                return left / right
            }

            TokenType.STAR -> {
                checkNumberOperands(expr.operator, left, right)
                return left * right
            }

            TokenType.PLUS -> return when {
                left is Double && right is Double -> left + right
                left is String && right is String -> left + right
                else -> throw LoxRuntimeError(expr.operator, "Operands must be two numbers or strings")
            }

            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                return left > right
            }

            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return left >= right
            }

            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                return left < right
            }

            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return left <= right
            }

            TokenType.BANG_EQUAL -> return !isEqual(left, right)
            TokenType.EQUAL_EQUAL -> return isEqual(left, right)

            else -> {}
        }

        error("Invalid binary expression")
    }

    override fun visitUnaryExpr(expr: UnaryExpression): Any {
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            TokenType.BANG -> return !isTruthy(right)
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                return -right
            }

            else -> {}
        }

        error("Invalid unary expression")
    }

    override fun visitGroupingExpr(expr: GroupingExpression): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: LiteralExpression): Any? {
        return expr.value
    }

    private fun evaluate(expr: LoxExpression): Any? {
        return expr.accept(this)
    }

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj is Boolean) return obj
        return true
    }

    private fun isEqual(left: Any?, right: Any?): Boolean {
        if (left == null && right == null) return true
        if (left == null) return false
        return left == right
    }

    @OptIn(ExperimentalContracts::class)
    private fun checkNumberOperand(operator: LoxToken, operand: Any?) {
        contract {
            returns() implies (operand is Double)
        }
        if (operand is Double) return
        throw LoxRuntimeError(operator, "Operand must be a number")
    }

    @OptIn(ExperimentalContracts::class)
    private fun checkNumberOperands(operator: LoxToken, left: Any?, right: Any?) {
        contract {
            returns() implies (left is Double && right is Double)
        }
        if (left is Double && right is Double) return
        throw LoxRuntimeError(operator, "Operands must be numbers")
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"

        if (obj is Double) {
            var text = obj.toString()
            if (text.endsWith(".0")) {
                text = text.dropLast(2)
            }
            return text
        }

        return obj.toString()
    }

    override fun visitExpressionStmt(statement: ExpressionStatement) {
        evaluate(statement.expression)
    }

    override fun visitPrintStmt(statement: PrintStatement) {
        println(stringify(evaluate(statement.expression)))
    }

    override fun visitVarStmt(statement: VarStatement) {
        var value: Any? = null
        if (statement.initializer != null) {
            value = evaluate(statement.initializer);
        }
        environment.define(statement.name.lexeme, value);
    }

    override fun visitVarExpression(expr: VarExpression): Any? {
        return environment.get(expr.name)
    }
}