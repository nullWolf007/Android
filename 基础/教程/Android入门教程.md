[TOC]

# Android入门教程

### 推荐网站

* [Androdi中文官网](https://developer.android.google.cn/?hl=zh_cn)

## 一、JDK的安装和环境配置

* 参考链接，详情请点击[**Java JDK下载、安装与环境变量配置**](https://blog.csdn.net/siwuxie095/article/details/53386227)

### 1.1 下载

* [Java SE下载链接](https://www.oracle.com/technetwork/java/javase/downloads/index.html)，里面包含各个版本的Java SE

  ![](..\教程\images\Java_SE_版本页面.jpg)

* 使用[Java SE 8u241 ](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)举例，需要Oracle账号，自己注册一个就行，点击你要下载的版本进行下载就行

  ![](..\教程\images\Java_SE_8_下载页面.png)

### 1.2 安装

* 下载完成后就可以进行安装
* 如需修改安装路径，点击更改就行。如不需的话，一直下一步就行

### 1.3 环境变量配置

* 对于Windows10来说，
  * 右键电脑->属性->高级系统设置即可到达
  * 也可以进入控制面板->系统和安全->系统->高级系统设置即可到达

* 点击环境变量
  * 在**系统变量**中新建**JAVA_HOME**变量，变量值为**jdk安装的路径**（如果由JAVA_HOME的话，直接添加就行，不用创建）
  * 找到**CLASSPATH变量**，修改为“.;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar;”
  * 找到系统变量的**Path变量**，**选中编辑然后新建**添加“%JAVA_HOME%\bin”和“%JAVA_HOME%\jre\bin”
* 然后一直确定保存就行

### 1.4 测试

* win+R打开“运行”对话框，然后输入cmd，回车进入cmd控制台
* 输入“java”或者输入“javac”，然后没有报错且有正确的输出即说明安装配置成功。如果需要查看Java的版本的话输入“java -version”

## 二、Android Studio的安装

* 参考链接，详情请点击[**Android Studio安装及环境配置教程**](https://blog.csdn.net/xuw_xy/article/details/89524335)
* 参考链接，详情请点击[****Android Studio 安装****](https://www.runoob.com/android/android-studio-install.html)

### 2.1 下载

* 点击下载[Android Stuido官网下载地址](https://developer.android.google.cn/studio/)

  ![Android_Studio下载页面](..\教程\images\Android_Studio下载页面.png)

### 2.2 安装

* 如果需要更换安装存储地址的话，更改一下就行（建议存储空间足够大，因为后续这个会很大）。其他的直接next就行

## 三、Android Studio使用说明

* [ **android studio 软件使用 详细说明**](https://blog.csdn.net/qq_41204464/article/details/83301540)

## 四、Java语法学习

* 推荐书籍[Java语言程序设计-基础班](https://book.douban.com/subject/6529833/)
* 推荐学习网站[Java教程|菜鸟教程](https://www.runoob.com/java/java-tutorial.html?tdsourcetag=s_pcqq_aiomsg)

## 五、Android学习

* 推荐书籍[**第一行代码：Android（第2版）**](https://book.douban.com/subject/26915433/)

* 推荐学习网站[官方文档](https://developer.android.google.cn/guide?hl=zh_cn)

## 六、创建第一个项目

* 官方链接[创建 Android 项目](https://developer.android.google.cn/training/basics/firstapp/creating-project?hl=zh_cn)
* 对于手机来说，需要打开开发者模式并启用USB调试(大部分手机都是连续点击版本来打开开发者模式，但是对于不同手机有轻微的区别)

## 七、界面和导航

* 官方链接[界面和导航](https://developer.android.google.cn/guide/topics/ui?hl=zh_cn)
* [**Android基础--------Android常用控件介绍及使用**](https://blog.csdn.net/weixin_38423829/article/details/80566203)

## 八、Activity和Fragment的学习

* 官方链接[Activity 简介](https://developer.android.google.cn/guide/components/activities/intro-activities?hl=zh_cn)
* 官方链接[Fragment](https://developer.android.google.cn/guide/components/fragments?hl=zh_cn)
* 官方链接[Intent 和 Intent 过滤器](https://developer.android.google.cn/guide/components/intents-filters?hl=zh_cn)

## 九、ListView和RecyclerView学习

* [**说说 Android UI 中的  ListView（列表控件）**](https://www.jianshu.com/p/5df7c7d48c2c)
* 官方链接[使用 RecyclerView 创建列表](https://developer.android.google.cn/guide/topics/ui/layout/recyclerview?hl=zh_cn)
* [官方RecyclerView示例](https://github.com/googlearchive/android-RecyclerView#readme)

## 十、数据存储

* 官方链接[使用 SQLite 保存数据](https://developer.android.google.cn/training/data-storage/sqlite.html?hl=zh_cn)

* 官方链接[保存键值对数据](https://developer.android.google.cn/training/data-storage/shared-preferences?hl=zh_cn)

## 十一、创建笔记本项目

* 主要功能：添加笔记，删除笔记
* 存在的小问题：时间显示未统一，标题未限制字数
* 点击下载[笔记本项目源码](https://github.com/zsy0216/Notepad)

## 十二、ViewPager+Fragment

* 官方链接[使用 ViewPager 在 Fragment 之间滑动](https://developer.android.google.cn/training/animation/screen-slide?hl=zh_cn)

## 十三、Service

* 官方链接[服务概览](https://developer.android.google.cn/guide/components/services?hl=zh_cn)

## 十四、网络库-OkHttp

* 点击查看官方文档[OkHttp](https://github.com/square/okhttp)
* [**OkHttp详解**](https://github.com/nullWolf007/Android/blob/master/%E8%BF%9B%E9%98%B6/%E7%BD%91%E7%BB%9C%E7%9B%B8%E5%85%B3/OkHttp%E8%AF%A6%E8%A7%A3.md)



