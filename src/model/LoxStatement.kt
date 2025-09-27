package model

sealed class LoxStatement {
    abstract fun <R> accept(visitor: StatementVisitor<R>): R
}

class ExpressionStatement(val expression: LoxExpression) : LoxStatement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visitExpressionStmt(this)
    }
}

class PrintStatement(val expression: LoxExpression) : LoxStatement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visitPrintStmt(this)
    }
}

class VarStatement(val name: LoxToken, val initializer: LoxExpression?) : LoxStatement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visitVarStmt(this)
    }
}

class BlockStatement(val statements: List<LoxStatement>) : LoxStatement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visitBlockStmt(this)
    }
}