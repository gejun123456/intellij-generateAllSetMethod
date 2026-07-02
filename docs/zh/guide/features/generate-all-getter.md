# 生成所有 Getter

为给定对象生成所有 getter 调用，并附带变量声明。

## 用法

把光标放在变量类型上，按 **Alt+Enter**（macOS 上是 **Option+Enter**），选择 **Generate all getter**。

### 生成前

```java
User user = getUser();
```

### 生成后

```java
String name = user.getName();
String password = user.getPassword();
int age = user.getAge();
String email = user.getEmail();
boolean active = user.isActive();
```

非常适合调试、日志记录，或从对象中提取值。

## 相关功能

- [Getter 断言](./assert-getter) — 为所有 getter 方法生成断言语句
- [生成所有 Setter](./generate-all-setter) — 为对象生成 setter 调用