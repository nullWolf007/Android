# SharedPreferences轻量级存储类详解

## 前言
* 只支持Java基本数据类型，不支持自定义数据类型
* 应用内数据共享
* 主要用来保存应用的一些常用配置

## SharedPreferences数据的四种操作模式
* Context.MODE_PRIVATE：默认操作模式，代表该文件是私有数据，只能被应用本身访问，写入的内容会覆盖原文件的内容
* Context.MODE_APPEND：该模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件
* Context.MODE_WORLD_READABLE：弃用
* Context.MODE_WORLD_WRITEABLE：弃用
