

[TOC]

# 理解Window和WindowManager

## Window和WIndowManager

### 一、示例

```java
//把一个Button添加到屏幕坐标（100，300）的位置上
Button button = new Button(this);
button.setText("BUTTON");
WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
       WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
       0, 0, PixelFormat.TRANSPARENT
);
layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
      | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
      | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
layoutParams.x = 100;
layoutParams.y = 300;
getWindowManager().addView(button, layoutParams);
```

###  二、WindowManager.LayoutParams的Flag常用参数

* FLAG_NOT_FOCUSABLE

  > 表示Window不需要获取焦点，也不需要接收各种输入事件，此标记会启用FLAG_NOT_TOUCH_MODAL，最终事件会直接传递给下层具焦点的Window

* FLAG_NOT_TOUCH_MODAL

  > 在此模式下，系统会将当前Window区域以外的单击事件传递给底层的Window，当前Window区域以内的单机事件则自己处理。一般来说需要开启此标记，否则其他Window将无法收到单击事件。

* FLAG_SHOW_WHEN_LOCKED

  > 开启此模式可以让Window显示在锁屏的界面上

### 三、WindowManager.LayoutParams的Type参数

* Window有三种类型：应用Window、子Window、系统Window。

  > 应用类Window对应着一个Activity。子Activity不能单独存在，它需要附属在特定的父Window之中，比如常见的Dialog。系统Window是需要声明权限在能创建的Window，比如Toast

* Window是分层的，层级大的会覆盖在层级小的Window的上面

  > 应用Window层级范围1-99；子Window层级范围1000-1999；系统Window层级范围2000-2999

### 四、WindowManager

* 添加View，更新View和删除View

  > addView(View view,ViewGroup.LayoutParams params)
  >
  > updateViewLayout(View view,ViewGroup.LayoutParams params)
  >
  > removeView(View view)

## Window的内部机制

### 一、概念

* 每一个Window都对应着一个View和一个ViewRootImpl，Window和View通过ViewRootImpl来建立联系。对Window的访问必须通过WindowManager。

### 二、Window的添加过程

* WindowManager是一个接口，它的真正实现是WindowManagerImpl类，WindowManagerImpl并没有真正的实现接口的方法，而是交给了WindowManagerGlobal
* WindowManagerGlobal的addView方法

1. 检查参数是否合法，如果是子Window那么还需要调整一些布局参数

2. 创建ViewRootImpl并将View添加到列表中

   ```java
   //几个列表
   //mViews存储的是所有Window所对应的View
   private final ArrayList<View> mViews = new ArrayList<View>();
   //mRoots存储的是所有Window所对应的ViewRootImpl
   private final ArrayList<ViewRootImpl> mRoots = new ArrayList<ViewRootImpl>();
   //mParams存储的是所有Window所对应的布局参数
   private final ArrayList<WindowManager.LayoutParams> mParams = new ArrayList<WindowManager.LayoutParams>();
   //mDyingViews则存储了那些正在被删除的View对象
   private final ArraySet<View> mDyingViews = new ArraySet<View>();
   ```

3. 通过ViewRootImpl来更新界面并完成Window的添加过程

   > Window的添加过程是一次IPC调用

### 三、Window的删除过程

* WindowManagerGlobal的removeView方法

  > 首先通过findViewLocked来遍历数组查找待删除的View的索引，然后再调用removeViewLocked来做进一步删除。removeViewLocked通过ViewRootImpl来完成删除操作，在WindowManager中提供两种删除接口removeView和removeViewImmediate。removeView表示异步删除，removeViewImmediate表示同步删除。一般使用removeView。

  > removeView异步删除，具体操作有ViewRootImpl的die方法来完成，异步时，die方法会发送请求删除的消息后就立刻返回了，这时候并没有真正完成删除操作。此时View会被添加到mDyingViews中。ViewRootImpl中的Handler会处理消息，并调用doDie方法，

  > 在doDie方法中会调用dispatchDetachedFormWindow方法，真正删除View的逻辑就在该方法中实现，它的主要工作是:
  >
  >   1. 垃圾回收的相关工作，比如清除数据和消息、移除回调
  >   2. 通过Session的remove方法删除Window：mWindowSession.remove(mWindow),这也是IPC过程，最终会调用WindowManagerService的removeWindow方法
  >   3. 调用View的dispatchDetachedFormWindow方法，在内部会调用View的onDetachedFromWindow()以及onDeteachedFormWindowInternal()。可以在onDetachedFromWindow()方法内部做一些资源回收的工作，比如终止动画、停止线程等。
  >   4. 调用WindowManagerGlobal的doRemoveView方法刷新数据，包括mRoots、mParams以及mDyingViews，需要把当前Window所关联的这三类对象从列表中删除。


### 四、Window的更新过程

* WindowManagerGlobal的updateViewLayout方法

  > 更新View的LayoutParams并替换掉老的LayoutParams，再通过setLayoutParams更新ViewRootImpl的LayoutParams。在ViewRootImpl中通过scheduleTraversals方法对View重写布局。ViewRootImpl会通过WindowSession来更新Window视图。

### 五、Window的创建过程

1. Activity的Window的创建过程P304