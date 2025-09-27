package model


sealed class Expression {
    abstract fun <R> accept(visitor: ExpressionVisitor<R>): R
}

class Binary(val left: Expression, val operator: Token, val right: Expression) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitBinaryExpr(this)
    }
}

class Grouping(val expression: Expression) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitGroupingExpr(this)
    }
}

class Literal(val value: Any?) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitLiteralExpr(this)
    }
}

class Unary(val operator: Token, val right: Expression) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visitUnaryExpr(this)
    }
}