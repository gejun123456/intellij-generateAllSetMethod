# Generate All Setter

Generate all setter calls for a variable with smart default values.

## Usage

Place your cursor on the variable declaration or type, then press **Alt+Enter** (or **Option+Enter** on macOS) and select **Generate all setter**.

### Before

```java
User user = new User();
```

### After

```java
user.setName("");
user.setPassword("");
user.setAge(0);
user.setEmail("");
user.setActive(true);
```

## Demo

![Generate All Setter](/screenshot/generate_setter_default_value.gif)

## Default Values

The plugin generates sensible default values for common types:

| Type | Default Value |
|------|---------------|
| `String` | `""` |
| `int` / `Integer` | `0` |
| `long` / `Long` | `0L` |
| `double` / `Double` | `0.0` |
| `float` / `Float` | `0.0f` |
| `boolean` / `Boolean` | `true` |
| `List` | `new ArrayList<>()` |
| `Set` | `new HashSet<>()` |
| `Map` | `new HashMap<>()` |
| `BigDecimal` | `BigDecimal.ZERO` |
| enum types | first enum constant |

## Tips

- Works on the variable type keyword (e.g., `User` in `User user = new User()`)
- Also works on method parameters and field declarations
- The plugin automatically adds any necessary imports to your file