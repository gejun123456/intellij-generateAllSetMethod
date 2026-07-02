# Kotlin Support

GenerateAllSetter provides support for Kotlin properties in addition to Java classes.

## Kotlin Properties

For Kotlin classes, the plugin generates setter/getter calls using Kotlin property assignment syntax.

### Example

For a Kotlin data class:

```kotlin
data class User(
    var name: String = "",
    var age: Int = 0
)
```

With a variable declaration:

```kotlin
val user = User()
```

The plugin will generate:

```kotlin
user.name = ""
user.age = 0
```

## How It Works

The plugin detects Kotlin classes through their property accessor conventions and generates idiomatic Kotlin code instead of Java-style setter calls.

## Requirements

- The Kotlin plugin must be enabled in your IDE
- The `org.jetbrains.kotlin` dependency is marked as optional in the plugin, so it won't conflict with non-Kotlin projects