---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: "GenerateAllSetter"
  text: "Supercharge Your IntelliJ Code Generation"
  tagline: "Generate all setter/getter calls, builder chains, object converters, and more — with a single keystroke."
  actions:
    - theme: brand
      text: "Get Started"
      link: /guide/
    - theme: alt
      text: "View on GitHub"
      link: "https://github.com/gejun123456/intellij-generateAllSetMethod"
    - theme: alt
      text: "Install from JetBrains Marketplace"
      link: "https://plugins.jetbrains.com/plugin/9360"

features:
  - title: "Generate All Setters"
    details: "Place your cursor on a variable and press Alt+Enter to generate all setter calls with smart default values."
    link: /guide/features/generate-all-setter
  - title: "Postfix Templates"
    details: "Type .allset, .allsetn, .allget, .allaccessor, or .allbuilder after a variable and press Tab to expand instantly."
    link: /guide/features/postfix-templates
  - title: "Object Converter"
    details: "Generate a converter method between two classes with matching fields — perfect for DTO/entity transformations."
    link: /guide/features/generate-converter
  - title: "Builder Support"
    details: "Generate full builder chains for classes with a static builder() method, including Lombok @Builder support."
    link: /guide/features/generate-builder
  - title: "Accessor Chain"
    details: "Generate chained setter calls like user.setName(\"\").setAge(0).setEmail(\"\") in one action."
    link: /guide/features/generate-accessors
  - title: "Kotlin & Groovy"
    details: "Full support for Kotlin properties and Groovy classes alongside Java."
    link: /guide/features/kotlin-support
---