package model

interface ExpressionVisitor<R> {
    fun visitBinaryExpr(expr: Binary): R
    fun visitUnaryExpr(expr: Unary): R
    fun visitGroupingExpr(expr: Grouping): R
    fun visitLiteralExpr(expr: Literal): R
}