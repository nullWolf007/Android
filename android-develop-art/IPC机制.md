

[TOC]

# IPC机制

## 一、前言

- IPC即Inter-Process Communication的缩写，即进程间通信或者跨进程通信

- process属性

  ```xml
   android:process=""
  ```

  

## 二、多进程

### 1.特点

- 不同进程的组件会拥有独立的虚拟机、Application以及内存空间

### 2.多进程存在的问题

（1）静态成员和单例模式完全失效

（2）线程同步机制完全失效

（3）SharedPreferences的可靠性下降

（4）Application会多次创建



## 三、序列化

1. 注意点

   - 静态成员属于类不属于对象，不会参与序列化过程
   - 用transient关键字标记的成员变量不参与序列化过程

   

## Binder

### AIDL示例了解Binder

1. 创建实体类

   ```java
   package com.example.inspeeding_yf006.learn.bean;
   
   import android.os.Parcel;
   import android.os.Parcelable;
   
   public class BookBean implements Parcelable {
   
       private int bookId;
       private String bookName;
   
       public BookBean(int bookId, String bookName) {
           this.bookId = bookId;
           this.bookName = bookName;
       }
   
       @Override
       public int describeContents() {
           return 0;
       }
   
       @Override
       public void writeToParcel(Parcel parcel, int i) {
           parcel.writeInt(bookId);
           parcel.writeString(bookName);
       }
   
       public static final Creator<BookBean> CREATOR = new Creator<BookBean>() {
           @Override
           public BookBean createFromParcel(Parcel in) {
               return new BookBean(in);
           }
   
           @Override
           public BookBean[] newArray(int size) {
               return new BookBean[size];
           }
       };
   
       protected BookBean(Parcel in) {
           bookId = in.readInt();
           bookName = in.readString();
       }
   }
   ```

   

2. 在main下创建aidl包，在aidl下，创建与实体类相同的路径，然后创建实体类对应的aidl文件

   ```java
   // BookBean.aidl
   package com.example.inspeeding_yf006.learn.bean;
   
   parcelable BookBean;
   ```

3. 创建对应的aidl

   ```java
   package com.example.inspeeding_yf006.learn.bean;
   
   import com.example.inspeeding_yf006.learn.bean.BookBean;
   
   interface IBookManager {
       List<BookBean> getBookList();
       void addBook(in BookBean bookBean);
   }
   ```

4. 查看generated目录下对应的java文件

   ```java
   /*
    * This file is auto-generated.  DO NOT MODIFY.
    * Original file: E:\\project\\study\\app\\src\\main\\aidl\\com\\example\\inspeeding_yf006\\learn\\bean\\IBookManager.aidl
    */
   package com.example.inspeeding_yf006.learn.bean;
   
   public interface IBookManager extends android.os.IInterface {
       /**
        * Local-side IPC implementation stub class.
        */
       public static abstract class Stub extends android.os.Binder implements com.example.inspeeding_yf006.learn.bean.IBookManager {
           private static final java.lang.String DESCRIPTOR = "com.example.inspeeding_yf006.learn.bean.IBookManager";
   
           /**
            * Construct the stub at attach it to the interface.
            */
           public Stub() {
               this.attachInterface(this, DESCRIPTOR);
           }
   
           /**
            * Cast an IBinder object into an com.example.inspeeding_yf006.learn.bean.IBookManager interface,
            * generating a proxy if needed.
            */
           public static com.example.inspeeding_yf006.learn.bean.IBookManager asInterface(android.os.IBinder obj) {
               if ((obj == null)) {
                   return null;
               }
               android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
               if (((iin != null) && (iin instanceof com.example.inspeeding_yf006.learn.bean.IBookManager))) {
                   return ((com.example.inspeeding_yf006.learn.bean.IBookManager) iin);
               }
               return new com.example.inspeeding_yf006.learn.bean.IBookManager.Stub.Proxy(obj);
           }
   
           @Override
           public android.os.IBinder asBinder() {
               return this;
           }
   
           @Override
           public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
               switch (code) {
                   case INTERFACE_TRANSACTION: {
                       reply.writeString(DESCRIPTOR);
                       return true;
                   }
                   case TRANSACTION_getBookList: {
                       data.enforceInterface(DESCRIPTOR);
                       java.util.List<com.example.inspeeding_yf006.learn.bean.BookBean> _result = this.getBookList();
                       reply.writeNoException();
                       reply.writeTypedList(_result);
                       return true;
                   }
                   case TRANSACTION_addBook: {
                       data.enforceInterface(DESCRIPTOR);
                       com.example.inspeeding_yf006.learn.bean.BookBean _arg0;
                       if ((0 != data.readInt())) {
                           _arg0 = com.example.inspeeding_yf006.learn.bean.BookBean.CREATOR.createFromParcel(data);
                       } else {
                           _arg0 = null;
                       }
                       this.addBook(_arg0);
                       reply.writeNoException();
                       return true;
                   }
               }
               return super.onTransact(code, data, reply, flags);
           }
   
           private static class Proxy implements com.example.inspeeding_yf006.learn.bean.IBookManager {
               private android.os.IBinder mRemote;
   
               Proxy(android.os.IBinder remote) {
                   mRemote = remote;
               }
   
               @Override
               public android.os.IBinder asBinder() {
                   return mRemote;
               }
   
               public java.lang.String getInterfaceDescriptor() {
                   return DESCRIPTOR;
               }
   
               @Override
               public java.util.List<com.example.inspeeding_yf006.learn.bean.BookBean> getBookList() throws android.os.RemoteException {
                   android.os.Parcel _data = android.os.Parcel.obtain();
                   android.os.Parcel _reply = android.os.Parcel.obtain();
                   java.util.List<com.example.inspeeding_yf006.learn.bean.BookBean> _result;
                   try {
                       _data.writeInterfaceToken(DESCRIPTOR);
                       mRemote.transact(Stub.TRANSACTION_getBookList, _data, _reply, 0);
                       _reply.readException();
                       _result = _reply.createTypedArrayList(com.example.inspeeding_yf006.learn.bean.BookBean.CREATOR);
                   } finally {
                       _reply.recycle();
                       _data.recycle();
                   }
                   return _result;
               }
   
               @Override
               public void addBook(com.example.inspeeding_yf006.learn.bean.BookBean bookBean) throws android.os.RemoteException {
                   android.os.Parcel _data = android.os.Parcel.obtain();
                   android.os.Parcel _reply = android.os.Parcel.obtain();
                   try {
                       _data.writeInterfaceToken(DESCRIPTOR);
                       if ((bookBean != null)) {
                           _data.writeInt(1);
                           bookBean.writeToParcel(_data, 0);
                       } else {
                           _data.writeInt(0);
                       }
                       mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
                       _reply.readException();
                   } finally {
                       _reply.recycle();
                       _data.recycle();
                   }
               }
           }
   
           static final int TRANSACTION_getBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
           static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
       }
   
       public java.util.List<com.example.inspeeding_yf006.learn.bean.BookBean> getBookList() throws android.os.RemoteException;
   
       public void addBook(com.example.inspeeding_yf006.learn.bean.BookBean bookBean) throws android.os.RemoteException;
   }
   ```

   - DESCRIPTION

     > Binder的唯一标识，一般用当前Binder的类名表示

   - asInterface(android.os.IBinder obj)

     > 将服务端的Binder对象转换成客户端所需的AIDL接口类型的对象；
     >
     > 如果客户端和服务端位于同一进程，返回的是服务端的Stub对象本身
     >
     > 如果不在同一进程，返回的是系统封装后的Stub.proxy对象

   - asBinder

     > 返回当前Binder对象

   - onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)

     > 运行在服务端中的Binder线程中，当客户端发起跨进程请求时，远程请求会通过系统底层封装后交由此方法进行处理
     >
     > 服务端通过code可以确定客户端所请求的目标方法是什么
     >
     > 接着从data中取出目标方法所需的参数，然后执行目标方法
     >
     > 执行完毕后，像reply中写入返回值
     >
     > 如果此方法返回false，则客户端的请求会失败

   - Proxy#getBookList

     > 运行在客户端。首先创建该方法所需要的输入型Parcel对象\_data、输出型Parcel对象\_reply和返回值对象List；
     >
     > 然后把该方法的参数信息写入\_data中，接着调用transact方法来发起RPC(远程过程请求)，同时当前线程挂起；
     >
     > 然后服务端的onTransact方法会被调用，直至RPC过程返回后，当前线程继续执行，并从\_reply中取出RPC过程的返回结果，最后返回\_reply中的数据

   - Proxy#addBook

     > 同上，只是没有返回值

