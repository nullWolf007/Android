[TOC]

# Android跨进程IPC之Binde和AIDL

### 参考文章

* [ **Android Binder机制全面解析** ](https://www.jianshu.com/p/b5cc1ef9f917)
* [**3分钟带你看懂android中的Binder机制**](https://segmentfault.com/a/1190000018317841)
* [**安卓开发艺术探索**](https://book.douban.com/subject/26599538/)
* [**Android进阶——Android跨进程通讯机制之Binder、IBinder、Parcel、AIDL**](https://blog.csdn.net/qq_30379689/article/details/79451596)

## 一、前言

### 1.1 Liunx相关知识

#### 1.1.1 Liunx进程空间划分

* 一个进程空间分为**用户空间**和**内核空间(Kernel)**，即把进程内用户和内核隔离开，所有进程公用一个内核空间

* 进程间，用户空间的数据不可共享

* 进程间，内核空间的数据共享

* 进程内，用户空间和内核空间进行交互需要通过系统调用，主要函数

  > copy_from_user()：将用户空间的数据拷贝到内核空间
  >
  > copy_to_user()：将内核空间的数据拷贝到用户空间

* 为了保证**安全性**和**独立性**，一个进程不能直接操作或者访问另一个进程，即Android的进程是相互独立、隔离的

* 传统跨进程通信——管道队列模式

  ![管道队列模式](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/%E7%AE%A1%E9%81%93%E9%98%9F%E5%88%97%E6%A8%A1%E5%BC%8F%E8%B7%A8%E8%BF%9B%E7%A8%8B.png)

#### 1.1.2内存映射

![内存映射1](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/%E5%86%85%E5%AD%98%E6%98%A0%E5%B0%841.png)

* 内存映射的实现过程主要是通过Linux系统下的函数：mmap()。该函数的作用就是**创建虚拟内存区域**和**与共享对象建立映射关系**

* **内存映射的左右**

  > 1. 实现内存共享：如跨进程通信
  > 2. 提高数据读/写效率：如文件读/写操作

### 1.2 IPC的定义

* IPC即Inter-Process Communication的缩写，即进程间通信或者跨进程通信

* process属性

  ```xml
  android:process=""
  ```

### 1.3 Android中IPC常用的方式及优缺点

|      名称       |                             优点                             |                             缺点                             |                           使用场景                           |
| :-------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
|     Bundle      |                           简单易用                           |                 只能传输Bundle支持的数据类型                 |                    四大组件间的进程间通信                    |
|    文件共享     |                           简单易用                           |    不适合高并发场景，并且**无法**做到进程间的**即时通信**    |        无并发访问情形，交换简单的数据实时性不高的场景        |
|      AIDL       |          功能强大，支持一对多并发通信，支持实时通信          |                使用稍复杂，需要处理好线程同步                |                    一对多通信且有RPC需求                     |
|    Messenger    |          功能一般，支持一对多串行通信，支持实时通信          | 不能很好处理高并发情形，不支持RPC，数据通过Message进行传输，因此只能传输Bundle支持的数据格式 | 低并发的一对多即时通信，无RPC需求，或者无需要返回结果的RPC需求 |
| ContentProvider | 在数据源访问方面功能强大，支持一对多并发数据共享，可通过Call方法扩展其他操作 |       可以理解为受约束的AIDL。主要提供数据源的CRUD操作       |                   一对多的进程间的数据共享                   |
|     Socket      |   功能强大，可以通过网络传输字节流，支持一对多并发实时通信   |            实现细节稍微有点繁琐，不支持直接的RPC             |                         网络数据交换                         |



### 1.4 Binder机制定义

* 是一种Android实现跨进程通讯的方式
* Binder(远程对象基础类)和IBinder(远程调用机制)
* Binder继承了IBinder
* Binder采用了内存映射的方式，所以只用拷贝一次

### 1.5 AIDL定义

* AIDL(Android Interface Definition Language)是一种接口定义语言，用于生成可以在Android设备上两个进程之间进行进程间通信的代码。其内部是通过Binder机制来实现的

### 1.6 应用多进程

#### 1.6.1 特点

* 一个应用不同进程的组件会拥有独立的虚拟机、Application以及内存空间

#### 1.6.2 应用多进程存在的问题

* 静态成员和单例模式完全失效

* 线程同步机制完全失效

* SharedPreferences的可靠性下降

* Application会多次创建

## 二、Binder概述

### 2.1 Android为什么选择Binder

* Android是基于Liunx内核的，所以Android为了实现进程间通信，有liunx的许多方法，如管道、socket等方式。既然Android选择Binder，则说明其他方式存在一些问题。
* 进程间通信考虑两个方面：一个是**性能**，一个是**安全**。
* 性能方面：传统的管道队列模式采用内存缓冲区的方式，数据需要拷贝两次，而Binder只用拷贝一次；scoket传输效率低，开销大
* 安全方面：Android作为一个开放式，拥有众多开发者的的平台，应用程序的来源广泛，确保终端安全是非常重要的，传统的IPC通信方式没有任何措施，基本依靠上层协议，其一无法确认对方可靠的身份，Android为每个安装好的应用程序分配了自己的UID，故进程的UID是鉴别进程身份的重要标志，传统的IPC要发送类似的UID也只能放在数据包里，但也容易被拦截，恶意进攻，socket则需要暴露自己的ip和端口，知道这些恶意程序则可以进行任意接入。
* Binder只需要拷贝一次，性能也不低，而且采用传统的C/S结构，稳定性强，发送添加UID/PID，安全性强

### 2.2 Binder通信模型

* Client进程：跨进程通讯的客户端（运行在某个进程）
* Server进程：跨进程通讯的服务端（运行在某个进程）
* Binder驱动：跨进程通讯的介质
* ServiceManager：跨进程通讯中提供服务的注册和查询（运行在System进程）

### 2.3 模型通讯流程

* Server端通过Binder驱动在ServiceManager中注册
* Client端通过Binder驱动获取ServerManager中注册的Server端
* Client端通过Binder驱动和Server端进行通讯

### 2.4 模型通讯原理

![**Binder模型通讯原理**](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/Binder%E6%A8%A1%E5%9E%8B%E9%80%9A%E8%AE%AF%E5%8E%9F%E7%90%86.png)

* Service端通过Binder驱动在ServiceManager的查找表中注册Object对象的add方法
* Client端通过Binder驱动在在ServiceManager的查找表中找到Object对象的add方法，并返回proxy对象的add方法，add方法是个空实现，proxy对象也不是真正的Object对象，而是通过Binder驱动封装好的代理类的add方法
* 当Client端调用add方法时，Client端会调用proxy对象的add方法，通过Binder驱动去请求ServiceManager来找到Service端真正对象，然后调用Service端的add方法

### 2.5 Binder对象和Binder驱动

* Binder对象：Bidner机制中进行进程间通讯的对象，对于Service端为Binder本地对象，对于Client端为Bidner代理对象
* Binder驱动：Bidner机制中进行进程间通讯的介质，Binder驱动回对具有跨进程传递能力的对象做特殊处理，自动完成代理对象和本地对象的转换

> 由于Binder驱动会对具有跨进程传递能力的对象做特殊处理，自动完成代理对象和本地对象的转换，因此在驱动中保存了每一个跨越进程的Binder对象的相关信息，Binder本地对象（或Binder实体）保存在binder_node的数据结构，Binder代理对象（或Binder引用/句柄）保存在binder_red的数据结构

### 2.6 Java层的Binder

* Binder类：Binder本地对象
* BinderProxy类：是Binder类的内部类，它代表本地代理对象
* Parcel类：是一个容器，它主要用于存储序列化数据，然后通过Binder在进程间传递这些数据
* IBinder接口：代表一种跨进程传输的能力，实现这个接口，就能将这个对象进行跨进程传递
* IInterface接口：cliten端与server端调用契约，实现这个接口，就代表server对象

### 2.7 Binder跨进程概述图

![**Binder跨进程通信**](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/Binder%E8%B7%A8%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1.png)

### 2.8 Android开发之AIDL

* Binder类和BinderProxy类都继承自IBinder，因此都具有跨进程传输的能力，在跨越进程的时候，Binder驱动会自动完成这两个对象的转换。IBinder是远程对象的基本接口，是为高性能设计的轻量级远程调用机制的核心部分，但它不仅用于远程带哦用，也用于进程间调用。IBinder接口定义了与远程对象交互的协议，建议不要直接实现这个接口，而应该从Binder派生。Binder实现了IBinder接口，但是一般不需要直接实现此类，而是根据你的需要由开发包中的工具生成，也就是**AIDL**，通过AIDL语言定义远程对象的方法，然后使用AIDL工具生成Binder的派生类，然后使用它

## 三、AIDL

### 3.1 AIDL支持的数据类型

* 基本数据类型（int,long,char,boolean,double等）

* String和CharSequence

* List:只支持ArrayList，里面每个元素都必须能够被AIDL支持

* Map：只支持HashMap,里面每个元素都必须被AIDL支持，包括key和value

* Parcelable:所有实现了Parcelable接口的对象

* AIDL：所有的AIDL接口本身也可以在AIDL文件中使用；AIDL中除了基本数据类型，其他数据类型必须标上方向：in out inout ；AIDL接口只支持方法，不支持静态变量

### 3.2 AIDL实例

* 查看代码实例请点击[AIDL实例](https://github.com/nullWolf007/ToolProject/tree/master/AIDL%E7%9A%84demo%E7%9A%84%E6%A0%B8%E5%BF%83%E4%BB%A3%E7%A0%81/app/src/main)

### 3.3 实例说明

#### 3.3.1 DESCRIPTION

* Binder的唯一标识，一般用当前Binder的类名表示

#### 3.3.2 asInterface()

* asInterface(android.os.IBinder obj)

* 将服务端的Binder对象转换成客户端所需的AIDL接口类型的对象；如果客户端和服务端位于同一进程，返回的是服务端的Stub对象本身；如果不在同一进程，返回的是系统封装后的Stub.proxy对象

#### 3.3.3 asBinder

* 返回当前Binder对象

#### 3.3.4 onTransact()

* onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)
* 运行在服务端中的Binder线程中，当客户端发起跨进程请求时，远程请求会通过系统底层封装后交由此方法进行处理；服务端通过code可以确定客户端所请求的目标方法是什么，接着从data中取出目标方法所需的参数，然后执行目标方法；执行完毕后，像reply中写入返回值，如果此方法返回false，则客户端的请求会失败。

#### 3.3.5 Proxy#getBookList

* 运行在客户端。首先创建该方法所需要的输入型Parcel对象\_data、输出型Parcel对象\_reply和返回值对象List；然后把该方法的参数信息写入\_data中，接着调用transact方法来发起RPC(远程过程请求)，同时当前线程挂起；然后服务端的onTransact方法会被调用，直至RPC过程返回后，当前线程继续执行，并从\_reply中取出RPC过程的返回结果，最后返回\_reply中的数据

#### 3.3.6 Proxy#addBook

* 同上，只是没有返回值

## 四、Messenger

### 4.1 说明

* 通过Messenger可以在不同的进程中传递Message对象，在Message中放入我们需要传递的数据，就可以轻松的实现数据的进程间传递了。Messenger是一种轻量级的IPC方案，它的底层实现是AIDL。只能传输Bundle支持的数据格式。

### 4.1 Messenger实例







  









### AIDL示例了解Binder

5. linkToDeath和unlinkToDeath死亡代理

## Android中的IPC方式

1. 使用Bundle

2. 使用文件共享：保存成文件，高并发都会存在问题；SharedPreferences实际就是保存成xml文件，除存在高并发问题，在多进程模式下也不可靠。

3. 使用Messenger(底层使用的AIDL)

   * 一次处理一个请求，串行的处理方式，因此在服务端不用考虑线程同步问题

   * 使用步骤：

     * 服务端进程：创建一个Service来处理客户端的连接请求；创建一个Handler并通过它来创建一个Messenger对象；在Service的onBind中返回这个Messenger对象底层的Binder即可。在清单文件注册一下。

       ```java
       public class MessengerService extends Service {
       
           private static class MessengerHandler extends Handler {
               @Override
               public void handleMessage(Message msg) {
                   switch (msg.what) {
                       case 1:
                           Log.e("service", "handleMessage: " + msg.getData().getString("msg"));
                           Messenger clientMsg = msg.replyTo;
                           Message replayMessage = Message.obtain(null,2);
                           Bundle bundle = new Bundle();
                           bundle.putString("reply","我已经收到你的消息，稍后回复你");
                           replayMessage.setData(bundle);
                           try {
                               clientMsg.send(replayMessage);
                           } catch (RemoteException e) {
                               e.printStackTrace();
                           }
                           break;
                       default:
                           super.handleMessage(msg);
                   }
               }
           }
       
           private final Messenger messenger = new Messenger(new MessengerHandler());
       
           @Nullable
           @Override
           public IBinder onBind(Intent intent) {
               return messenger.getBinder();
           }
       }
       
       ```

       

     * 客户端进程：首先绑定服务端的Service；绑定成功后用服务端返回的IBinder对象创建一个Messager，通过这个Messenger就可以向服务器发送消息了，发送类型必须为Message对象； 如果需要服务端能够回应客户端，在客户端需要创建一个Handler并创建一个新的Messenger，并把这个Messenger对象通过Message的replyTo参数传递给服务端，服务端通过这个replayTo参数就可以回应客户端

       ```java
       public class MainActivity extends AppCompatActivity {
       
           private Messenger messenger;
       
           private static class MessengerHandler extends Handler {
               @Override
               public void handleMessage(Message msg) {
                   switch (msg.what) {
                       case 2:
                           Log.e("client", "handleMessage: " + msg.getData().getString("reply"));
                           break;
                       default:
                           super.handleMessage(msg);
                   }
               }
           }
       
           private Messenger getReplayMessenger = new Messenger(new MessengerHandler());
       
           private ServiceConnection serviceConnection = new ServiceConnection() {
               @Override
               public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                   messenger = new Messenger(iBinder);
                   Message msg = Message.obtain(null, 1);
                   Bundle bundle = new Bundle();
                   bundle.putString("msg", "hello,this is a client");
                   msg.setData(bundle);
       
                   //回馈
                   msg.replyTo = getReplayMessenger;
       
                   try {
                       messenger.send(msg);
                   } catch (RemoteException e) {
                       e.printStackTrace();
                   }
               }
       
               @Override
               public void onServiceDisconnected(ComponentName componentName) {
       
               }
           };
       
           @Override
           protected void onCreate(Bundle savedInstanceState) {
               super.onCreate(savedInstanceState);
               setContentView(R.layout.activity_main);
               Intent intent = new Intent(this, MessengerService.class);
               bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
           }
       
           @Override
           protected void onDestroy() {
               unbindService(serviceConnection);
               super.onDestroy();
           }
       }
       ```

     

4. 使用AIDL

   * 服务端：创建一个Service用来监听客户端的连接请求，然后创建一个AIDL文件，将暴露给客户端的接口在AIDL文件中声明，最后在Service中实现这个AIDL接口。

   * 客户端：绑定服务器的Service，绑定成功后，将服务端返回的Binder对象转成AIDL接口所属的类型，接着就可以调用AIDL中方法。

   * AIDL接口的创建：AIDL的包结构在服务端和客户端要保持一致

     ```java
     package com.example.inspeeding_yf006.learn.bean;
     
     import com.example.inspeeding_yf006.learn.bean.BookBean;
     
     interface IBookManager {
         List<BookBean> getBookList();
         void addBook(in BookBean bookBean);
     }
     ```

   * 远程服务端Service的实现：
   
   * 客户端的实现
   
   * P73


​     