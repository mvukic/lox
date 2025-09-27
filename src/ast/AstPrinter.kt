package ast

import model.*


class AstPrinter : ExpressionVisitor<String> {

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

    private fun parenthesize(name: String, vararg expressions: LoxExpression) = buildString {
        append("(")
        append(name)
        for (expression in expressions) {
            append(" ")
            append(expression.accept(this@AstPrinter))
        }
        append(")")
    }


}