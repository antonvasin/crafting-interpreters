package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

public class Environment {
  final Environment enclosing;
  // We store variables as a list and address them by index.
  // Since we know each variable's index from the time of definition
  // we can use that instead of searching for the variable name in a Map.
  private final List<Object> values = new ArrayList<>();

  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  void define(Object value) {
    values.add(value);
  }

  Object getAt(int distance, int slot) {
    return ancestor(distance).values.get(slot);
  }

  void assignAt(int distance, int slot, Object value) {
    ancestor(distance).values.set(slot, value);
  }

  /*
   * Unwraps parent environments by certain distance
   */
  Environment ancestor(int distance) {
    Environment environment = this;
    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing;
    }
    return environment;
  }
}
