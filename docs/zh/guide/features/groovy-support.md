# Groovy 支持

GenerateAllSetter 也支持 Groovy 类。

## Groovy 属性

Groovy 会自动为属性生成 setter 和 getter。插件可以检测 Groovy 类并生成相应的 setter 调用。

### 示例

```groovy
class User {
    String name
    int age
    String email
}
```

以及变量声明：

```groovy
User user = new User()
```

插件将生成：

```groovy
user.setName("")
user.setAge(0)
user.setEmail("")
```

## 要求

- 你的 IDE 必须启用 Groovy 插件
- `org.intellij.groovy` 依赖在插件中被标记为可选