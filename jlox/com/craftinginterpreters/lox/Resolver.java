package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/*
 * Resolver is an additional pass after parsing and before executing which
 * resolves all variables and makes some additional checks.
 * It acts as a kind of "interpreter" that visits only nodes that has to do
 * with variable resolution. It has no side-effects and no control flow.
 * All branches along with function bodies are visited.
 */
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
  private final Interpreter interpreter;
  private final Stack<Map<String, Variable>> scopes = new Stack<>();
  private FunctionType currentFunction = FunctionType.NONE;

  Resolver(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  private enum FunctionType { NONE, FUNCTION, METHOD }

  private class Variable {
    enum State { DECLARED, DEFINED, USED }
    private State state = State.DECLARED;
    private final Token name;

    Variable(Token name) {
      this.name = name;
    }

    public Token getName() {
      return name;
    }

    public boolean isDeclared() { return state == State.DECLARED; }
    public boolean isDefined() { return state == State.DEFINED; }
    public boolean isUsed() { return state == State.USED; }

    public void setDefined() {
      if (isDeclared()) state = State.DEFINED;
    }

    public void setUsed() {
      if (isDefined()) state = State.USED;
    }
  }

  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    beginScope();
    resolve(stmt.statements);
    endScope();
    return null;
  }

  @Override
  public Void visitClassStmt(Stmt.Class stmt) {
    declare(stmt.name);
    define(stmt.name);

    for (Stmt.Function method : stmt.methods) {
      FunctionType declaration = FunctionType.METHOD;
      resolveFunction(method.function, declaration);
    }

    return null;
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    declare(stmt.name);
    // We define function right away so it would be available in it's own inner scope
    define(stmt.name);
    resolveFunction(stmt.function, FunctionType.FUNCTION);
    return null;
  }

  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);
    if (stmt.elseBranch != null) resolve(stmt.elseBranch);
    return null;
  }

  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitReturnStmt(Stmt.Return stmt) {
    if (currentFunction == FunctionType.NONE) {
      Lox.error(stmt.keyword, "'return' is only allowed inside function body");
    }

    if (stmt.value != null) {
      resolve(stmt.value);
    }

    return null;
  }

  @Override
  public Void visitBreakStmt(Stmt.Break stmt) {
    return null;
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    // Separating declare and define allows us to handles cases such as `var a = 1; { var a = a; }`;
    declare(stmt.name);
    if (stmt.initializer != null) {
      resolve(stmt.initializer);
    }
    define(stmt.name);
    return null;
  }

  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    resolve(stmt.condition);
    resolve(stmt.body);
    return null;
  }

  @Override
  public Void visitAssignExpr(Expr.Assign expr) {
    resolve(expr.value);
    resolveLocal(expr, expr.name, false);;
    return null;
  }

  @Override
  public Void visitFunctionExpr(Expr.Function expr) {
    resolveFunction(expr, FunctionType.FUNCTION);
    return null;
  }

  @Override
  public Void visitBinaryExpr(Expr.Binary expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitCallExpr(Expr.Call expr) {
    resolve(expr.callee);

    for (Expr argument : expr.arguments) {
      resolve(argument);
    }

    return null;
  }

  @Override
  public Void visitGetExpr(Expr.Get expr) {
    // We only recurse into the expr to the left of the `.` since properties are dynamic and looked up in interpreter
    resolve(expr.object);
    return null;
  }

  @Override
  public Void visitGroupingExpr(Expr.Grouping expr) {
    resolve(expr.expression);
    return null;
  }

  @Override
  public Void visitLiteralExpr(Expr.Literal expr) {
    return null;
  }

  @Override
  public Void visitLogicalExpr(Expr.Logical expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitSetExpr(Expr.Set expr) {
    resolve(expr.value);
    resolve(expr.object);
    return null;
  }

  @Override
  public Void visitUnaryExpr(Expr.Unary expr) {
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitVariableExpr(Expr.Variable expr) {
    // Here we prohibit using a variable in it's own initializer by checking if it is only declared but not yet initialized
    if (!scopes.isEmpty() &&
        scopes.peek().containsKey(expr.name.lexeme) &&
        scopes.peek().get(expr.name.lexeme).isDeclared()) {
      Lox.error(expr.name, "Can't read local variable in its own initializer");
    }

    resolveLocal(expr, expr.name, true);
    return null;
  }

  private void resolve(Stmt stmt) {
    stmt.accept(this);
  }

  private void resolve(Expr expr) {
    expr.accept(this);
  }

  void resolve(List<Stmt> statements) {
    for (Stmt statement : statements) {
      resolve(statement);
    }
  }

  private void beginScope() {
    scopes.push(new HashMap<String, Variable>());
  }

  private void endScope() {
    Map<String, Variable> scope = scopes.pop();

    // We check for unused variables and report them as errors
    for (Map.Entry<String, Variable> entry : scope.entrySet()) {
      Variable scopeVariable = entry.getValue();
      if (!scopeVariable.isUsed()) {
        Lox.error(scopeVariable.getName(), "Unused variable '" + entry.getKey() + "'.");
      }
    }
  }

  private void declare(Token name) {
    if (scopes.isEmpty()) return;

    Map<String, Variable> scope = scopes.peek();
    if (scope.containsKey(name.lexeme)) {
      Lox.error(name, "Variable already exists");
    }

    scope.put(name.lexeme, new Variable(name));
  }

  private void define(Token name) {
    if (scopes.isEmpty()) return;
    scopes.peek().get(name.lexeme).setDefined();
  }

  private void resolveLocal(Expr expr, Token name, boolean isRead) {
    // We go down the stack and try to resolve variable in the nearest scope
    for (int i = scopes.size()-1; i >= 0; i--) {
      if (scopes.get(i).containsKey(name.lexeme)) {
        interpreter.resolve(expr, scopes.size()-1-i);

        // We mark function as used upon resolution to report unused errors later but only when it is beaing read
        if (isRead) {
          scopes.get(i).get(name.lexeme).setUsed();
        }
        return;
      }
    }
  }

  private void resolveFunction(Expr.Function function, FunctionType type) {
    FunctionType enclosingFunction = currentFunction;
    currentFunction = type;
    beginScope();
    for (Token param : function.params) {
      declare(param);
      define(param);
    }
    resolve(function.body);
    endScope();
    currentFunction = enclosingFunction;
  }
}
