# AlarmManager的详解

## 前言
* AlarmManager就是"提醒"，是android中常用的一种系统级别的提示服务。在特定的时刻为我们广播一个指定的Intent。使用特定的PendingIntent，PendingIntent可以理解为Intent的封装包，也就是在Intent上加个指定的动作，在使用Intent的时候，我们需要执行startActivity,startService或者sendBroadcast才能使Intent有用，PendingIntent的话就是将这个动作包含在内

## 基本使用步骤
1. 定义一个PendingIntent对象
```java
PendingIntent pi = PendingIntent.getBroadcast(this,0,intent,0);
```
2. AlarmManager的三个常用方法
```java
set(int type，long startTime，PendingIntent pi)
```
该方法用于设置一次性闹钟，第一个参数表示闹钟类型，第二个参数表示闹钟执行时间，第三个参数表示闹钟响应动作
