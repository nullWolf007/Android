[TOC]
- <!-- TOC -->
- [ Handler和Message和MessageQueue和Looper](#Handler和Message和MessageQueue和Looper)
  - [ 一、前言](#一前言)
    - [ 1.1Message](#11Message)
    - [ 1.2MessageQueue](#12MessageQueue)
    - [ 1.3Looper](#13Looper)
    - [ 1.4Handler](#14Handler)
    - [ 1.5说明](#15说明)
    - [ 1.6图解](#16图解)
  - [ 二、Looper源码解析](#二Looper源码解析)
    - [ 2.1 构造方法](#21-构造方法)
    - [ 2.2 prepare()方法](#22-prepare方法)
    - [ 2.3 loop()方法](#23-loop方法)
  - [ 三、Handler源码解析](#三Handler源码解析)
    - [ 3.1实例化](#31实例化)
    - [ 3.2发送消息sendMessageAtTime](#32发送消息sendMessageAtTime)
    - [ 3.3消息加入队列enqueueMessage](#33消息加入队列enqueueMessage)
    - [ 3.4 处理消息dispatchMessage](#34-处理消息dispatchMessage)
  - [ 四、MessageQueue源码解析](#四MessageQueue源码解析)
    - [ 4.1构造方法](#41构造方法)
    - [ 4.2next()](#42next)
    - [ 4.3消息入栈enqueueMessage()](#43消息入栈enqueueMessage)
  - [ 五、Message源码解析](#五Message源码解析)
    - [ 5.1初始化](#51初始化)
    - [ 5.2消息回收recycleUnchecked](#52消息回收recycleUnchecked)
  - [ 六、整体流程图](#六整体流程图)
  - [ 七、实践](#七实践)
    - [ 7.1为什么需要Handler](#71为什么需要Handler)
    - [ 7.2Handler的作用](#72Handler的作用)
    - [ 7.3Handler更新UI线程的一般使用](#73Handler更新UI线程的一般使用)
    - [ 7.4如何在子线程中使用Handler(子线程)](#74如何在子线程中使用Handler子线程)
  <!-- /TOC -->
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

### 3.4 处理消息dispatchMessage

```java
    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }
```

* 说明：前面Looper.loop的时候，我们发现获取到消息的时候，会msg.target.dispatchMessage(msg)，也就是handler的dispatchMessage进行处理。handler处理消息就是调用了handleMessage啊方法，而这个方法就是我们需要重写的方法。同样的我i们也可以创建handler的时候实现Callback接口，这样就是进入handleCallback方法

## 四、MessageQueue源码解析

### 4.1构造方法

```java
    MessageQueue(boolean quitAllowed) {
        mQuitAllowed = quitAllowed;
        mPtr = nativeInit();
    }
```

* 说明：MessageQueue初始化的时候会同时初始化底层的NativeMessageQueue对象，并且持有NativeMessageQueue的内存地址(long)

### 4.2next()

```java
    Message next() {
        // Return here if the message loop has already quit and been disposed.
        // This can happen if the application tries to restart a looper after quit
        // which is not supported.
        final long ptr = mPtr;
        if (ptr == 0) {
            return null;
        }

        int pendingIdleHandlerCount = -1; // -1 only during first iteration
        int nextPollTimeoutMillis = 0;
        for (;;) {
            if (nextPollTimeoutMillis != 0) {
                Binder.flushPendingCommands();
            }

            nativePollOnce(ptr, nextPollTimeoutMillis);

            synchronized (this) {
                // Try to retrieve the next message.  Return if found.
                final long now = SystemClock.uptimeMillis();
                Message prevMsg = null;
                Message msg = mMessages;
                if (msg != null && msg.target == null) {
                    // Stalled by a barrier.  Find the next asynchronous message in the queue.
                    do {
                        prevMsg = msg;
                        msg = msg.next;
                    } while (msg != null && !msg.isAsynchronous());
                }
                if (msg != null) {
                    if (now < msg.when) {
                        // Next message is not ready.  Set a timeout to wake up when it is ready.
                        nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                    } else {
                        // Got a message.
                        mBlocked = false;
                        if (prevMsg != null) {
                            prevMsg.next = msg.next;
                        } else {
                            mMessages = msg.next;
                        }
                        msg.next = null;
                        if (DEBUG) Log.v(TAG, "Returning message: " + msg);
                        msg.markInUse();
                        return msg;
                    }
                } else {
                    // No more messages.
                    nextPollTimeoutMillis = -1;
                }

                // Process the quit message now that all pending messages have been handled.
                if (mQuitting) {
                    dispose();
                    return null;
                }

                // If first time idle, then get the number of idlers to run.
                // Idle handles only run if the queue is empty or if the first message
                // in the queue (possibly a barrier) is due to be handled in the future.
                if (pendingIdleHandlerCount < 0
                        && (mMessages == null || now < mMessages.when)) {
                    pendingIdleHandlerCount = mIdleHandlers.size();
                }
                if (pendingIdleHandlerCount <= 0) {
                    // No idle handlers to run.  Loop and wait some more.
                    mBlocked = true;
                    continue;
                }

                if (mPendingIdleHandlers == null) {
                    mPendingIdleHandlers = new IdleHandler[Math.max(pendingIdleHandlerCount, 4)];
                }
                mPendingIdleHandlers = mIdleHandlers.toArray(mPendingIdleHandlers);
            }

            // Run the idle handlers.
            // We only ever reach this code block during the first iteration.
            for (int i = 0; i < pendingIdleHandlerCount; i++) {
                final IdleHandler idler = mPendingIdleHandlers[i];
                mPendingIdleHandlers[i] = null; // release the reference to the handler

                boolean keep = false;
                try {
                    keep = idler.queueIdle();
                } catch (Throwable t) {
                    Log.wtf(TAG, "IdleHandler threw exception", t);
                }

                if (!keep) {
                    synchronized (this) {
                        mIdleHandlers.remove(idler);
                    }
                }
            }

            // Reset the idle handler count to 0 so we do not run them again.
            pendingIdleHandlerCount = 0;

            // While calling an idle handler, a new message could have been delivered
            // so go back and look again for a pending message without waiting.
            nextPollTimeoutMillis = 0;
        }
    }
```

* next()中，因为消息队列是按照延迟时间排序的，所以先考虑延迟最小的也就是头消息。当头消息为空，说明队列中没有消息了，nextPollTimeoutMIllis就被赋值为-1,当头消息延迟时间大于当前时间，把延迟时间和当前时间的差值赋给nextPollTimeoutMIllis
* 当消息延迟时间小于等于0，直接返回msg给handler处理
* nativePollOnce(ptr, nextPollTimeoutMillis)方法是native底层实现堵塞逻辑，堵塞状态会到时间唤醒，也可被新消息唤醒，一旦唤醒会重新获取头消息，重新评估是否堵塞或者直接返回消息

### 4.3消息入栈enqueueMessage()

```java
    boolean enqueueMessage(Message msg, long when) {
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        if (msg.isInUse()) {
            throw new IllegalStateException(msg + " This message is already in use.");
        }

        synchronized (this) {
            if (mQuitting) {
                IllegalStateException e = new IllegalStateException(
                        msg.target + " sending message to a Handler on a dead thread");
                Log.w(TAG, e.getMessage(), e);
                msg.recycle();
                return false;
            }

            msg.markInUse();
            msg.when = when;
            Message p = mMessages;
            boolean needWake;
            if (p == null || when == 0 || when < p.when) {
                // New head, wake up the event queue if blocked.
                msg.next = p;
                mMessages = msg;
                needWake = mBlocked;
            } else {
                // Inserted within the middle of the queue.  Usually we don't have to wake
                // up the event queue unless there is a barrier at the head of the queue
                // and the message is the earliest asynchronous message in the queue.
                needWake = mBlocked && p.target == null && msg.isAsynchronous();
                Message prev;
                for (;;) {
                    prev = p;
                    p = p.next;
                    if (p == null || when < p.when) {
                        break;
                    }
                    if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                msg.next = p; // invariant: p == prev.next
                prev.next = msg;
            }

            // We can assume mPtr != 0 because mQuitting is false.
            if (needWake) {
                nativeWake(mPtr);
            }
        }
        return true;
    }
```

* 消息入栈时，首先会判断新消息如果是第一个消息或者新消息没有延迟或者新消息延迟时间小于队列第一个消息的，都会立刻对这个消息进行处理。只有当消息延迟大于队列消息时，才会依次遍历消息队列，将消息按延迟时间插入消息队列响应位置。

## 五、Message源码解析

### 5.1初始化

```java
    public static Message obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new Message();
    }
```

* 建议使用obtain()获取Message对象，因为Message维护着一个消息池，这个消息池的数据结构是单向链表，优先从池里拿数据，如果池里没有再创建对象。如果Message对象已存在，可以使用obtain(msg)方法，最终也会调用obtain()

### 5.2消息回收recycleUnchecked

```java
    void recycleUnchecked() {
        // Mark the message as in use while it remains in the recycled object pool.
        // Clear out all other details.
        flags = FLAG_IN_USE;
        what = 0;
        arg1 = 0;
        arg2 = 0;
        obj = null;
        replyTo = null;
        sendingUid = -1;
        when = 0;
        target = null;
        callback = null;
        data = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }
```

* 消息的回收不是将Message对象销毁，而是将Message对象的值恢复初始化值然后返回池里，等待使用。这种机制就和线程池的机制是同样的原理，减少申请开辟和回收空间的时间

## 六、整体流程图

![handler工作机制](https://github.com/nullWolf007/images/raw/master/android/book/handler%E5%B7%A5%E4%BD%9C%E6%9C%BA%E5%88%B6.jpg)

## 七、实践

### 7.1为什么需要Handler

* 当主线程处理一个消息超过5秒，就会出现ANR，所以需要把一些处理时间比较长的消息，放在一个单独的线程中进行处理，把处理以后的结果，返回给主线程运行，就需要用Handle进行线程间的通信

### 7.2Handler的作用
**7.2.1让线程延时执行**   

* final boolean postAtTime(Runnable r, long uptimeMillis)
* final boolean postDelayed(Runnable r, long delayMillis)

**7.2.2让任务在其他线程中执行，并返回结果**

***在新启动的线程中发送消息***

* 使用Handler对象的sendMessage()方法或者SendEmptyMessage()方法发送消息。

***在主线程中获取处理消息***

* 重写Handler类中处理消息的方法（void handleMessage(Message msg)），当新启动的线程发送消息时，消息发送到与之关联的MessageQueue。而Hanlder不断地从MessageQueue中获取并处理消息。

### 7.3Handler更新UI线程的一般使用
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

### 7.4如何在子线程中使用Handler(子线程)

1. 调用Looper的prepare()方法为当前非主线程创建Looper对象，创建Looper对象时，他的构造器创建与之配套的MessageQueue
2. 创建Handle子类实例，重写handleMessage()方法，该方法处理来自其他线程的消息
3. 调用Looper的looper()方法来启动Looper
```java
使用这个handle实例在任何其他线程中发送消息，最终处理消息的代码都会在你创建的handle实例的线程中运行
```
