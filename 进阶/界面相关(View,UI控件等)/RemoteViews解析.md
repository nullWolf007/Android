# RemoteViews解析

### 参考链接

* [**Android开发艺术探索**](https://book.douban.com/subject/26599538/)

## 一、RemoteViews的应用

### 1.1 前言

* RemoteViews主要用在通知栏和桌面小部件的开发过程中。通知栏主要通过NotificationManager的notify方法来实现，它除了默认效果外，还可以另外定义布局；桌面小部件则是通过AppWidgetProvider来实现，AppWidgetProvider本质上是一个广播。通知栏和桌面小部件的开发过程中都会用到RemoteViews，他们在更新界面时无法像在Activity里面那样去直接更新View，因为这两者都运行在system_server进程

### 1.2 RemoteViews在通知栏的应用

* 注意Android8.0前后通知栏的区别，做好适配。8.0之后需要CHANNEL_ID并且需要createNotificationChannel()

* 使用RemoteViews实现自定义通知栏

* 查看自定义通知栏请点击[通知栏的demo核心代码](https://github.com/nullWolf007/ToolProject/tree/master/通知栏的demo核心代码) 

### 1.3 RemoteViews在桌面小部件的应用

#### 1.3.1 主要步骤

* 定义小部件界面
* 定义小部件配置信息
* 定义小部件的实现类
* 在AndroidManifest.xml中声明小部件

#### 1.3.2 AppWidgetProvider的常用方法

* onEnable：当该窗口小部件第一次添加到桌面时调用该方法，可添加多次但只在第一次调用
* onUpdate：小部件被添加时或者每次小部件更新时都会调用一次该方法，小部件的更新时机由updatePeriodMills来指定，每个周期小部件都会自动更新一次
* onDeleted：每删除一次桌面小部件就调用一次
* onDisabled：当最后一个该类型的桌面小部件被删除时调用该方法，注意是最后一个
* onReceive：这是广播的内置方法，用于分发具体的事件给其他方法

#### 1.3.3 实例代码

* 查看桌面小部件实例代码请点击[桌面小部件demo的核心代码](https://github.com/nullWolf007/ToolProject/tree/master/桌面小部件demo的核心代码) 
* updatePeriodMills最低更新时间周期为30分钟

## 二、RemoteViews的内部机制

### 2.1 RemoteViews支持的View类型

**Layout**

* FrameLayout、LinearLayout、RelativeLayout、GridLayout

**View**

* AnalogClock、Button、Chronometer、Image Button、ImageView、ProgressBar、TextView、ViewFlipper、ListView、GridView、StackView、AdapterViewFlipper、ViewStub

**使用其他的View类型，会抛出异常**

### 2.2 RemoteViews常用的方法

|                            方法名                            |                      作用                       |
| :----------------------------------------------------------: | :---------------------------------------------: |
|        setTextViewText(int viewId,CharSequence text)         |               设置TextView的文本                |
|     setTextViewTextSize(int viewId,int units,float size)     |             设置TextView的字体大小              |
|              setTextColor(int viewId,int color)              |             设置TextView的字体颜色              |
|          setImageViewResource(int viewId,int srcId)          |             设置ImageView的图片资源             |
|         setImageViewBitmap(int viewId,Bitmap bitmap)         |               设置ImageView的图片               |
|        setInt(int viewId,String methodName,int value)        |      反射调用View对象的参数类型为int的方法      |
|       setLong(int viewId,String methodName,long value)       |     反射调用View对象的参数类型为long的方法      |
|    setBoolean(int viewId,String methodName,boolean value)    |    反射调用View对象的参数类型为boolean的方法    |
| setOnClickPendingIntent(int viewId,PendingIntent pendingIntent) | 为View添加点击事件，事件类型只能为PendingIntent |

 ### 2.3 整体通信流程

* 通知栏和桌面小部件分别由NotificationManager和AppWidgetManager管理，而NotificationManager和AppWidgetManager通过Binder分别和SystemServer进程中的NotificationManagerService以及AppWidgetService进行通信。所以通知栏和桌面小部件的布局文件都是在SystemServer进程中进行加载的，所以实际上是跨进程通信
* 首先RemoteViews会通过Binder传递到Binder传递到SystemServer进程，这是因为RemoteViews实现了Parcelable接口。系统会根据remoteViews中的包名等信息取得到应用的资源，然后通过LayoutInflater去加载RemoteViews的布局文件。系统会等RemoteViews被加载以后进行界面更新的任务，也就是我们通过set方法来设置的布局。当需要更新RemoteViews的时候，需要调用一系列set方法，并通过NotificationManager和AppWidgetManager来提交更新任务，具体的更新操作也是在SystemServer中进行
* 理论上系统可以直接通过Binder去支持所有的View和View操作。但是这样的话代价太大，因为View的方法太多了，另外就是大量的IPC操作会影响效率。为了解决这个问题，系统提供了一个Action的概念。Action代表一个View操作，Action实现了Parcelable接口。系统先把View操作封装到Action对象中，传递Action对象而不是View操作，远程中取出Action对象，然后得到View的操作去执行。远程对象通过RemoteViews的apply方法来进行View的更新操作。

![**RemoteViews内部机制**](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/%E7%95%8C%E9%9D%A2%E7%9B%B8%E5%85%B3/RemoteViews%E5%86%85%E9%83%A8%E6%9C%BA%E5%88%B6.png)

### 2.4 源码解析

