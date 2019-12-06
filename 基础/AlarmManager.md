# AlarmManager的详解

## 前言
* AlarmManager就是"提醒"，是android中常用的一种系统级别的提示服务。在特定的时刻为我们广播一个指定的Intent。使用特定的PendingIntent，PendingIntent可以理解为Intent的封装包，也就是在Intent上加个指定的动作，在使用Intent的时候，我们需要执行startActivity,startService或者sendBroadcast才能使Intent有用，PendingIntent的话就是将这个动作包含在内
* 新建AlarmManager对象
```java
//AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务  
AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);   
```

## 基本使用步骤
1. 定义一个PendingIntent对象
```java
PendingIntent pi = PendingIntent.getBroadcast(this,0,intent,0);
```
2. AlarmManager的常用方法
* 设置一次性闹钟
```java
set(int type，long startTime，PendingIntent pi)
//该方法用于设置一次性闹钟，type表示闹钟类型，startTime表示闹钟执行时间，pi表示闹钟响应动作
```
* 周期性执行的定时服务
```java
setRepeating(int type，long startTime，long intervalTime，PendingIntent pi)
//该方法用于设置重复闹钟，type表示闹钟类型，startTime表示闹钟首次执行时间，intervalTime表示闹钟两次执行的间隔时间，
//pi表示闹钟响应动作
```
* 周期性执行的定时服务
```java
setInexactRepeating（int type，long startTime，long intervalTime，PendingIntent pi
//设置重复闹钟，不过两个闹钟执行的间隔时间不是固定的，它相对而言更省电，因为系统可能会将几个差不多的闹钟合并为一个执行，
//减少设备唤醒次数
```
* 取消定时服务
```java
void cancel(PendingIntent pi)
//取消和PendingIntent配置的闹钟服务
```

## 参数详悉
### type闹钟的类型
* AlarmManager.ELAPSED_REALTIME：表示闹钟在手机睡眠状态下不可用，该状态下闹钟使用的是相对时间（相对于系统启动开始），状态值为3
* AlarmManager.ELAPSED_REALTIME_WAKEUP：表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间，状态值为2
* AlarmManager.ELAPSED_REALTIME_WAKEUP：表示闹钟在睡眠状态下不可用，该状态下闹钟使用绝对时间，即当前系统时间，状态值为1
* AlarmManager.RTC_WAKEUP：表示闹钟在睡眠状态下会唤醒系统并执行提示功能，使用绝对时间，状态值为0
* AlarmManager.POWER_OFF_WAKEUP：表示闹钟在手机关机状态下也能正常进行提示功能，使用绝对时间，状态值为4
### PendingIntent pi参数
* 通过启动服务来实现闹钟提示的话：Pending.getService(Context c,int i,Intent intent,int j)方法
* 通过广播来实现闹钟提示的话：PendingIntent.getBroadcast(Context c,int i,Intent intent,int j)方法
* 采用Activity的方式实现闹钟提示的话：PendingIntent.getActivity(Context c,int i,Intent intent,int j)方法

### 参考文章
* [Android中的AlarmManager的使用](http://blog.csdn.net/wangxingwu_314/article/details/8060312)


