package uk.co.judy.lox;

import java.util.List;

public abstract class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {

    void print(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                System.out.println(statement.accept(this));
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt) {
        return parenthesize("expr", stmt.expression);
    }

    @Override
    public String visitVarStmt(Stmt.Var stmt) {
        StringBuilder builder = new StringBuilder();

        builder.append("(var").append(" ");
        builder.append(stmt.name.lexeme).append(" ");
        builder.append(stmt.initializer.accept(this));
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitPrintStmt(Stmt.Print stmt) {
        return parenthesize("print", stmt.expression);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        StringBuilder builder = new StringBuilder();

        builder.append("(=").append(" ");
        builder.append(expr.name.lexeme).append(" ");
        builder.append(expr.value.accept(this));
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) {
            return "nil";
        }
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return "";
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }

//    public static void main(String[] args) throws IOException {
//        Expr expression = new Expr.Binary(
//                new Expr.Unary(new Token(TokenType.MINUS, "-", null, 1),
//                new Expr.Literal(123)),
//            new Token(TokenType.STAR, "*", null, 1),
//                new Expr.Grouping(new Expr.Literal(45.67)));
//        new AstPrinter().print(expression);
//    }
}
