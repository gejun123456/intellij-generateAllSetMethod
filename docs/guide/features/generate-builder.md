# Generate with Builder

Generate a builder chain for classes that have a static `builder()` method.

## Usage

Place your cursor on the variable type, press **Alt+Enter** (or **Option+Enter** on macOS) and select **Generate all setter with builder**.

### Before

```java
User user = null;
```

### After

```java
User user = User.builder()
    .name("")
    .password("")
    .age(0)
    .email("")
    .active(true)
    .build();
```

## Lombok @Builder Support

This feature works seamlessly with [Lombok's `@Builder`](https://projectlombok.org/features/Builder) annotation. If your class uses `@Builder`, the plugin will detect the generated `builder()` method and create the appropriate chain.

## Postfix Template

You can also use the `.allbuilder` postfix template for an even faster workflow:

```
User.allbuilder
```

Press **Tab** or **Enter** to expand into a full builder chain.

See [Postfix Templates](./postfix-templates) for more details.