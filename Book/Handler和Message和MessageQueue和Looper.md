[TOC]

# Handler和Message和MessageQueue和Looper

参考文章：

* [Android 异步消息处理机制（Handler 、 Looper 、MessageQueue）源码解析](http://blog.csdn.net/amazing7/article/details/51424038)
* [Handler消息机制，讲解Handler、Message、MessageQueue、Looper之间的关系](https://blog.csdn.net/nmyangmo/article/details/82260616)

## 一、前言

* 是进程内部、线程间的一种通信机制

### 1.1Message

* handle接受和处理的消息对象

### 1.2MessageQueue

* 存储消息对象的队列

### 1.3Looper

* 每一个线程中只有一个Looper，它负责管理对应的MessageQueue，会不断的在MessageQueue中取出消息，并将消息分给对应的handle进行处理

### 1.4Handler

* 发送消息，它能把消息发送给Looper管理的MessageQueue

* 处理消息，它负责处理Looper分给它的消息

### 1.5说明

* 要想使用handler，首先要保证当前线程存在Looper对象；主线程不需要主动创建Looper对象是因为主线程已经帮你准备好了，见android.app.ActivityThread->Looper.prepareMainLooper()；如果我们想要在子线程使用handler来接受数据，需要先通过Looper.prepare()创建Looper

  ```java
  Looper.prepare();
  ...//创建Handler并传入
  Looper.loop();
  ```

### 1.6图解

![handler-looper-message-messagequeue](https://github.com/nullWolf007/images/raw/master/android/book/handler-looper-message-messagequeue.png)

## 二、Looper源码解析

### 2.1 构造方法

```java
private Looper(boolean quitAllowed) {
	mQueue = new MessageQueue(quitAllowed);
    mThread = Thread.currentThread();
}
```

* 说明：创建Looper对象的时候，同时创建了MessageQueue，并让Looper绑定当前线程。但是我们从来步直接调用构造方法获取Looper对象，而是使用Looper的prepare()方法

### 2.2 prepare()方法

```java
private static void prepare(boolean quitAllowed) {
	if (sThreadLocal.get() != null) {
    	throw new RuntimeException("Only one Looper may be created per thread");
    }
	sThreadLocal.set(new Looper(quitAllowed));
}
```

* 说明：使用ThreadLocal对象来保存当前的Looper对象，ThreadLocal类可以对数据进行线程隔离，保证了当前的线程只能获取当前的线程的Looper对象，同时prepare()保证了当前线程只有一个Looper对象，间接保证了一个线程只有一个MessageQueue对象

### 2.3 loop()方法

```java
    public static void loop() {
        final Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        final MessageQueue queue = me.mQueue;

        // Make sure the identity of this thread is that of the local process,
        // and keep track of what that identity token actually is.
        Binder.clearCallingIdentity();
        final long ident = Binder.clearCallingIdentity();

        for (;;) {
            Message msg = queue.next(); // might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }

            // This must be in a local variable, in case a UI event sets the logger
            final Printer logging = me.mLogging;
            if (logging != null) {
                logging.println(">>>>> Dispatching to " + msg.target + " " +
                        msg.callback + ": " + msg.what);
            }

            final long slowDispatchThresholdMs = me.mSlowDispatchThresholdMs;

            final long traceTag = me.mTraceTag;
            if (traceTag != 0 && Trace.isTagEnabled(traceTag)) {
                Trace.traceBegin(traceTag, msg.target.getTraceName(msg));
            }
            final long start = (slowDispatchThresholdMs == 0) ? 0 : SystemClock.uptimeMillis();
            final long end;
            try {
                msg.target.dispatchMessage(msg);
                end = (slowDispatchThresholdMs == 0) ? 0 : SystemClock.uptimeMillis();
            } finally {
                if (traceTag != 0) {
                    Trace.traceEnd(traceTag);
                }
            }
            if (slowDispatchThresholdMs > 0) {
                final long time = end - start;
                if (time > slowDispatchThresholdMs) {
                    Slog.w(TAG, "Dispatch took " + time + "ms on "
                            + Thread.currentThread().getName() + ", h=" +
                            msg.target + " cb=" + msg.callback + " msg=" + msg.what);
                }
            }

            if (logging != null) {
                logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
            }

            // Make sure that during the course of dispatching the
            // identity of the thread wasn't corrupted.
            final long newIdent = Binder.clearCallingIdentity();
            if (ident != newIdent) {
                Log.wtf(TAG, "Thread identity changed from 0x"
                        + Long.toHexString(ident) + " to 0x"
                        + Long.toHexString(newIdent) + " while dispatching to "
                        + msg.target.getClass().getName() + " "
                        + msg.callback + " what=" + msg.what);
            }

            msg.recycleUnchecked();
        }
    }
```

* 说明：通过for(;;)来实现无限循环，从MessageQueue中循环取数据，如果取到了Message对象数据，就调用msg.target.dispatchMessage(msg)将msg交给handler对象来处理（msg.target是handler对象），最后调用 msg.recycleUnchecked()进行回收

## 三、Handler源码解析

### 3.1实例化

```java
    public Handler(Callback callback, boolean async) {
        if (FIND_POTENTIAL_LEAKS) {
            final Class<? extends Handler> klass = getClass();
            if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&
                    (klass.getModifiers() & Modifier.STATIC) == 0) {
                Log.w(TAG, "The following Handler class should be static or leaks might occur: " +
                    klass.getCanonicalName());
            }
        }

        mLooper = Looper.myLooper();
        if (mLooper == null) {
            throw new RuntimeException(
                "Can't create handler inside thread that has not called Looper.prepare()");
        }
        mQueue = mLooper.mQueue;
        mCallback = callback;
        mAsynchronous = async;
    }
```

* 说明：实例化过程中获取当前线程的Looper对象，再通过Looper获取MessageQueue对象，这样就可以方便的把消息加入到MessageQueue中

### 3.2发送消息sendMessageAtTime

```java
    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        MessageQueue queue = mQueue;
        if (queue == null) {
            RuntimeException e = new RuntimeException(
                    this + " sendMessageAtTime() called with no mQueue");
            Log.w("Looper", e.getMessage(), e);
            return false;
        }
        return enqueueMessage(queue, msg, uptimeMillis);
    }
```

* 不管是postDelayed还是postAtTime最终都会调用sendMessageAtTime方法。这个方法会调用enqueueMessage()方法把消息加入到队列中去

### 3.3消息加入队列enqueueMessage

```java
    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
        if (mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }
```

* 最终调用MessageQueue的enqueueMessage方法，把消息加入到队列中去







## 实践

### 2.1为什么需要Handler

* 当主线程处理一个消息超过5秒，就会出现ANR，所以需要把一些处理时间比较长的消息，放在一个单独的线程中进行处理，把处理以后的结果，返回给主线程运行，就需要用Handle进行线程间的通信

### 2.2Handler的作用
**2.2.1让线程延时执行**   

* final boolean postAtTime(Runnable r, long uptimeMillis)
* final boolean postDelayed(Runnable r, long delayMillis)

**2.2.2让任务在其他线程中执行，并返回结果**

***在新启动的线程中发送消息***

* 使用Handler对象的sendMessage()方法或者SendEmptyMessage()方法发送消息。

***在主线程中获取处理消息***

* 重写Handler类中处理消息的方法（void handleMessage(Message msg)），当新启动的线程发送消息时，消息发送到与之关联的MessageQueue。而Hanlder不断地从MessageQueue中获取并处理消息。

### 2.3Handler更新UI线程的一般使用
* 首先进行handler声明，复写handleMessage方法(在主线程中)，用来接收消息
```java
private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO 接收消息并且去更新UI线程上的控件内容
            if (msg.what == UPDATE) {
                // 更新界面上的textview
                tv.setText(String.valueOf(msg.obj));
            }
            super.handleMessage(msg);
        }
    };
```
* 子线程发送Message给ui线程表示自己任务已经执行完成，主线程可以做相应的操作了。
```java
new Thread() {
            @Override
            public void run() {
                // TODO 子线程中通过handler发送消息给handler接收，由handler去更新TextView的值
                try {
                       //do something

                        Message msg = new Message();
                        msg.what = UPDATE;                  
                        msg.obj = "更新后的值" ;
                        handler.sendMessage(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
```

### 2.4如何在子线程中使用Handler(子线程)

1. 调用Looper的prepare()方法为当前非主线程创建Looper对象，创建Looper对象时，他的构造器创建与之配套的MessageQueue
2. 创建Handle子类实例，重写handleMessage()方法，该方法处理来自其他线程的消息
3. 调用Looper的looper()方法来启动Looper
```java
使用这个handle实例在任何其他线程中发送消息，最终处理消息的代码都会在你创建的handle实例的线程中运行
```
