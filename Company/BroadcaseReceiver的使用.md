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


### 参考文章
* [Android四大组件：BroadcastReceiver史上最全面解析](http://www.jianshu.com/p/ca3d87a4cdf3)
