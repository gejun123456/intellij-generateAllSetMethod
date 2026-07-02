# Postfix Templates

Postfix templates offer the fastest way to generate code — just type a suffix after a variable name and press Tab or Enter.

## Available Templates

| Template | Description | Example |
|----------|-------------|---------|
| `.allset` | Generate all setters with default values | `user.allset` → `user.setName(""); user.setAge(0);` |
| `.allsetn` | Generate all setters without default values | `user.allsetn` → `user.setName(); user.setAge();` |
| `.allget` | Generate all getters with variable assignments | `user.allget` → `String name = user.getName();` |
| `.allaccessor` | Generate chained accessor setter calls | `user.allaccessor` → `user.setName("").setAge(0);` |
| `.allbuilder` | Generate full builder chain | `User.allbuilder` → `User.builder().name("").age(0).build();` |

## How to Use

1. Type a variable name followed by one of the postfix suffixes:

```java
user.allset
```

2. Press **Tab** or **Enter**
3. The template expands immediately

## Demo

![Generate All Setter](/screenshot/generateSetterPlugin.gif)

## Requirements

Postfix templates are currently available for **Java** files. Support requires IDE build version >= 213.

## Tips

- Postfix templates are faster than the Alt+Enter intention actions for quick code generation
- They work with any variable in scope — no need to position the cursor on the type declaration
- `.allbuilder` works with the class name directly (e.g., `User.allbuilder`), not a variable