package model

interface StatementVisitor<R> {
    fun visitExpressionStmt(statement: ExpressionStatement): R
    fun visitPrintStmt(statement: PrintStatement): R
    fun visitVarStmt(statement: VarStatement): R
}