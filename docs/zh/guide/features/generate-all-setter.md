# 生成所有 Setter

为变量生成所有 setter 调用，并带有智能默认值。

## 用法

把光标放在变量声明或类型上，按 **Alt+Enter**（macOS 上是 **Option+Enter**），选择 **Generate all setter**。

### 生成前

```java
User user = new User();
```

### 生成后

```java
user.setName("");
user.setPassword("");
user.setAge(0);
user.setEmail("");
user.setActive(true);
```

## 演示

![生成所有 Setter](/screenshot/generate_setter_default_value.gif)

## 默认值

插件为常见类型生成合理的默认值：

| 类型 | 默认值 |
|------|--------|
| `String` | `""` |
| `int` / `Integer` | `0` |
| `long` / `Long` | `0L` |
| `double` / `Double` | `0.0` |
| `float` / `Float` | `0.0f` |
| `boolean` / `Boolean` | `true` |
| `List` | `new ArrayList<>()` |
| `Set` | `new HashSet<>()` |
| `Map` | `new HashMap<>()` |
| `BigDecimal` | `BigDecimal.ZERO` |
| 枚举类型 | 第一个枚举常量 |

## 提示

- 适用于变量类型关键字（如 `User user = new User()` 中的 `User`）
- 也适用于方法参数和字段声明
- 插件会自动添加必要的 import 语句