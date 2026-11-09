# Builder 模式生成

为包含静态 `builder()` 方法的类生成 Builder 链式调用。

## 用法

把光标放在变量类型上，按 **Alt+Enter**（macOS 上是 **Option+Enter**），选择 **Generate all setter with builder**。

### 生成前

```java
User user = null;
```

### 生成后

```java
User user = User.builder()
    .name("")
    .password("")
    .age(0)
    .email("")
    .active(true)
    .build();
```

## Lombok @Builder 支持

该功能与 [Lombok 的 `@Builder`](https://projectlombok.org/features/Builder) 注解无缝配合。如果你的类使用了 `@Builder`，插件会检测到生成的 `builder()` 方法并创建相应的链式调用。

## Postfix 模板

你也可以使用 `.allbuilder` postfix 模板实现更快速的操作：

```
User.allbuilder
```

按 **Tab** 或 **Enter** 展开为完整的 Builder 链。

详见 [Postfix 模板](./postfix-templates)。