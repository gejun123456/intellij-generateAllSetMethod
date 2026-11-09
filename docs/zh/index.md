---
layout: home

hero:
  name: "GenerateAllSetter"
  text: "让你的 IntelliJ 代码生成效率翻倍"
  tagline: "一键生成所有 setter/getter 调用、Builder 链式调用、对象转换方法等 — 一个快捷键搞定。"
  actions:
    - theme: brand
      text: "快速开始"
      link: /zh/guide/
    - theme: alt
      text: "在 GitHub 上查看"
      link: "https://github.com/gejun123456/intellij-generateAllSetMethod"
    - theme: alt
      text: "从 JetBrains 市场安装"
      link: "https://plugins.jetbrains.com/plugin/9360"

features:
  - title: "生成所有 Setter"
    details: "光标放在变量上，按 Alt+Enter 一键生成所有 setter 调用，带智能默认值。"
    link: /zh/guide/features/generate-all-setter
  - title: "Postfix 模板"
    details: "在变量后输入 .allset、.allsetn、.allget、.allaccessor 或 .allbuilder，按 Tab 键瞬间展开。"
    link: /zh/guide/features/postfix-templates
  - title: "对象转换方法"
    details: "在两个字段匹配的类之间生成转换方法 — DTO 与 Entity 转换利器。"
    link: /zh/guide/features/generate-converter
  - title: "Builder 支持"
    details: "为包含 builder() 方法的类生成完整 Builder 链，支持 Lombok @Builder。"
    link: /zh/guide/features/generate-builder
  - title: "Accessor 链式调用"
    details: "一键生成链式 setter 调用，如 user.setName(\"\").setAge(0).setEmail(\"\")。"
    link: /zh/guide/features/generate-accessors
  - title: "Kotlin & Groovy"
    details: "除 Java 外，全面支持 Kotlin 属性和 Groovy 类。"
    link: /zh/guide/features/kotlin-support
---