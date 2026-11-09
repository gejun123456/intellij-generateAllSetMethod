# 快速开始

GenerateAllSetter 是一个 IntelliJ IDEA 插件，可以大幅提升编写重复性 setter/getter 调用、对象转换方法、Builder 链等代码的效率。

## 安装

打开你的 IDE，进入：

**Preferences / Settings → Plugins → Marketplace → 搜索 "GenerateAllSetter" → 安装**

或者从 [GitHub Releases](https://github.com/gejun123456/intellij-generateAllSetMethod/releases/latest) 下载最新版本，从磁盘安装。

## 第一次使用

假设你有一个包含 `setName()` 和 `setPassword()` 方法的 `User` 类，以及一个变量声明：

```java
User user = new User();
```

把光标放在变量类型 `User` 上，按 **Alt+Enter**，选择 **Generate all setter**，插件将生成：

```java
user.setName("");
user.setPassword("");
```

就是这么简单！🎉

## 支持的 IDE

- IntelliJ IDEA（Ultimate 和 Community）
- Android Studio
- 其他构建版本 >= 213 的 JetBrains IDE

## 支持的语言

- **Java** — 完整支持
- **Kotlin** — 属性支持
- **Groovy** — 基础支持

## 下一步

浏览[功能特性](./features/generate-all-setter)部分来了解所有功能，或者试试 [Postfix 模板](./features/postfix-templates)，这是更快速的代码生成方式。