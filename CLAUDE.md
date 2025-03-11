# Crafting Interpreters - jLox Guide

## Build & Run Commands
- Build: `make jlox`
- Run REPL: `make repl` or `java -cp build/jlox com.craftinginterpreters.lox.Lox`
- Run file: `java -cp build/jlox com.craftinginterpreters.lox.Lox examples/filename.lox`
- Test: `make test` (runs examples/test.lox)
- Generate AST: `make generate_ast`
- Clean: `make clean`

## Code Style Guidelines
- Indentation: 2 spaces (no tabs)
- Line length: ~80 characters
- Braces: K&R style (opening brace on same line)
- Naming: camelCase for variables/methods, PascalCase for classes
- Error handling: static error reporting via `Lox.error()` methods
- Java imports: grouped by package, no wildcards
- Documentation: JavaDoc comments for classes and public methods
- Static analysis: Build uses `-Werror` flag