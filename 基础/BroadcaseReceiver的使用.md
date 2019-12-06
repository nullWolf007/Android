# BroadcaseReceiver解析

## 前言
* 广播分为：广播发送者，广播接收者
* 作用：用于监听/接受 应用发出的广播消息，并做出响应
* 应用场景：a.不同组件之间通信(包括应用内/不同应用之间) b.与android系统在特定情况下的通信 c.多线程通信
* 模式：观察者模式（基于消息的发布/订阅事件模型）

## 基本使用步骤
1. 自定义广播接收者BroadcastReceiver(继承BroadcastReceiver基类，复写抽象方法onReceive()方法)
* 广播接收器收到相应的广播后，会自动回掉onReceive()方法
* 一般情况下，onReceive方法会涉及与其他组件之间的交互，如发送Notification、启动service等
* 默认情况下，广播接收器运行在UI线程中，因此onReceive方法不能执行耗时操作，否则会导致ANR
```java
public class mBroadcastReceiver extends BroadcastReceiver {

  //接收到广播后自动调用该方法
  @Override
  public void onReceive(Context context, Intent intent) {
   //写入接收广播后的操作
    }
}
```
2. 广播接收器注册(静态注册和动态注册)
* 静态注册(略)：在AndroidManifest.xml里通过&lt;receive&gt;标签声明
* 动态注册：在代码中通过调用Context的registerReceiver()方法进行动态注册BroadcastReveiver
```java
@Override
  protected void onResume(){
      super.onResume();
      
    //实例化BroadcastReceiver子类 &  IntentFilter
     mBroadcastReceiver mBroadcastReceiver = new mBroadcastReceiver();
     IntentFilter intentFilter = new IntentFilter();

    //设置接收广播的类型
     intentFilter.addAction(android.net.conn.CONNECTIVITY_CHANGE);

    //调用Context的registerReceiver（）方法进行动态注册
     registerReceiver(mBroadcastReceiver, intentFilter);
 }


//注册广播后，要在相应位置记得销毁广播
//即在onPause() 中unregisterReceiver(mBroadcastReceiver)
//当此Activity实例化时，会动态将MyBroadcastReceiver注册到系统中
//当此Activity销毁时，动态注册的MyBroadcastReceiver将不再接收到相应的广播。
 @Override
 protected void onPause() {
     super.onPause();
      //销毁在onResume()方法中的广播
     unregisterReceiver(mBroadcastReceiver);
     }
}
```
动态广播最好在Activity的onResume()注册、onPause()注销。

### 参考文章
* [Android四大组件：BroadcastReceiver史上最全面解析](http://www.jianshu.com/p/ca3d87a4cdf3)
