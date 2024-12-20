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

### 1

Means they can't be parsed with just regular expressions. Likely because they use indentation for expressing blocks, which mean you can't say that, eg. ”everything inside braces is a block“

### 2

Ruby, CoffeScript when parsing ambiguous expressions like `a + b → a+b`, `a +b → a(+b)`. C ???

### 3

To have functional comments, eg. ignoring certain checker rules. (`@ts-ignore`)

### 4

Nesting is difficult because then you would have to write rule for `//` comments twice: as separate rule and inside the block comment

## Chapter 5 p71

### 1

It encodes property access to an object and : `4`, `foo`, `foo()`, `foo.bar(2+2, baz)`, etc:
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

## Chapter 6 p91

### 1

Implement comma expressions with the same precedence and associativity as in C. Write the grammar.

```
expression →  comma ;
comma -> equality ( "," equality )* ;
equality →  comparison ( ( "!=" | "==" ) comparison )* ;

### 2

Ternary has lower precedence than equality but higher than comma operator. Expressions between `?` and `:` are parsed as if they're parenthesised. It has right-to-left associativity.

```
expression →  comma ;
comma -> ternary ( "," ternary )* ;
ternary -> equality ( "?" expression ":" equality)* ;
equality →  comparison ( ( "!=" | "==" ) comparison )* ;
...

==TODO: implement==

## Chapter 7 p102

### 1

Comparing different types:

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

### 2

Implement casting to `String` in `"pancake" + 10 => "pancake10"`.

### 3

If we divide by 0 right now the result is `Infinity`. Since we don't have
   `Infinity` in our types currently it is better to raise `RuntimeError`.

- JS, Lua return `Infinity` as well.
- Python raises runtime error.
- In C, Zig division by zero is UB.

## Chapter 8 p124

### 2

==TODO: throw error when accessing uninitialized variable==

### 3

```
var a = 1;
{
  var a = a + 2;
  print a;
}

```

`var a = a + 2` is expected to assign local `a` to 3 (1 + 2) and print 3. Outer `a` should stay `1`.
