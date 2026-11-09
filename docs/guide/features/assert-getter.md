# Assert Getter

Generate assertion statements (assertThat, assertEquals, etc.) for all getter methods of an object.

## Usage

Place your cursor on the variable type, press **Alt+Enter** (or **Option+Enter** on macOS) and select **Generate Assert all getter**.

### Before

```java
User user = getUser();
// cursor on User type
```

### After (JUnit 5)

```java
assertEquals("name", user.getName());
assertEquals("password", user.getPassword());
assertEquals(0, user.getAge());
assertEquals("email", user.getEmail());
assertTrue(user.isActive());
```

## Supported Test Engines

The plugin auto-detects which testing framework your project uses:

| Engine | Example Output |
|--------|---------------|
| **AssertJ** | `assertThat(user.getName()).isEqualTo("name");` |
| **JUnit 4** | `assertEquals("name", user.getName());` |
| **JUnit 5** | `assertEquals("name", user.getName());` |
| **TestNG** | `assertEquals(user.getName(), "name");` |
| **JDK assert** | `assert user.getName() != null;` |

## Assert Not Null

There's also a dedicated **Assert not null** action that generates:

```java
assertNotNull(user.getName());
assertNotNull(user.getPassword());
```

This is useful when you just want to verify that values are not null without checking specific values.