[TOC]

# Intent和PendingIntent

### 参考链接

* [**Android开发艺术探索**](https://book.douban.com/subject/26599538/)

## 二、PendingIntent概述

### 2.1 前言

#### 2.1.1 含义

* PendingIntent表示一种处于pending状态的意图，而pending状态标识一种待定、等待、即将发生的意思，也就是说接下来有一个Intent(即意图)将在某个待定的时刻发生。

#### 2.1.2 PendingIntent和Intent的区别

* PendingIntent是在将来某个不确定的时刻发生，而Intent是立刻发生

#### 2.1.3 使用场景

* 结合通知栏Notification使用
* 给RemoteViews添加点击事件，因为RemoteViews运行在远程进程中

### 2.2 主要方法

#### 2.2.1 getActivity()

* getActivity(Context context, int requestCode, Intent intent, int flags) 
* 获得一个PendingIntent，该待定意图发生时，效果相当于Context.startActivity(Intent)

#### 2.2.2 getService()

* getService(Context context, int requestCode, Intent intent, int flags)
* 获得一个PendingIntent，该待定意图发生时，效果相当于Context.startService(Intent)

#### 2.2.3 getBroadcast()

* getBroadcast(Context context, int requestCode, Intent intent, int flags)
* 获得一个PendingIntent，该特定意图发生时，效果相当于Context.sendBroadcast(Intent)

#### 2.2.4 send和cancel

* PendingIntent通过send和cancel方法来发送和取消特定的Intent

### 2.3 requestCode和flags

#### 2.3.1 相同的PendingIntent

* 如果两个PendingIntent的内部Intent相同并且requestCode相同就是相同的。内部Intent相同的意思时两个Intent的ComponentName和intent-filter都相同

#### 2.3.2 requestCode

* 标识PendingIntent发送方的请求码，多数情况设为0即可

#### 2.3.3 flags

**FLAG_ONE_SHOT**

* 当前描述的PendingIntent只能被使用一次， 然后它就会被自动cancel， 如果后续还有相同的Pendinglntent， 那么它们的send方法就会调用失败。对于通知栏消息来说， 如果采用此标记位，那么同类的通知只能使用一次，后续的通知单击后将无法打开。 

**FLAG_NO_CREATE**

* 当前描述的PendingIntent不会主动创建，如果当前PendingIntent之前不存在，那么getActivity、getService和getBroadcast方法会直接返回null， 即获取PendingIntent失败。这个标记位很少见，它无法单独使用，因此在日常开发中它并没有太多的使用意义，这里就不再过多介绍了。 

**FLAG_CANCEL_CURRENT**

* 当前描述的PendingIntent如果已经存在，那么它们都会被cancel， 然后系统会创建一个新的PendingIntent。对于通知栏消息来说， 那些被cancel的消息单击后将无法打开。

**FLAG_UPDATE_CURRENT**

* 当前描述的PendingIntent如果已经存在，那么它们都会被更新， 即它们的Intent中的Extras会被替换成最新的。 

#### 2.3.4 flags和通知栏结合说明

* 如有manager.notify(1,notification)
* 如果notify的第一个参数id是常量，那么多次调用notify只能弹出一个通知，后续的通知会把前面的完全替代掉
* 如果每次id不同，那么当PendingIntent不相同时，在这种情况下不管采用何种标志位，这些通知之间都不会相互干扰；如果PendingIntent相同时，这个时候分情况讨论：如果采用了FLAG_ONE_SHOT标志位，那么后续通知中的PendingIntent回合第一条通知保持完全一致，包括其中的Extras，单击任何一条通知后，剩下的通知均无法打开，当所有的通知被清除后，会再次重复该过程；如果采用FLAG_CANCEL_CURRENT标记位，那么只有最新的通知可以打开，之前弹出的所有通知均无法打开；如果采用FLAG_UPDATE_CURRENT标记位，那么之前弹出的通知中的PendingIntent会被更新，最终他们和最新的一条通知保持完全一致，包括其中的Extras，并且这些通知都是可以打开的。

