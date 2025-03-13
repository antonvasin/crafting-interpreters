# crafting-interpreters

[Crafting Interpreters](https://craftinginterpreters.com) solution. [Complete book + code on GitHub](https://github.com/munificent/craftinginterpreters).

## Run

```shell
make
java -cp build/jlox com.craftinginterpreters.lox.Lox examples/basic.lox
```

## Challenges

### Chapter 3 _p34_

1. [x] Write some Lox programs.
2. How errors should be handled? Can be solved with error callback. Better to introduce `throw` keyword.
    - Are arrays supported? Can be implemented by user.
    - Is it blocking, or how can I wait for some task to finish? Use function argument for callback parameter.
    - Are modules supported? How can I split program in multiple files? Module support
3. For real world language:
    - async control flow
    - iterators/ranges
    - operator overloading
    - bitwise operators
    - Std lib stuff e.g., `Date`, `Map/Hash/Object`, FS, HTTP, testing

### Chapter 4 _p51_

1. Means they can't be parsed with just regular expressions. Likely because they use indentation for expressing blocks, which mean you can't say that, eg. ”everything inside braces is a block“
2. Ruby, CoffeScript when parsing ambiguous expressions like `a + b → a+b`, `a +b → a(+b)`. C ???
3. To have functional comments, eg. ignoring certain checker rules. (`@ts-ignore`)
4. Nesting is difficult because then you would have to write rule for `//` comments twice: as separate rule and inside the block comment

### Chapter 5 _p71_

1. It encodes access to some object or it's property: `4`, `foo`, `foo()`, `foo.bar(2+2, baz)`, etc:
    ```
    expr -> expr method
    method -> "(" ( expr ( "," expr )* )? ")"
    method -> "." IDENTIFIER
    method -> method method
    expr -> NUMBER
    expr -> IDENTIFIER
    ```
2. [ ] OOP-style pattern for functional language
3. [ ] Reverse polish notation Visitor.

### Chapter 6 _p87_

1. [x] Implement comma expressions with the same precedence and associativity as in C.
    ```
    expression →  comma ;
    comma -> equality ( "," equality )* ;
    equality →  comparison ( ( "!=" | "==" ) comparison )* ;
    ```
2. [ ] Implement ternary. Ternary has lower precedence than equality but higher than comma operator.
    Expressions between `?` and `:` are parsed as if they're parenthesised. It has right-to-left associativity.
    ```
    expression →  comma ;
    comma -> ternary ( "," ternary )* ;
    ternary -> equality ( "?" expression ":" equality)* ;
    equality →  comparison ( ( "!=" | "==" ) comparison )* ;
    ...
    ```
3. [ ] Add error production to handle each binary operator appearing without
   a left-hand operand.

### Chapter 7 _p102_

1. Comparing different types:
    - `String` and `Boolean` for `"1"` and `"0"`, possibly also `"true"` and
    `"false"` with `Boolean` taking precedence. Can be useful when working
    with command line arguments and environment variables.
    - `Number` and `Boolean` for `1` and `0`, Boolean takes precedence. Makes
    `0` more logical and an exception from "truthy" values.
    - `String` and `Number`, `String` coerced into `Number` when
    `Number` is on either side of an expression. Can be useful when dealing
    with mixed user input such as forms.
    - `String` and `Number` for sorting mixed types like in Ruby. Number always
    comes first in sort.
2. [ ] Implement casting to `String` in `"pancake" + 10 => "pancake10"`.
3. If we divide by 0 right now the result is `Infinity`. Since we don't have `Infinity` in our types currently it is better to raise `RuntimeError`.
    - JS, Lua return `Infinity` as well.
    - Python raises runtime error.
    - In C, Zig division by zero is UB.

### Chapter 8 _p124_

1. [x] Implement printing of expressions in interpreter.
2. [ ] throw error when accessing uninitialized variable
3. `var a = a + 2` is expected to assign local `a` to 3 (1 + 2) and print 3. Outer `a` should stay `1`.

### Chapter 9 _p141_

1. We can declare two functions `run_when_truthy` and `run_when_falsy`, evaluate our condition and call necessary function. In Smalltalk ["decision logic is expressed by sending messages to booleans, numbers and collections with blocks as arguments"](https://cuis-smalltalk.github.io/TheCuisBook/Control-flow-with-block-and-message.html).
2. Tail-call elimination as done in Lisp. This allows us to treat recursion the same as usual loop.
3. [x] Implement `break` statements.

### Chapter 10 _p162_

1. In Smalltalk there are _unary_, _binary_ and _keyword_ messages. Unary
   messages are just a word/name. Binary are operators such as `+`, `-`, `*`,
   etc. Keyword messages are using keywords as arguments: `42 between: 41 and: 43`
   and it's name is really a `#between:and:` message. If we'd pass other set of arguments
   then the message itself would be different.
2. Implement lambdas. We need to introduce new expression type for anonymous functions.
   We modify function declaration parsing to check whether we have function declaration or anonymous function.
3. Not correct because it shadows function parameters. Lox allows this but
   should not since parameters share the function scope and not wrapped in their own.
   JS also allows to shadow function arguments. C and Java don't. Generally it
   is a good idea to prohibit this altogether.
