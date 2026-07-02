# Getter 断言

为对象的所有 getter 方法生成断言语句（assertThat、assertEquals 等）。

## 用法

把光标放在变量类型上，按 **Alt+Enter**（macOS 上是 **Option+Enter**），选择 **Generate Assert all getter**。

### 生成前

```java
User user = getUser();
// 光标在 User 类型上
```

### 生成后（JUnit 5）

```java
assertEquals("name", user.getName());
assertEquals("password", user.getPassword());
assertEquals(0, user.getAge());
assertEquals("email", user.getEmail());
assertTrue(user.isActive());
```

## 支持的测试框架

插件会自动检测项目使用的测试框架：

| 框架 | 示例输出 |
|------|----------|
| **AssertJ** | `assertThat(user.getName()).isEqualTo("name");` |
| **JUnit 4** | `assertEquals("name", user.getName());` |
| **JUnit 5** | `assertEquals("name", user.getName());` |
| **TestNG** | `assertEquals(user.getName(), "name");` |
| **JDK assert** | `assert user.getName() != null;` |

## Assert Not Null

还有一个专门的 **Assert not null** 操作，会生成：

```java
assertNotNull(user.getName());
assertNotNull(user.getPassword());
```

当你想验证值不为 null 而不检查具体值时非常有用。