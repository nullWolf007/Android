# Handle & Message & Looper 异步消息处理

## 大体了解
### handle
```
发送消息，它能把消息发送给Looper管理的MessageQueue
处理消息，它负责处理Looper分给它的消息
```
### Message
```
handle接受和处理的消息对象
```
### Looper
```
每一个线程中只有一个Looper，它负责管理对应的MessageQueue，会不断的在MessageQueue中取出消息，并将消息分给对应的handle进行处理
```

## Handle
### 为什么需要handle
```
当主线程处理一个消息超过5秒，就会出现ANR，所以需要把一些处理时间比较长的消息，放在一个单独的线程中进行处理，把处理以后的结果，
返回给主线程运行，就需要用Handle进行线程间的通信
```
### Handle的作用
**让线程延时执行**   
* final boolean postAtTime(Runnable r, long uptimeMillis)
* final boolean postDelayed(Runnable r, long delayMillis)

**让任务在其他线程中执行，并返回结果**
* 在新启动的线程中发送消息

&emsp;&emsp; ```使用Handler对象的sendMessage()方法或者SendEmptyMessage()方法发送消息。```
* 在主线程中获取处理消息

&emsp;&emsp; ```重写Handler类中处理消息的方法（void handleMessage(Message msg)），当新启动的线程发送消息时，消息发送到与之关联的MessageQueue。而Hanlder不断地从MessageQueue中获取并处理消息。```

### Handle更新UI线程的一般使用
* 首先进行handle声明，复写handleMessage方法(在主线程中)
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

### 如何在子线程中使用Handle(子线程)
1. 调用Looper的prepare()方法为当前非主线程创建Looper对象，创建Looper对象时，他的构造器创建与之配套的MessageQueue
2. 创建Handle子类实例，重写handleMessage()方法，该方法处理来自其他线程的消息
3. 调用Looper的looper()方法来启动Looper
```
使用这个handle实例在任何其他线程中发送消息，最终处理消息的代码都会在你创建的handle实例的线程中运行
```

## 补充
* UI线程就是主线程


参考文章：
* [Android 异步消息处理机制（Handler 、 Looper 、MessageQueue）源码解析](http://blog.csdn.net/amazing7/article/details/51424038)
