package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final String name;
  private final Expr.Function declaration;
  private final Environment closure;
  private final boolean isInitializer;

  LoxFunction(String name, Expr.Function declaration, Environment closure, boolean isInitializer) {
    this.name = name;
    this.closure = closure;
    this.declaration = declaration;
    this.isInitializer = isInitializer;
  }

  LoxFunction bind(LoxInstance instance) {
    Environment environment = new Environment(closure);
    environment.define(instance);
    return new LoxFunction(name, declaration, environment, isInitializer);
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
      environment.define(arguments.get(i));
    }

    // We get return value by catching special exception and returning it's
    // value. This is needed because `return` can occur anywhere in the function
    // so we need a way to jump to the bottom of the call stack from anywhere.
    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      // This allows empty `return` statements inside initializers
      if (isInitializer) return closure.getAt(0, 0);
      return returnValue.value;
    }

    // Always return `this` from initializer
    if (isInitializer) return closure.getAt(0, 0);

    return null;
  }
}
