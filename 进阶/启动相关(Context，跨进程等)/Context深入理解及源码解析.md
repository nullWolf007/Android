[TOC]
- <!-- TOC -->
- [ Context深入理解及源码解析](#Context深入理解及源码解析)
  - [ 参考链接](#参考链接)
  - [ 一、官方解释](#一官方解释)
  - [ 二、继承关系](#二继承关系)
  - [ 三、源码解析](#三源码解析)
    - [ 3.1Context源码解析](#31Context源码解析)
    - [ 3.2 ContextWrapper源码解析](#32-ContextWrapper源码解析)
    - [ 3.3 ContextImpl源码解析](#33-ContextImpl源码解析)
  - [ 四、关联时机](#四关联时机)
    - [ 4.1ActivityThread](#41ActivityThread)
  - [ 五、Context资源详解](#五Context资源详解)
    - [ 5.1 疑问](#51-疑问)
    - [ 5.2 Context对象的区别和用法](#52-Context对象的区别和用法)
    - [ 5.3 源码分析](#53-源码分析)
      - [ 5.3.1 init方法](#531-init方法)
      - [ 5.3.2 getResouces方法](#532-getResouces方法)
      - [ 5.3.3 getTopLevelResouces方法](#533-getTopLevelResouces方法)
      - [ 5.3.4 结论](#534-结论)
  <!-- /TOC -->
[TOC]

# Context深入理解及源码解析

### 参考链接

* [**你足够了解Context吗？**](https://www.jianshu.com/p/46c35c5079b4)

## 一、官方解释

> Interface to global information about an application environment. This is an abstract class whose implementation is provided by the Android system. It allows access to application-specific resources and classes, as well as up-calls for application-level operations such as launching activities, broadcasting and receiving intents, etc.

* 是应用环境的全局信息，字面上是上下文的意思
* 是提供给安卓系统的一个抽象类
* 允许通过Context来获取资源，类等。同时也可以去启动Activity，广播等

## 二、继承关系

![Context](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/Context%E7%BB%A7%E6%89%BF%E5%85%B3%E7%B3%BB.png)

* 通过上图我们可以了解到，Context的直接子类为ContextWrapper(实现类)和ContextImpl(包装类)。ContextWrapper的子类有Application、Service、ContextThemeWrapper。而Activity是ContextThemeWrapper的子类。为什么Activity不再同一级呢？因为对于Activity有Theme的区别，Activity可以指定主题，而Application、Service没有主题。

## 三、源码解析

### 3.1Context源码解析

```java
public abstract class Context {

    // 获取应用程序包的AssetManager实例
    public abstract AssetManager getAssets();
 
    // 获取应用程序包的Resources实例
    public abstract Resources getResources();

    // 获取PackageManager实例，以查看全局package信息    
    public abstract PackageManager getPackageManager();

    // 获取应用程序包的ContentResolver实例
    public abstract ContentResolver getContentResolver();
    
    // 它返回当前进程的主线程的Looper，此线程分发调用给应用组件(activities, services等)
    public abstract Looper getMainLooper();

    // 返回当前进程的单实例全局Application对象的Context     
    public abstract Context getApplicationContext();

    // 从string表中获取本地化的、格式化的字符序列
    public final CharSequence getText(int resId) {
        return getResources().getText(resId);
    }

    // 从string表中获取本地化的字符串
    public final String getString(int resId) {
        return getResources().getString(resId);
    }

    public final String getString(int resId, Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    }

    // 返回一个可用于获取包中类信息的class loader
    public abstract ClassLoader getClassLoader();

    // 返回应用程序包名
    public abstract String getPackageName();

    // 返回应用程序信息
    public abstract ApplicationInfo getApplicationInfo();

    // 根据文件名获取SharedPreferences
    public abstract SharedPreferences getSharedPreferences(String name,
            int mode);

    // 其根目录为: Environment.getExternalStorageDirectory()
    public abstract File getExternalFilesDir(String type);

    // 返回应用程序obb文件路径
    public abstract File getObbDir();

    // 启动一个新的activity 
    public abstract void startActivity(Intent intent);

    // 启动一个新的activity 
    public void startActivityAsUser(Intent intent, UserHandle user) {
        throw new RuntimeException("Not implemented. Must override in a subclass.");
    }

    // 启动一个新的activity 
    // intent: 将被启动的activity的描述信息
    // options: 描述activity将如何被启动
    public abstract void startActivity(Intent intent, Bundle options);

    // 启动多个新的activity
    public abstract void startActivities(Intent[] intents);

    // 启动多个新的activity
    public abstract void startActivities(Intent[] intents, Bundle options);

    // 广播一个intent给所有感兴趣的接收者，异步机制 
    public abstract void sendBroadcast(Intent intent);

    // 广播一个intent给所有感兴趣的接收者，异步机制 
    public abstract void sendBroadcast(Intent intent,String receiverPermission);

    public abstract void sendOrderedBroadcast(Intent intent,String receiverPermission);
 
    public abstract void sendOrderedBroadcast(Intent intent,
            String receiverPermission, BroadcastReceiver resultReceiver,
            Handler scheduler, int initialCode, String initialData,
            Bundle initialExtras);

    public abstract void sendBroadcastAsUser(Intent intent, UserHandle user);

    public abstract void sendBroadcastAsUser(Intent intent, UserHandle user,
            String receiverPermission);
  
    // 注册一个BroadcastReceiver，且它将在主activity线程中运行
    public abstract Intent registerReceiver(BroadcastReceiver receiver,
                                            IntentFilter filter);

    public abstract Intent registerReceiver(BroadcastReceiver receiver,
            IntentFilter filter, String broadcastPermission, Handler scheduler);

    public abstract void unregisterReceiver(BroadcastReceiver receiver);
 
    // 请求启动一个application service
    public abstract ComponentName startService(Intent service);

    // 请求停止一个application service
    public abstract boolean stopService(Intent service);
 
    // 连接一个应用服务，它定义了application和service间的依赖关系
    public abstract boolean bindService(Intent service, ServiceConnection conn,
            int flags);

    // 断开一个应用服务，当服务重新开始时，将不再接收到调用， 
    // 且服务允许随时停止
    public abstract void unbindService(ServiceConnection conn);
 
    public abstract Object getSystemService(String name);
 
    public abstract int checkPermission(String permission, int pid, int uid);
 
    // 返回一个新的与application name对应的Context对象
    public abstract Context createPackageContext(String packageName,
            int flags) throws PackageManager.NameNotFoundException;
    
    // 返回基于当前Context对象的新对象，其资源与display相匹配
    public abstract Context createDisplayContext(Display display);
 }  
```

* 从上面的代码可以知道Context提供了各种各样的方法，但是Context是一个抽象类，这些抽象的方法还是需要去实现的，那是谁实现了这些方法呢？从子类名称来看，几乎可以确认为ContextImpl，那究竟是不是，我们可以看下源码，先看ContextWrapper是不是，再看ContextImpl是不是

### 3.2 ContextWrapper源码解析

```java
public class ContextWrapper extends Context {
    Context mBase;

    public ContextWrapper(Context base) {
        mBase = base;
    }
    
    protected void attachBaseContext(Context base) {
        if (mBase != null) {
            throw new IllegalStateException("Base context already set");
        }
        mBase = base;
    }
    
    //省略了
    //......
}
```

* 我们可以看到ContextWrapper没有去实现，但是他拥有一个mBase的Context，所有操作都是他完成的。其实这个Context就是ContextImpl的实例。那我们就看看ContextImpl是怎么实现的

### 3.3 ContextImpl源码解析

* 查看源码，我们就能发现，再ContextImpl中实现了各种方法，但是这个代码太多了，就不展示了，有兴趣的可以去查看源码。Context的实现类只有ContextImpl。Activity、Service、Application都没有去实现，他们都是通过和ContextImpl关联，来提供这些功能。那我们已经知道了大体流程，现在我们需要的是找到何时完成的关联

## 四、关联时机

### 4.1ActivityThread

* 我们常常会误认为Android程序的入口是Application的onCreate()方法，其实这是错误的想法。其实Android程序的入口是ActivityThread的main的方法
* 查看ActivityThread详细内容请点击#[ActivityThread深入理解及源码解析

## 五、Context资源详解

### 5.1 疑问

* 为什么都是Context，但是不同的会有不同的功能，比如Activity就有showDialog的功能，而其他的如Service等就不能showDialog。难道说这些拥有不同的Context？

### 5.2 Context对象的区别和用法

![Context对象的区别和用法](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/Context%E8%B5%84%E6%BA%90%E5%8C%BA%E5%88%AB.png)

* 上面的表格说明了，不同的Context拥有自己不同的的功能。可以看到Activity拥有最多的功能。所以人会理所当然的认为这些使用的不是同一块资源。但是实际上他们使用的是同一块资源。我们通过源码去发现其中的原因。

### 5.3 源码分析

#### 5.3.1 init方法

```java
final void init(LoadedApk packageInfo,  
            IBinder activityToken, ActivityThread mainThread,  
            Resources container) {  
    mPackageInfo = packageInfo;  
    mResources = mPackageInfo.getResources(mainThread);  
  
    if (mResources != null && container != null  
            && container.getCompatibilityInfo().applicationScale !=  
                    mResources.getCompatibilityInfo().applicationScale) {  
        if (DEBUG) {  
            Log.d(TAG, "loaded context has different scaling. Using container's" +  
                    " compatiblity info:" + container.getDisplayMetrics());  
        }  
        mResources = mainThread.getTopLevelResources(  
                mPackageInfo.getResDir(), container.getCompatibilityInfo().copy());  
    }  
    mMainThread = mainThread;  
    mContentResolver = new ApplicationContentResolver(this, mainThread);  
  
    setActivityToken(activityToken);  
} 
```

* 通过上面的代码，我们能找到是通过getResources()方法去获取到的资源

#### 5.3.2 getResouces方法

```java
public Resources getResources(ActivityThread mainThread) {  
    if (mResources == null) {  
        mResources = mainThread.getTopLevelResources(mResDir, this);  
    }  
    return mResources;  
} 
```

* 这里又调用了getTopLevelResouces方法

#### 5.3.3 getTopLevelResouces方法

```java
Resources getTopLevelResources(String resDir, CompatibilityInfo compInfo) {  
    ResourcesKey key = new ResourcesKey(resDir, compInfo.applicationScale);  
    Resources r;  
    synchronized (mPackages) {  
        // Resources is app scale dependent.  
        if (false) {  
            Slog.w(TAG, "getTopLevelResources: " + resDir + " / "  
                    + compInfo.applicationScale);  
        }  
        WeakReference<Resources> wr = mActiveResources.get(key);  
        r = wr != null ? wr.get() : null;  
          
        if (r != null && r.getAssets().isUpToDate()) {  
            if (false) {  
                Slog.w(TAG, "Returning cached resources " + r + " " + resDir  
                        + ": appScale=" + r.getCompatibilityInfo().applicationScale);  
            }  
            return r;  
        }  
    }  
  
    AssetManager assets = new AssetManager();  
    if (assets.addAssetPath(resDir) == 0) {  
        return null;  
    }  
  
    DisplayMetrics metrics = getDisplayMetricsLocked(false);  
    r = new Resources(assets, metrics, getConfiguration(), compInfo);  
    if (false) {  
        Slog.i(TAG, "Created app resources " + resDir + " " + r + ": "  
                + r.getConfiguration() + " appScale="  
                + r.getCompatibilityInfo().applicationScale);  
    }  
      
    synchronized (mPackages) {  
        WeakReference<Resources> wr = mActiveResources.get(key);  
        Resources existing = wr != null ? wr.get() : null;  
        if (existing != null && existing.getAssets().isUpToDate()) {  
            // Someone else already created the resources while we were  
            // unlocked; go ahead and use theirs.  
            r.getAssets().close();  
            return existing;  
        }  
        // XXX need to remove entries when weak references go away  
        mActiveResources.put(key, new WeakReference<Resources>(r));  
        return r;  
    }  
}  
```

* mActiveResources对象内部保存了该应用程序所使用到的所有Resources对象，其类型为WeakReference，所以内存紧张时，可以释放Resouces占用的资源。

#### 5.3.4 结论

* 当ActivityThread类中创建Application、Service、Activity的同时，完成了与ContextImpl的关联绑定，通过ContextImpl类中init方法，获得了一个唯一的**Resources**对象，根据上述代码中资源的请求机制，再加上ResourcesManager采用单例模式，这样就保证了不同的ContextImpl访问的是同一套资源。
* **Application、Service、Activity，它们本身对Resources资源处理方法的不同，造成了这个Resoucres最后表现出来的不一样**
* 通俗的来说就是，拿到的是同一个东西，但是不同的人用法会不一样



