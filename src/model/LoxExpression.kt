package model


sealed class LoxExpression {
    abstract fun <R> accept(visitor: ExpressionVisitor<R>): R
}

class Binary(val left: LoxExpression, val operator: LoxToken, val right: LoxExpression) : LoxExpression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitBinaryExpr(this)
    }
}

class Grouping(val expression: LoxExpression) : LoxExpression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitGroupingExpr(this)
    }
}

class Literal(val value: Any?) : LoxExpression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitLiteralExpr(this)
    }
}

class Unary(val operator: LoxToken, val right: LoxExpression) : LoxExpression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitUnaryExpr(this)
    }
}