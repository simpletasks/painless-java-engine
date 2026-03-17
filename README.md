# painless-java-engine
Painless → Java execution engine for document mutation.

## Overview

This project is a **demo implementation of a Painless-like scripting engine**, built to showcase the full processing pipeline:

- Tokenization (lexical analysis)
- Parsing into an Abstract Syntax Tree (AST)
- Compilation into executable commands
- Runtime execution

The implementation demonstrates how a simple scripting language can be designed and executed end-to-end.

---

## Supported Painless Subset

The current implementation supports a practical subset of Painless, including:

- variable declarations and assignments
- `if` / `if-else` / block statements / `return`
- path access via dot notation (`ctx._source.status`)
- map-style bracket access (`ctx._source['status']`)
- symbolic map/list initialization (`[:]`, `[]`, `['k': v]`, `[1,2,3]`)
- literals: `null`, string, integer, decimal, boolean
- binary operators: logical, equality, comparison, arithmetic
- unary operators: `!`, unary `+` / `-`, prefix/postfix `++` and `--`
- function calls, method calls, casts, and `instanceof`

---

## Scope

This is a **demo / showcase project**, focused on:

- language modeling
- AST design
- execution flow

It is not a full implementation of OpenSearch Painless.

## Example

```painless
if (ctx._source.status == null) {
  ctx._source.status = 'READY';
}

return ctx._source.status;
```

