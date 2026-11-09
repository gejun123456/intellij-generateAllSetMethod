# 复杂返回值类型

为方法生成 List、Set、Map 和 Optional 等复杂返回类型的默认返回值。

## 用法

把光标放在方法体内，按 **Alt+Enter**（macOS 上是 **Option+Enter**），选择 **Generate all setter**。

### List 返回类型

```java
public List<User> getUsers() {
    // 光标在这里
}
```

生成后：

```java
public List<User> getUsers() {
    return new ArrayList<>();
}
```

### Set 返回类型

```java
public Set<String> getNames() {
    // 光标在这里
}
```

生成后：

```java
public Set<String> getNames() {
    return new HashSet<>();
}
```

### Map 返回类型

```java
public Map<String, User> getUserMap() {
    // 光标在这里
}
```

生成后：

```java
public Map<String, User> getUserMap() {
    return new HashMap<>();
}
```

### Optional 返回类型

```java
public Optional<User> findUser(String id) {
    // 光标在这里
}
```

生成后：

```java
public Optional<User> findUser(String id) {
    return Optional.empty();
}
```

## 演示

![复杂返回值类型](/screenshot/generate_list_default_value.gif)

## 提示

- 插件会自动插入正确的泛型类型参数
- import 语句会自动添加
- 支持任意深度的泛型（如 `Map<String, List<User>>`）