# Postfix 模板

Postfix 模板是最快速的代码生成方式 — 只需在变量名后输入后缀，按 Tab 或 Enter 即可。

## 可用模板

| 模板 | 描述 | 示例 |
|------|------|------|
| `.allset` | 生成所有 setter（带默认值） | `user.allset` → `user.setName(""); user.setAge(0);` |
| `.allsetn` | 生成所有 setter（无默认值） | `user.allsetn` → `user.setName(); user.setAge();` |
| `.allget` | 生成所有 getter（带变量赋值） | `user.allget` → `String name = user.getName();` |
| `.allaccessor` | 生成链式 accessor setter 调用 | `user.allaccessor` → `user.setName("").setAge(0);` |
| `.allbuilder` | 生成完整 Builder 链 | `User.allbuilder` → `User.builder().name("").age(0).build();` |

## 使用方法

1. 输入变量名，后跟一个 postfix 后缀：

```java
user.allset
```

2. 按 **Tab** 或 **Enter**
3. 模板立即展开

## 演示

![生成所有 Setter](/screenshot/generateSetterPlugin.gif)

## 要求

Postfix 模板目前适用于 **Java** 文件。需要 IDE 构建版本 >= 213。

## 提示

- 对于快速代码生成，Postfix 模板比 Alt+Enter 意图操作更快
- 适用于作用域内的任何变量 — 无需将光标定位到类型声明上
- `.allbuilder` 直接使用类名（如 `User.allbuilder`），而不是变量