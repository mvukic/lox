package ast

import model.*


class AstPrinter : ExpressionVisitor<String>, StatementVisitor<String> {

    fun print(expression: LoxExpression): String {
        return expression.accept(this)
    }

    override fun visitBinaryExpr(expr: BinaryExpression): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    override fun visitUnaryExpr(expr: UnaryExpression): String {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    override fun visitGroupingExpr(expr: GroupingExpression): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: LiteralExpression): String {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    override fun visitVarExpression(expr: VarExpression): String {
        return expr.name.lexeme
    }

    override fun visitAssignExpression(expr: AssignExpression): String {
        return parenthesize2("=", expr.name.lexeme, expr.value)
    }

    override fun visitExpressionStmt(statement: ExpressionStatement): String {
        return parenthesize(";", statement.expression)
    }

    override fun visitPrintStmt(statement: PrintStatement): String {
        return parenthesize("print", statement.expression)
    }

    override fun visitVarStmt(statement: VarStatement): String {
        if (statement.initializer == null) {
            return parenthesize2("var", statement.name);
        }

        return parenthesize2("var", statement.name, "=", statement.initializer)
    }

    override fun visitBlockStmt(statement: BlockStatement) = buildString {
        append("(block ")

        for (statement in statement.statements) {
            append(statement.accept(this@AstPrinter))
        }

        append(")")
    }

    private fun parenthesize(name: String, vararg expressions: LoxExpression) = buildString {
        append("(")
        append(name)
        for (expression in expressions) {
            append(" ")
            append(expression.accept(this@AstPrinter))
        }
        append(")")
    }

    private fun parenthesize2(name: String, vararg parts: Any?) = buildString {
        append("(")
        append(name)
        transform(parts)
        append(")")
    }

    private fun StringBuilder.transform(vararg parts: Any?) {
        for (part in parts) {
            append(" ")
            when (part) {
                is LoxExpression -> append(part.accept(this@AstPrinter))
                is LoxStatement -> append(part.accept(this@AstPrinter))
                is LoxToken -> append(part.lexeme)
                is MutableList<*> -> transform(*part.toTypedArray())
                else -> append(part)
            }
        }
    }
}
