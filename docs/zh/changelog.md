# 更新日志

## 2.8.5

- **新功能**：新增 postfix 模板：`.allset`、`.allsetn`、`.allget`、`.allaccessor`、`.allbuilder` — 在变量后输入并按 Tab/Enter 展开为所有 setter/getter/builder 调用
- **修复**：Postfix 模板展开不再在生成的代码上方留下原始变量名
- **修复**：修复 AssertJ 测试引擎检测缺陷
- **新功能**：新增从变量生成 setter getter 转换器支持
- **改进**：优化缩进方法
- **改进**：在方法转换中添加 NPE 判断
- **改进**：提升最低支持的 IDE 版本到 2021.3（since-build 213）

## 2.8.4

- **新功能**：测试框架支持：AssertJ、JUnit 4/5、TestNG、JDK assert 自动检测
- **新功能**：新增在偏好设置中禁用 Guava 代码生成选项
- 感谢 [hza](https://github.com/hza) 的贡献

## 2.8.3

- **修复**：修复死锁问题

## 2.8.2

- **修复**：修复文档 null 异常

## 2.8.1

- **新功能**：支持 Groovy
- **改进**：更好的 Builder 支持
- **新功能**：支持 Accessor 链式调用

## 2.8

- **修复**：修复 2021.2 中的异常

## 2.7

- **修复**：修复意图操作文本
- **新功能**：枚举类型支持

## 2.6

- **新功能**：支持 Lombok builder 使用 `with`（感谢 [axesipov](https://github.com/axesipov)）

## 2.5

- **新功能**：兼容 2020 版本

## 2.4

- **修复**：修复从方法生成 getter setter 转换不起作用

## 2.3

- **修复**：修复空指针

## 2.2

- **新功能**：新增生成所有 setter 无默认值
- **新功能**：新增生成 assert all getter

## 2.1

- **新功能**：新增 Kotlin 基础支持
- **改进**：重命名 "generate all setter name" 为 "Generate all setter"
- **改进**：在 new 操作符中添加空格

## 1.9

- **改进**：Map 导入和某些包的默认值

## 1.8

- **改进**：支持 byte 类型

## 1.7

- **新功能**：支持两个 List 之间的转换

## 1.6

- **修复**：修复光标在构造方法上时的异常

## 1.5

- **新功能**：为 List、Set、Map 返回类型生成默认值

## 1.4

- **新功能**：可在方法上使用：方法参数包含相同值时会传递给对象

## 1.3

- **修复**：修复文档

## 1.2

- **修复**：修复 long 和 double 的后缀

## 1.1

- **新功能**：自动添加 import 到类