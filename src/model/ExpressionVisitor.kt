package model

interface ExpressionVisitor<R> {
    fun visitBinaryExpr(expr: BinaryExpression): R
    fun visitUnaryExpr(expr: UnaryExpression): R
    fun visitGroupingExpr(expr: GroupingExpression): R
    fun visitLiteralExpr(expr: LiteralExpression): R
    fun visitVarExpression(expr: VarExpression): R
    fun visitAssignExpression(expr: AssignExpression): R
}