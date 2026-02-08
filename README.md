# Dice

A terminal-based Lisp REPL with built-in dice rolling, built with Java 21 and Lanterna.

## Usage

The UI has two panels — an output log and an input bar. Press `Tab` to switch focus between them.

### Input Bar

- Type an expression and press `Enter` to evaluate
- `Up` / `Down` — command history
- `Alt+Left` — jump to start
- `Alt+Right` — jump to end of text

### Output Log

- `Up` / `Down` — scroll through output

## Language

Expressions use S-expression syntax.

### Arithmetic

```
(number/add 2 3)       => 5
(number/sub 10 4)      => 6
(number/mul 3 4)       => 12
(number/div 12 4)      => 3
```

### Dice Rolling

```
(roll 'd6')           => 2D6: 8
(roll '3d6+5')         => 3D6 + 5: 18
```

### Variables and Functions

```
(def x 10)
(number/add x 5)       => 15

(def double (lambda (n) (number/mul n 2)))
(double 7)             => 14
```

### Conditionals and Equality

```
(if true 1 2)                    => 1
(if (isEqual 1 2) "same" "diff") => diff
```

### Text

```
(text/concat "hello" " " "world") => hello world
```

### Type Checks

```
(type/isNumber 42)     => true
(type/isText 'hi')     => true
(type/isList ())       => true
(type/isAtom foo)      => true
```

### Built-in Commands

- `(quit)` — exit the application
- `(clear)` — clear the output log

## Building

```
./gradlew build
```

## Testing

```
./gradlew test
```

Coverage report is generated at `build/reports/jacoco/test/html/index.html`.
