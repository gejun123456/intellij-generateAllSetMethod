# Generate with Accessors

Generate chained setter calls (accessor style) for a variable.

## Usage

Place your cursor on the variable type, press **Alt+Enter** (or **Option+Enter** on macOS) and select **Generate all setter with accessors**.

### Before

```java
User user = new User();
```

### After

```java
user.setName("").setPassword("").setAge(0).setEmail("").setActive(true);
```

## Comparison

| Style | Result |
|-------|--------|
| Regular setter | Each setter on its own line |
| Accessor chain | All setters chained with `.` on one line |

The accessor style is more compact and can be useful when you're passing a fully configured object inline.

## Postfix Template

You can also use the `.allaccessor` postfix template:

```
user.allaccessor
```

Press **Tab** or **Enter** to expand into a chained accessor call.

See [Postfix Templates](./postfix-templates) for more details.