# 生成转换方法

在两个具有匹配字段的类之间生成转换方法。

## 用法

把光标放在方法体内，按 **Alt+Enter**（macOS 上是 **Option+Enter**），选择 **Generate setter getter from function**。

### 生成前

```java
public UserDto convert(User user) {
    // 光标在这里
}
```

### 生成后

```java
public UserDto convert(User user) {
    UserDto userDto = new UserDto();
    userDto.setName(user.getName());
    userDto.setEmail(user.getEmail());
    userDto.setAge(user.getAge());
    return userDto;
}
```

## 演示

![生成转换方法](/screenshot/generate_the_conveter.gif)

## 工作原理

1. 插件检测方法参数和返回类型
2. 通过名称匹配两个类之间的字段
3. 在结果对象上生成 setter 调用，从源对象的 getter 获取值
4. 匹配不区分大小写，支持常见的字段命名模式

## 提示

- 源类和目标类有相似命名的字段时效果最佳
- 可以有多方法参数 — 插件会找到最佳匹配
- 生成后，可以对未自动匹配的字段手动调整映射