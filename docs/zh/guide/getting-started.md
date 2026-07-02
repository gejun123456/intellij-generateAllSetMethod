# 安装指南

## 前提条件

- JetBrains IDE，构建版本 >= 213（IntelliJ IDEA 2021.3 或更高版本）
- Java 或 Kotlin 项目

## 从 JetBrains 市场安装

1. 打开你的 IDE
2. 进入 **Preferences / Settings** → **Plugins**
3. 点击 **Marketplace** 标签
4. 搜索 **"GenerateAllSetter"**
5. 点击 **Install**
6. 重启 IDE

## 手动安装

1. 从 [GitHub Releases](https://github.com/gejun123456/intellij-generateAllSetMethod/releases/latest) 下载最新版本的插件 ZIP 文件
2. 进入 **Preferences / Settings** → **Plugins**
3. 点击齿轮图标 ⚙️ → **Install Plugin from Disk...**
4. 选择下载的 ZIP 文件
5. 重启 IDE

## 验证安装

重启后，打开一个包含有 setter 的类的 Java 文件。输入：

```java
User user = new User();
```

把光标放在 `User` 上，按 **Alt+Enter**（macOS 上是 **Option+Enter**），你应该能看到 **Generate all setter** 在意图操作列表中。

## 更新

如果在 **Settings → Appearance & Behavior → System Settings → Updates** 中启用了 **Auto-detect plugin updates**，插件会自动更新。