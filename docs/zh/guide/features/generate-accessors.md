# Accessor 链式调用

为变量生成链式 setter 调用（accessor 风格）。

## 用法

把光标放在变量类型上，按 **Alt+Enter**（macOS 上是 **Option+Enter**），选择 **Generate all setter with accessors**。

### 生成前

```java
User user = new User();
```

### 生成后

```java
user.setName("").setPassword("").setAge(0).setEmail("").setActive(true);
```

## 对比

| 风格 | 结果 |
|------|------|
| 普通 Setter | 每个 setter 独占一行 |
| Accessor 链 | 所有 setter 通过 `.` 链式连接在一行 |

Accessor 风格更紧凑，适合在需要内联传递完整配置对象时使用。

## Postfix 模板

你也可以使用 `.allaccessor` postfix 模板：

```
user.allaccessor
```

按 **Tab** 或 **Enter** 展开为链式 accessor 调用。

详见 [Postfix 模板](./postfix-templates)。