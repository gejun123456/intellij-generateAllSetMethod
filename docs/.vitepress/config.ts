import { defineConfig } from 'vitepress'

const sidebarEn = [
  {
    text: 'Guide',
    items: [
      { text: 'Quick Start', link: '/guide/' },
      { text: 'Installation', link: '/guide/getting-started' },
      {
        text: 'Features',
        items: [
          { text: 'Generate All Setter', link: '/guide/features/generate-all-setter' },
          { text: 'Generate All Setter (No Default)', link: '/guide/features/generate-all-setter-no-default' },
          { text: 'Generate All Getter', link: '/guide/features/generate-all-getter' },
          { text: 'Generate Converter', link: '/guide/features/generate-converter' },
          { text: 'Generate with Builder', link: '/guide/features/generate-builder' },
          { text: 'Generate with Accessors', link: '/guide/features/generate-accessors' },
          { text: 'Postfix Templates', link: '/guide/features/postfix-templates' },
          { text: 'Complex Return Types', link: '/guide/features/complex-return-types' },
          { text: 'Assert Getter', link: '/guide/features/assert-getter' },
          { text: 'Kotlin Support', link: '/guide/features/kotlin-support' },
          { text: 'Groovy Support', link: '/guide/features/groovy-support' },
        ]
      },
    ]
  },
  { text: 'Configuration', link: '/configuration/' },
  { text: 'Changelog', link: '/changelog' },
  { text: 'Contributing', link: '/contributing' },
]

const sidebarZh = [
  {
    text: '指南',
    items: [
      { text: '快速开始', link: '/zh/guide/' },
      { text: '安装指南', link: '/zh/guide/getting-started' },
      {
        text: '功能特性',
        items: [
          { text: '生成所有 Setter', link: '/zh/guide/features/generate-all-setter' },
          { text: '生成所有 Setter（无默认值）', link: '/zh/guide/features/generate-all-setter-no-default' },
          { text: '生成所有 Getter', link: '/zh/guide/features/generate-all-getter' },
          { text: '生成转换方法', link: '/zh/guide/features/generate-converter' },
          { text: 'Builder 模式生成', link: '/zh/guide/features/generate-builder' },
          { text: 'Accessor 链式调用', link: '/zh/guide/features/generate-accessors' },
          { text: 'Postfix 模板', link: '/zh/guide/features/postfix-templates' },
          { text: '复杂返回值类型', link: '/zh/guide/features/complex-return-types' },
          { text: 'Getter 断言', link: '/zh/guide/features/assert-getter' },
          { text: 'Kotlin 支持', link: '/zh/guide/features/kotlin-support' },
          { text: 'Groovy 支持', link: '/zh/guide/features/groovy-support' },
        ]
      },
    ]
  },
  { text: '配置说明', link: '/zh/configuration/' },
  { text: '更新日志', link: '/zh/changelog' },
  { text: '贡献指南', link: '/zh/contributing' },
]

export default defineConfig({
  base: '/intellij-generateAllSetMethod/',

  head: [
    ['link', { rel: 'icon', href: '/intellij-generateAllSetMethod/logo.svg' }],
  ],

  locales: {
    '/': {
      lang: 'en-US',
      label: 'English',
      title: 'GenerateAllSetter',
      description: 'IntelliJ IDEA Plugin — Generate all setter/getter calls, builder chains, converters, and more',
    },
    '/zh/': {
      lang: 'zh-CN',
      label: '简体中文',
      title: 'GenerateAllSetter',
      description: 'IntelliJ IDEA 插件 — 一键生成所有 setter/getter 调用、Builder 链式调用、对象转换方法等',
    },
  },

  themeConfig: {
    logo: '/intellij-generateAllSetMethod/logo.svg',

    socialLinks: [
      { icon: 'github', link: 'https://github.com/gejun123456/intellij-generateAllSetMethod' },
    ],

    footer: {
      message: 'Released under the Apache 2.0 License.',
      copyright: 'Copyright © 2024-present bruceGe',
    },

    locales: {
      '/': {
        nav: [
          { text: 'Home', link: '/' },
          { text: 'Guide', link: '/guide/' },
          { text: 'Configuration', link: '/configuration/' },
          { text: 'Changelog', link: '/changelog' },
        ],
        sidebar: sidebarEn,
        editLink: {
          pattern: 'https://github.com/gejun123456/intellij-generateAllSetMethod/edit/main/docs/:path',
          text: 'Edit this page on GitHub',
        },
      },
      '/zh/': {
        nav: [
          { text: '首页', link: '/zh/' },
          { text: '指南', link: '/zh/guide/' },
          { text: '配置说明', link: '/zh/configuration/' },
          { text: '更新日志', link: '/zh/changelog' },
        ],
        sidebar: sidebarZh,
        editLink: {
          pattern: 'https://github.com/gejun123456/intellij-generateAllSetMethod/edit/main/docs/zh/:path',
          text: '在 GitHub 上编辑此页',
        },
      },
    },
  },
})