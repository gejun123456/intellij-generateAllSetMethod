# intellij-generateAllSetMethod


[![GitHub release][release-img]][latest-release] [![Jetbrains Plugins][plugin-img]][plugin] 
[![Version](http://phpstorm.espend.de/badge/9360/version)][plugin]  
[![Downloads](http://phpstorm.espend.de/badge/9360/downloads)][plugin]
[![Downloads last month](http://phpstorm.espend.de/badge/9360/last-month)][plugin]

- generate setter default values when cursor on local variable
![setter](https://raw.githubusercontent.com/gejun123456/intellij-generateAllSetMethod/master/screenshot/generate_setter_default_value.gif)

- generate converter when cursor in method
![methodConveter](https://raw.githubusercontent.com/gejun123456/intellij-generateAllSetMethod/master/screenshot/generate_the_conveter.gif)

- generate List Set Map return value
![ListImplement](https://raw.githubusercontent.com/gejun123456/intellij-generateAllSetMethod/master/screenshot/generate_list_default_value.gif)
Installation
----

support following product build version > 141

- Android Studio
- IntelliJ IDEA
- IntelliJ IDEA Community Edition

**using IDE plugin system**
- <kbd>Preferences(Settings)</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>find"GenerateAllSetter"</kbd> > <kbd>Install Plugin</kbd>

**Manual:**
- download[`lastest plugin zip`][latest-release] -> <kbd>Preferences(Settings)</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>

restart **IDE**.

## contribute 
- import the project to Intellij
- add a gradle task runIde
- change the code and run the task to see the change

## my other plugins
- MybatisCodeHelperPro  https://github.com/gejun123456/MyBatisCodeHelper-Pro

- StackOverFlow https://github.com/gejun123456/IntellijGoToStackOverFlow

[release-img]: https://img.shields.io/github/release/gejun123456/intellij-generateAllSetMethod.svg
[latest-release]: https://github.com/gejun123456/intellij-generateAllSetMethod/releases/latest
[plugin-img]: https://img.shields.io/badge/plugin-9360-orange.svg
[plugin]: https://plugins.jetbrains.com/plugin/9360
