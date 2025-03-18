package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

public class LoxInstance {
  private LoxClass klass;
  private final Map<String, Object> fields = new HashMap<>();

  LoxInstance(LoxClass klass) {
    this.klass = klass;
  }

  Object get(Token name) {
    // Try to return field first
    if (fields.containsKey(name.lexeme)) {
      return fields.get(name.lexeme);
    }

    // Then try to find a method
    LoxFunction method = klass.findMethod(name.lexeme);
    if (method != null) return method.bind(this);

    throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
  }

  void set(Token name, Object value) {
    // Lox allows creation of fields in runtime so we just `put` our value
    fields.put(name.lexeme, value);
  }

  @Override
  public String toString() {
    return klass.name + " instance";
  }
}
