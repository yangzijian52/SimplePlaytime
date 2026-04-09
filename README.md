# SimplePlaytime

一个适用于 Paper 服务端的简单在线时长统计插件，集成 PlaceholderAPI 变量，可用于展示玩家个人在线时长和排行榜。

## 当前版本

- 插件版本：`1.0.1`
- Paper API：`26.1.1.build.29-alpha`
- Java 要求：`25+`

## 主要功能

- 统计玩家累计在线时长
- 自动忽略名称以 `bot_` 开头的机器人账号
- 提供个人时长变量
- 提供前十排行榜名称和时长变量
- 自动定时保存数据

## 运行要求

- Paper `26.1.x`
- Java `25+`
- PlaceholderAPI

## PlaceholderAPI 变量

- `%spt_time%`
- `%spt_top_name_1%` 到 `%spt_top_name_10%`
- `%spt_top_time_1%` 到 `%spt_top_time_10%`

## 当前状态

- 已从 `Gradle` 重构为 `Maven`
- 已升级到官方当前最新可用 `paper-api 26.1.1.build.29-alpha`
- 已切换到 `Java 25`
- 当前已完成本地构建整理
- 当前尚未完成服内实测

## 构建

```bash
mvn clean package
```
