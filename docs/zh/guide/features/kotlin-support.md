# Kotlin 支持

GenerateAllSetter 除了 Java 类外，还支持 Kotlin 属性。

## Kotlin 属性

对于 Kotlin 类，插件使用 Kotlin 属性赋值语法生成 setter/getter 调用。

### 示例

对于 Kotlin data class：

```kotlin
data class User(
    var name: String = "",
    var age: Int = 0
)
```

以及变量声明：

```kotlin
val user = User()
```

插件将生成：

```kotlin
user.name = ""
user.age = 0
```

## 工作原理

插件通过属性访问器约定检测 Kotlin 类，并生成符合 Kotlin 风格的代码，而不是 Java 风格的 setter 调用。

## 要求

- 你的 IDE 必须启用 Kotlin 插件
- `org.jetbrains.kotlin` 依赖在插件中被标记为可选，因此不会与非 Kotlin 项目冲突