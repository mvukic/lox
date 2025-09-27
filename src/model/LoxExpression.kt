package model


sealed class LoxExpression {
    abstract fun <R> accept(visitor: ExpressionVisitor<R>): R
}

class BinaryExpression(val left: LoxExpression, val operator: LoxToken, val right: LoxExpression) : LoxExpression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitBinaryExpr(this)
    }
}

class GroupingExpression(val expression: LoxExpression) : LoxExpression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitGroupingExpr(this)
    }
}

class LiteralExpression(val value: Any?) : LoxExpression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitLiteralExpr(this)
    }
}

class UnaryExpression(val operator: LoxToken, val right: LoxExpression) : LoxExpression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitUnaryExpr(this)
    }
}

class VarExpression(val name: LoxToken) : LoxExpression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitVarExpression(this)
    }
}

class AssignExpression(val name: LoxToken, val value: LoxExpression) : LoxExpression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitAssignExpression(this)
    }
}