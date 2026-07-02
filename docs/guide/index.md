# Quick Start

GenerateAllSetter is an IntelliJ IDEA plugin that dramatically speeds up writing repetitive setter/getter calls, object converters, builder chains, and more.

## Installation

Open your IDE and go to:

**Preferences / Settings → Plugins → Marketplace → Search "GenerateAllSetter" → Install**

Or download the latest release from [GitHub Releases](https://github.com/gejun123456/intellij-generateAllSetMethod/releases/latest) and install from disk.

## Your First Generation

Let's say you have a class `User` with `setName()` and `setPassword()` methods, and you have a variable declaration:

```java
User user = new User();
```

Place your cursor on the variable type `User` and press **Alt+Enter** (or **Option+Enter** on macOS). Select **Generate all setter** — the plugin will produce:

```java
user.setName("");
user.setPassword("");
```

That's it! 🎉

## Supported IDEs

- IntelliJ IDEA (Ultimate & Community)
- Android Studio
- Other JetBrains IDEs with build version >= 213

## Supported Languages

- **Java** — full support
- **Kotlin** — properties support
- **Groovy** — basic support

## Next Steps

Browse the [Features](./features/generate-all-setter) section to discover all the plugin's capabilities, or check out [Postfix Templates](./features/postfix-templates) for an even faster way to generate code.