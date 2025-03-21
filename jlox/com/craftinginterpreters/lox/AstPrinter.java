package com.craftinginterpreters.lox;

public class AstPrinter implements Expr.Visitor<String> {
  String print(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public String visitAssignExpr(Expr.Assign expr) {
    return parenthesize("= " + expr.name.lexeme, expr.value);
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
    if (expr.value == null)
      return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitLogicalExpr(Expr.Logical expr) {
    return parenthesize(expr.operator.lexeme, expr.left, expr.right);
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    return parenthesize(expr.operator.lexeme, expr.right);
  }

  @Override
  public String visitVariableExpr(Expr.Variable expr) {
    return expr.name.lexeme;
  }

  @Override
  public String visitFunctionExpr(Expr.Function expr) {
    StringBuilder builder = new StringBuilder();
    builder.append("(fun (");
    
    for (int i = 0; i < expr.params.size(); i++) {
      if (i > 0) builder.append(" ");
      builder.append(expr.params.get(i).lexeme);
    }
    
    builder.append(") ");
    
    // We would need to recursively visit the body statements here,
    // but to keep it simple we'll just indicate it's a body
    builder.append("body");
    
    builder.append(")");
    return builder.toString();
  }

  @Override
  public String visitCallExpr(Expr.Call expr) {
    StringBuilder builder = new StringBuilder();
    builder.append("(call ");
    builder.append(expr.callee.accept(this));
    
    for (Expr argument : expr.arguments) {
      builder.append(" ");
      builder.append(argument.accept(this));
    }
    
    builder.append(")");
    return builder.toString();
  }

  @Override
  public String visitGetExpr(Expr.Get expr) {
    return parenthesize("get", expr.object) + "." + expr.name.lexeme;
  }

  @Override
  public String visitSetExpr(Expr.Set expr) {
    return parenthesize("set " + expr.name.lexeme, expr.object, expr.value);
  }

  @Override
  public String visitThisExpr(Expr.This expr) {
    return "this";
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
}
