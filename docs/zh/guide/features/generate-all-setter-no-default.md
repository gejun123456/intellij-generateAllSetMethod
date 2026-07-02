# 生成所有 Setter（无默认值）

生成所有 setter 调用但不带默认值 — 当你想将值显式设置为 `null` 或使用类本身定义的默认值时很有用。

## 用法

把光标放在变量声明或类型上，按 **Alt+Enter**（macOS 上是 **Option+Enter**），选择 **Generate all setter no default value**。

### 生成前

```java
User user = new User();
```

### 生成后

```java
user.setName(null);
user.setPassword(null);
user.setAge(0);
user.setEmail(null);
user.setActive(false);
```

注意，基本类型始终会得到默认值（数字为 `0`，boolean 为 `false`），因为它们不能为 `null`。

## 何时使用

- 你想显式设置某些字段，而让其他字段保持为 `null`
- 你在编写测试数据，期望值为 `null`
- 你想在代码中明确显示 null 赋值