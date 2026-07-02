# Generate All Getter

Generate all getter calls with variable declarations for a given object.

## Usage

Place your cursor on the variable type, press **Alt+Enter** (or **Option+Enter** on macOS) and select **Generate all getter**.

### Before

```java
User user = getUser();
```

### After

```java
String name = user.getName();
String password = user.getPassword();
int age = user.getAge();
String email = user.getEmail();
boolean active = user.isActive();
```

This is perfect for debugging, logging, or extracting values from an object.

## Related Features

- [Assert Getter](./assert-getter) — generate assertion statements for all getter methods
- [Generate All Setter](./generate-all-setter) — generate setter calls for an object