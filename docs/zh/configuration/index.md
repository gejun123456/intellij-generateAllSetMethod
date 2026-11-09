# 配置说明

你可以通过设置面板自定义插件的行为。

## 访问设置

进入 **Preferences / Settings** → **Tools** → **GenerateAllSetter**。

或者在设置对话框中搜索 "GenerateAllSetter"。

## 配置选项

### 默认值模板

你可以为不同类型配置自定义默认值。详见[模板自定义](#模板自定义)部分。

### Guava 代码生成

启用后，插件将使用 Guava 集合类型（如 `Lists.newArrayList()`）替代标准 Java 集合构造器。

可以在设置面板的 **Guava Support** 中切换此选项。

### 测试框架自动检测

插件会自动检测项目使用的测试框架：

- AssertJ
- JUnit 4
- JUnit 5
- TestNG
- JDK assert

在设置面板中可以查看当前检测到的引擎。

## 模板自定义

插件支持通过 Velocity 模板进行自定义模板。你可以定义模板以特定模式生成代码。

### 使用模板

1. 进入 **Settings → Tools → GenerateAllSetter**
2. 在模板编辑器中创建或修改模板
3. 当光标在变量上时，使用 **Generate by template** 意图操作

### 模板变量

| 变量 | 描述 |
|------|------|
| `$className` | 变量的类名 |
| `$fields` | 字段列表（包含名称和类型） |
| `$variableName` | 变量名 |