# SimplePlaytime ⏳

一个专为 **Paper 1.21+** 服务器设计的轻量级、高性能玩家在线时长统计插件。
原生支持 **PlaceholderAPI**，内置排行榜变量，支持智能时间格式化，完美适配 **DecentHolograms** 全息图展示。

## ✨ 特性 (Features)

*   **⚡ 极致轻量**：使用 JSON 本地存储，无需配置 MySQL，开箱即用。
*   **🤖 智能过滤**：自动忽略名字以 `bot_` 开头（不区分大小写）的假人/机器人，不纳入统计。
*   **📅 智能格式化**：
    *   支持 **年、月、日、时、分** 完整单位。
    *   **极简显示**：自动隐藏数值为 0 的单位（例如：只玩了5分钟显示 `5分`，而不是 `0年0月0天0时5分`）。
*   **🏆 内置排行榜**：提供全服前 10 名的名称和时长变量，支持异步刷新，不卡主线程。
*   **💾 数据安全**：每 5 分钟自动异步保存数据，玩家退出时立即保存。

## 🛠️ 环境要求 (Requirements)

*   **服务端核心**：Paper 1.21+ (或兼容核心，如 Leaves)
*   **Java 版本**：Java 21+
*   **前置插件**：PlaceholderAPI

## 📦 安装 (Installation)

1.  下载 `SimplePlaytime-1.0.0-all.jar`。
2.  将文件放入服务器的 `plugins` 文件夹。
3.  重启服务器。
4.  无需任何配置，插件会自动开始统计。

## 📊 变量大全 (Placeholders)

插件内置 PAPI 变量，安装后直接使用，无需下载扩展：

| 变量名 | 描述 | 示例输出 |
| :--- | :--- | :--- |
| `%spt_time%` | 玩家当前的累计在线时长 | `1天2时30分` |
| `%spt_top_name_1%` | 排行榜第 1 名的**名字** | `Steve` |
| `%spt_top_time_1%` | 排行榜第 1 名的**时长** | `5年3天` |
| `%spt_top_name_X%` | 排行榜第 X 名的**名字** (1-10) | `Alex` |
| `%spt_top_time_X%` | 排行榜第 X 名的**时长** (1-10) | `1月20分` |

> **注意**：排行榜数据每 1 分钟自动刷新一次。

## 🖼️ 全息图排行榜示例 (DecentHolograms)

如果你使用 **DecentHolograms**，可以使用以下命令快速创建“肝帝排行榜”：

```bash
# 1. 创建全息图
/dh h create playtime_top

# 2. 添加标题
/dh l add playtime_top &e&l全服肝帝排行榜

# 3. 添加第一名
/dh l add playtime_top 1 &6&lNo.1 &e%spt_top_name_1% &7- &f%spt_top_time_1%

# 4. 添加第二名
/dh l add playtime_top 1 &b&lNo.2 &e%spt_top_name_2% &7- &f%spt_top_time_2%

# 5. 添加第三名
/dh l add playtime_top 1 &c&lNo.3 &e%spt_top_name_3% &7- &f%spt_top_time_3%

#以此类推

```

## 🏗️ 从源码构建 (Build from source)
如果你想自己修改代码，请按照以下步骤构建：

克隆本仓库。

运行构建命令：

Windows: 双击 gradlew.bat 或运行 gradlew.bat shadowJar

Linux/Mac: ./gradlew shadowJar

构建成功后，插件位于 build/libs/SimplePlaytime-1.0.0-all.jar。

## 📝 许可证 (License)
本项目采用 MIT License 开源。
