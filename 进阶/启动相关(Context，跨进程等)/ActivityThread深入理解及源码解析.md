# ActivityThread深入理解及源码解析

### 一、ActivityThread关联图

![ActivityThread关联图](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/ActivityThread%E5%85%B3%E8%81%94%E5%9B%BE.png)

* 从图中我们可以看到四大组件中的mActivities，mServices,mProviderMap都保存在ArrayMap中。但是唯独BroadcastReceiver没有进行保存，这是因为BroadcastReceiver对象的生命周期很短暂，属于调用它，再创建运行，所以不需要保存。

* mInitialApplication就是Application对象，一个进程只能有一个Application，但是一个Application可以占用多个进程
* 变量mResourcesManage管理着应用中的资源

### 二、ActivityThread的main方法

#### 2.1源码

```java
    public static void main(String[] args) {
        Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "ActivityThreadMain");
        SamplingProfilerIntegration.start();

        // CloseGuard defaults to true and can be quite spammy.  We
        // disable it here, but selectively enable it later (via
        // StrictMode) on debug builds, but using DropBox, not logs.
        CloseGuard.setEnabled(false);
		//初始化应用中需要使用的系统路径
        Environment.initForCurrentUser();

        // Set the reporter for event logging in libcore
        EventLogger.setReporter(new EventLoggingReporter());

        //为应用 设置当前用户的CA证书保存的位置
        final File configDir = Environment.getUserConfigDirectory(UserHandle.myUserId());
        TrustedCertificateStore.setDefaultUserDirectory(configDir);
		//设置进程名称
        Process.setArgV0("<pre-initialized>");

        Looper.prepareMainLooper();
		//创建ActivityThread对象
        ActivityThread thread = new ActivityThread();
        thread.attach(false);

        if (sMainThreadHandler == null) {
            sMainThreadHandler = thread.getHandler();
        }

        if (false) {
            Looper.myLooper().setMessageLogging(new
                    LogPrinter(Log.DEBUG, "ActivityThread"));
        }

        // End of event ActivityThreadMain.
        Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
        Looper.loop();

        throw new RuntimeException("Main thread loop unexpectedly exited");
    }
```

* 上述代码就是main方法的代码，我们从上面取出几行代码进行单独讲解

```java
        Looper.prepareMainLooper();
		//创建ActivityThread对象
        ActivityThread thread = new ActivityThread();
        thread.attach(false);

        if (sMainThreadHandler == null) {
            sMainThreadHandler = thread.getHandler();
        }

        if (false) {
            Looper.myLooper().setMessageLogging(new
                    LogPrinter(Log.DEBUG, "ActivityThread"));
        }

        // End of event ActivityThreadMain.
        Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
        Looper.loop();

        throw new RuntimeException("Main thread loop unexpectedly exited");
    }
```

* 首先通过了Looper.prepareMainLooper();为主线程创建了Looper，然后thread.getHandler()保存了主线程的Handler，最后Looper.loop()进入消息循环。请点击查看[Handler和Message和MessageQueue和Looper](https://github.com/nullWolf007/Android/blob/master/%E8%BF%9B%E9%98%B6/Handler%E5%92%8CMessage%E5%92%8CMessageQueue%E5%92%8CLooper.md)
* 最后看下thread.attach(false)

```java
    private void attach(boolean system) {
        sCurrentActivityThread = this;
        mSystemThread = system;
        if (!system) {
            ViewRootImpl.addFirstDrawHandler(new Runnable() {
                @Override
                public void run() {
                    ensureJitEnabled();
                }
            });
            android.ddm.DdmHandleAppName.setAppName("<pre-initialized>",
                                                    UserHandle.myUserId());
            //将mAppThread放到RuntimeInit类中的静态变量
            RuntimeInit.setApplicationObject(mAppThread.asBinder());
            final IActivityManager mgr = ActivityManager.getService();
            try {
                //将mAppThread传入ActivityThreadManager中
                mgr.attachApplication(mAppThread);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
            // Watch for getting close to heap limit.
            BinderInternal.addGcWatcher(new Runnable() {
                @Override public void run() {
                    if (!mSomeActivitiesChanged) {
                        return;
                    }
                    Runtime runtime = Runtime.getRuntime();
                    long dalvikMax = runtime.maxMemory();
                    long dalvikUsed = runtime.totalMemory() - runtime.freeMemory();
                    if (dalvikUsed > ((3*dalvikMax)/4)) {
                        if (DEBUG_MEMORY_TRIM) Slog.d(TAG, "Dalvik max=" + (dalvikMax/1024)
                                + " total=" + (runtime.totalMemory()/1024)
                                + " used=" + (dalvikUsed/1024));
                        mSomeActivitiesChanged = false;
                        try {
                            mgr.releaseSomeActivities(mAppThread);
                        } catch (RemoteException e) {
                            throw e.rethrowFromSystemServer();
                        }
                    }
                }
            });
        } else {
            // Don't set application object here -- if the system crashes,
            // we can't display an alert, we just want to die die die.
            android.ddm.DdmHandleAppName.setAppName("system_process",
                    UserHandle.myUserId());
            try {
                mInstrumentation = new Instrumentation();
                ContextImpl context = ContextImpl.createAppContext(
                        this, getSystemContext().mPackageInfo);
                mInitialApplication = context.mPackageInfo.makeApplication(true, null);
                mInitialApplication.onCreate();
            } catch (Exception e) {
                throw new RuntimeException(
                        "Unable to instantiate Application():" + e.toString(), e);
            }
        }

        // add dropbox logging to libcore
        DropBox.setReporter(new DropBoxReporter());

        ViewRootImpl.ConfigChangedCallback configChangedCallback
                = (Configuration globalConfig) -> {
            synchronized (mResourcesManager) {
                // We need to apply this change to the resources immediately, because upon returning
                // the view hierarchy will be informed about it.
                if (mResourcesManager.applyConfigurationToResourcesLocked(globalConfig,
                        null /* compat */)) {
                    updateLocaleListFromAppContext(mInitialApplication.getApplicationContext(),
                            mResourcesManager.getConfiguration().getLocales());

                    // This actually changed the resources! Tell everyone about it.
                    if (mPendingConfiguration == null
                            || mPendingConfiguration.isOtherSeqNewer(globalConfig)) {
                        mPendingConfiguration = globalConfig;
                        sendMessage(H.CONFIGURATION_CHANGED, globalConfig);
                    }
                }
            }
        };
        ViewRootImpl.addConfigCallback(configChangedCallback);
    }
```

* 对于system值为false的情况，上述代码主要完成两件事情。

  * 1.调用RuntimeInit.setApplicationObject(mAppThread.asBinder());方法，把对象mAppThread的Binder放到了RuntimeInit类中的静态变量mApplicationObject中。mAppThread就是ApplicationThread对象
  
    ```java
    public static final void setApplicationObject(IBinder app) {
    	mApplicationObject = app;
    }
    ```
  
  * 2.调用**ActivityManagerService**的attachApplication()方法，将mAppThread 作为参数传入ActivityManagerService，这样ActivityManagerService就可以调用**ApplicaitonThread**的接口了，这样就把AMS和ActivityThread联系起来了

