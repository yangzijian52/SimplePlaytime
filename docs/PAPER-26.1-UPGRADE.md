# SimplePlaytime 升级到 Paper 26.1 记录

## 本次处理内容

- 将项目从 `Gradle` 重构为 `Maven`
- 将 `paper-api` 升级到官方当前最新可用版本 `26.1.1.build.29-alpha`
- 将 Java 编译目标升级到 `25`
- 将 `plugin.yml` 改为自动读取 Maven 版本号
- 为 `Gson` 增加显式依赖并在打包时合并进插件
- 更新中文 `README`

## 当前状态

- 已完成本地项目结构整理
- 已完成升级配置修改
- 还未进行服内实测

## 运行要求

- Paper `26.1.x`
- Java `25+`
- PlaceholderAPI
