## Chapter 3 p34

- How errors should be handled? Can be solved with error callback. Better to introduce `throw` keyword.
  - Are arrays supported? Can be implemented by user.
  - Is it blocking, or how can I wait for some task to finish? Use function argument for callback parameter.
  - Are modules supported? How can I split program in multiple files? Module support
- `Date`
  - `Map/Hash/Object`
  - async functions
  - modules and packages
  - bitwise operators
  - http, fs support

## Chapter 4 p55

1. Means they can't be parsed with just regular expressions. Likely because they use indentation for expressing blocks, which mean you can't say that, eg. ”everything inside braces is a block“
2. Ruby, CoffeScript when parsing ambiguous expressions like `a + b → a+b`, `a +b → a(+b)`. C ???
3. To have functional comments, eg. ignoring certain checker rules. (`@ts-ignore`)
4. Nesting is difficult because then you would have to write rule for `//` comments twice: as separate rule and inside the block comment

## Chapter 5 p71

1. It encodes property access to an object and : `4`, `foo`, `foo()`, `foo.bar(2+2, baz)`, etc:
   ```
   expr →  expr ( "(" ( expr ( " , " expr )* )? ")" | " . " IDENTIFIER )+
     | IDENTIFIER
     | NUMBER
   ```
   ```
   expr -> expr method
   method -> "(" ( expr ( "," expr )* )? ")"
   method -> "." IDENTIFIER
   method -> method method
   expr -> NUMBER
   expr -> IDENTIFIER
   ```
2. ???
3. ???
4. ???

## Chapter 6

1. Implement comma expressions with the same precedence and associativity as in
   C. Write the grammar.
   ```
   expression →  comma ;
   comma -> equality ( "," equality )* ;
   equality →  comparison ( ( "!=" | "==" ) comparison )* ;
   comparison →  term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
   term →  factor ( ( "-" | "+" ) factor )* ;
   factor →  unary ( ( "/" | "*" ) unary )* ;
   unary →  ( "!" | "-" ) unary | primary ;
   primary →  NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
   ```
