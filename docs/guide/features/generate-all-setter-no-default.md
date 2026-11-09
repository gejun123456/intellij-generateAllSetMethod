# Generate All Setter (No Default Value)

Generate all setter calls without default values — useful when you want to explicitly set values to `null` or use the defaults defined by the class.

## Usage

Place your cursor on the variable declaration or type, press **Alt+Enter** (or **Option+Enter** on macOS) and select **Generate all setter no default value**.

### Before

```java
User user = new User();
```

### After

```java
user.setName(null);
user.setPassword(null);
user.setAge(0);
user.setEmail(null);
user.setActive(false);
```

Note that primitive types always receive a default value (`0` for numbers, `false` for booleans) since they cannot be `null`.

## When to Use This

- You want to explicitly set some fields while leaving others as `null`
- You're writing test data where `null` is the expected value
- You want to make the null assignment visible in the code