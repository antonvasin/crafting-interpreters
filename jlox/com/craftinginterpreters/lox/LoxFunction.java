package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final String name;
  private final Expr.Function declaration;
  private final Environment closure;

  LoxFunction(String name, Expr.Function declaration, Environment closure) {
    this.name = name;
    this.closure = closure;
    this.declaration = declaration;
  }

  LoxFunction bind(LoxInstance instance) {
    Environment environment = new Environment(closure);
    environment.define("this", instance);
    return new LoxFunction(name, declaration, environment);
  }

  @Override
  public String toString() {
    if (name == null) return "<fn>";
    return "<fn " + name + ">";
  }

  @Override
  public int arity() {
    return this.declaration.params.size();
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment environment = new Environment(closure);

    for (int i = 0; i < declaration.params.size(); i++) {
      environment.define(declaration.params.get(i).lexeme, arguments.get(i));
    }

    // We get return value by catching special exception and returning it's
    // value. This is needed because `return` can occur anywhere in the function
    // so we need a way to jump to the bottom of the call stack from anywhere.
    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      return returnValue.value;
    }

    return null;
  }
}
