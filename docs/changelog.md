# Changelog

## 2.8.5

- **NEW**: Add postfix templates: `.allset`, `.allsetn`, `.allget`, `.allaccessor`, `.allbuilder` — type after a variable and press Tab/Enter to expand into all setter/getter/builder calls
- **FIX**: Postfix template expansion no longer leaves the original variable name on the line above the generated code
- **FIX**: Fix AssertJ test engine detection defect
- **NEW**: Add generate setter getter converter from variable support
- **IMPROVE**: Optimize the indentation method
- **IMPROVE**: Add NPE judge in method convert
- **IMPROVE**: Bump minimum supported IDE to 2021.3 (since-build 213)

## 2.8.4

- **NEW**: Test engines support: AssertJ, JUnit 4/5, TestNG, JDK assert with auto-detection
- **NEW**: Option to disable Guava code generation + splitter in preferences dialog
- Thanks to [hza](https://github.com/hza) for the contribution

## 2.8.3

- **FIX**: Must not start write action from within read action in the other thread — deadlock fix

## 2.8.2

- **FIX**: Fix document null exception

## 2.8.1

- **NEW**: Support Groovy
- **IMPROVE**: Better builder support
- **NEW**: Support accessor chain

## 2.8

- **FIX**: Fix exception in 2021.2

## 2.7

- **FIX**: Fix intention action text
- **NEW**: Enum type support

## 2.6

- **NEW**: Support Lombok builder using `with` (Thanks to [axesipov](https://github.com/axesipov))

## 2.5

- **NEW**: Compatible for 2020 version

## 2.4

- **FIX**: Fix Generate from method getter setter converter not working

## 2.3

- **FIX**: Fix null pointer

## 2.2

- **NEW**: Add generate all setter no default value
- **NEW**: Add generate assert all getter

## 2.1

- **NEW**: Add base support for Kotlin
- **IMPROVE**: Rename "generate all setter name" to "Generate all setter"
- **IMPROVE**: Add space in new operator

## 1.9

- **IMPROVE**: Map import and add default value for some packages

## 1.8

- **IMPROVE**: Support byte type

## 1.7

- **NEW**: Support two list convert

## 1.6

- **FIX**: Fix exception when cursor is on constructor method

## 1.5

- **NEW**: Generate default value for return type List, Set, Map

## 1.4

- **NEW**: Use on method: if method parameters contain same value, will pass to it

## 1.3

- **FIX**: Fix docs

## 1.2

- **FIX**: Fix suffix for long and double

## 1.1

- **NEW**: Auto add import to class