# Groovy Support

GenerateAllSetter also works with Groovy classes.

## Groovy Properties

Groovy automatically generates setters and getters for properties. The plugin detects Groovy classes and generates appropriate setter calls.

### Example

```groovy
class User {
    String name
    int age
    String email
}
```

With a variable declaration:

```groovy
User user = new User()
```

The plugin will generate:

```groovy
user.setName("")
user.setAge(0)
user.setEmail("")
```

## Requirements

- The Groovy plugin must be enabled in your IDE
- The `org.intellij.groovy` dependency is marked as optional in the plugin