5. linkToDeath和unlinkToDeath死亡代理

## Android中的IPC方式

1. 使用Bundle

2. 使用文件共享：保存成文件，高并发都会存在问题；SharedPreferences实际就是保存成xml文件，除存在高并发问题，在多进程模式下也不可靠。

3. 使用Messenger(底层使用的AIDL)

   - 一次处理一个请求，串行的处理方式，因此在服务端不用考虑线程同步问题

   - 使用步骤：

     - 服务端进程：创建一个Service来处理客户端的连接请求；创建一个Handler并通过它来创建一个Messenger对象；在Service的onBind中返回这个Messenger对象底层的Binder即可。在清单文件注册一下。

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

       

     - 客户端进程：首先绑定服务端的Service；绑定成功后用服务端返回的IBinder对象创建一个Messager，通过这个Messenger就可以向服务器发送消息了，发送类型必须为Message对象； 如果需要服务端能够回应客户端，在客户端需要创建一个Handler并创建一个新的Messenger，并把这个Messenger对象通过Message的replyTo参数传递给服务端，服务端通过这个replayTo参数就可以回应客户端

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

   - 服务端：创建一个Service用来监听客户端的连接请求，然后创建一个AIDL文件，将暴露给客户端的接口在AIDL文件中声明，最后在Service中实现这个AIDL接口。

   - 客户端：绑定服务器的Service，绑定成功后，将服务端返回的Binder对象转成AIDL接口所属的类型，接着就可以调用AIDL中方法。

   - AIDL接口的创建：AIDL的包结构在服务端和客户端要保持一致

     ```java
     package com.example.inspeeding_yf006.learn.bean;
     
     import com.example.inspeeding_yf006.learn.bean.BookBean;
     
     interface IBookManager {
         List<BookBean> getBookList();
         void addBook(in BookBean bookBean);
     }
     ```

     ```text
     AIDL支持的数据类型：
     1. 基本数据类型（int,long,char,boolean,double等）
     2. String和CharSequence
     3. List:只支持ArrayList，里面每个元素都必须能够被AIDL支持
     4. Map：只支持HashMap,里面每个元素都必须被AIDL支持，包括key和value
     5. Parcelable:所有实现了Parcelable接口的对象
     6. AIDL：所有的AIDL接口本身也可以在AIDL文件中使用
     AIDL中除了基本数据类型，其他数据类型必须标上方向：in out inout 
     AIDL接口只支持方法，不支持静态变量
     ```

   - 远程服务端Service的实现：

   - 客户端的实现

   - P73

​     
