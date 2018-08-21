# RxJava2

## 一、定义

- RxJava是一个基于事件流、实现异步操作的库

## 二、作用

- 实现异步操作

## 三、特点

- 基于事件流的脸识调用
- 简洁、简单、优雅

## 四、RxJava原理

|         角色         |            作用             |
| :------------------: | :-------------------------: |
| 被观察者(Observable) |          产生事件           |
|   观察者(Observer)   |  接收事件，并给出响应动作   |
|   订阅(Subscribe)    |    连接 被观察者和观察者    |
|     事件(Event)      | 被观察者和观察者 沟通的载体 |

## 五、基本使用

### 步骤1：创建被观察者和生产事件

```java
Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
// create() 是 RxJava 最基本的创造事件序列的方法
// 此处传入了一个 OnSubscribe 对象参数
// 当 Observable 被订阅时，OnSubscribe 的 call() 方法会自动被调用，即事件序列就会依照设定依次被触发
// 即观察者会依次调用对应事件的复写方法从而响应事件
// 从而实现被观察者调用了观察者的回调方法 & 由被观察者向观察者的事件传递，即观察者模式
	// 2. 在复写的subscribe（）里定义需要发送的事件
	@Override
	public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
    	// 通过 ObservableEmitter类对象产生事件并通知观察者
    	// ObservableEmitter类介绍
    	// a. 定义：事件发射器
    	// b. 作用：定义需要发送的事件 & 向观察者发送事件
        emitter.onNext(1);
        emitter.onNext(2);
        emitter.onNext(3);
        emitter.onComplete();
    }
});
```

```java
//扩展：RxJava 提供了其他方法用于 创建被观察者对象Observable
// 方法1：just(T...)：直接将传入的参数依次发送出来
Observable observable = Observable.just("A", "B", "C");
// 将会依次调用：
// onNext("A");
// onNext("B");
// onNext("C");
// onCompleted();

// 方法2：from(T[]) / from(Iterable<? extends T>) : 将传入的数组 / Iterable 拆分成具体对象后，依次发送出来
String[] words = {"A", "B", "C"};
Observable observable = Observable.from(words);
// 将会依次调用：
// onNext("A");
// onNext("B");
// onNext("C");
// onCompleted();
```

### 步骤2：创建观察者 （`Observer` ）并 定义响应事件的行为

- 方法1：采用Observer 接口

```java
// 1. 创建观察者 （Observer ）对象
Observer<Integer> observer = new Observer<Integer>() {
// 2. 创建对象时通过对应复写对应事件方法 从而 响应对应事件

		// 观察者接收事件前，默认最先调用复写 onSubscribe（）
         @Override
         public void onSubscribe(Disposable d) {
              Log.d(TAG, "开始采用subscribe连接");
         }

         // 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
         @Override
         public void onNext(Integer value) {
             Log.d(TAG, "对Next事件作出响应" + value);
         }

         // 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
         @Override
         public void onError(Throwable e) {
             Log.d(TAG, "对Error事件作出响应");
         }

         // 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
         @Override
         public void onComplete() {
             Log.d(TAG, "对Complete事件作出响应");
         }
};
```

- 方式2：采用Subscriber 抽象类

```java
// 说明：Subscriber类 = RxJava 内置的一个实现了 Observer 的抽象类，对 Observer 接口进行了扩展

// 1. 创建观察者 （Observer ）对象
Subscriber<String> subscriber = new Subscriber<Integer>() {

// 2. 创建对象时通过对应复写对应事件方法 从而 响应对应事件
            // 观察者接收事件前，默认最先调用复写 onSubscribe（）
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "开始采用subscribe连接");
            }

            // 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件作出响应" + value);
            }

            // 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            // 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        };


```

- 区别

  > 特别注意：
  >
  > 相同点：二者基本使用方式完全一致（实质上，在RxJava的 subscribe 过程中，Observer总是会先被转换成Subscriber再使用）
  >
  > 不同点：Subscriber抽象类对 Observer 接口进行了扩展，新增了两个方法： 
  >
  > 1. onStart()：在还未响应事件前调用，用于做一些初始化工作 
  > 2. unsubscribe()：用于取消订阅。在该方法被调用后，观察者将不再接收 & 响应事件 。 调用该方法前，先使用 isUnsubscribed() 判断状态，确定被观察者Observable是否还持有观察者Subscriber的引用，如果引用不能及时释放，就会出现内存泄露 

### 步骤3.通过订阅（Subscribe）连接观察者和被观察者

```java
observable.subscribe(observer);
```

## 六、链式使用

```java
// RxJava的链式操作
Observable.create(new ObservableOnSubscribe<Integer>() {
		// 1. 创建被观察者 & 生产事件
         @Override
         public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
              emitter.onNext(1);
              emitter.onNext(2);
              emitter.onNext(3);
              emitter.onComplete();
         }
}) 
    .subscribeOn(Schedulers.computation())//事件源发射事件在哪个运行,代表CPU计算密集型的操作
    .observeOn(AndroidSchedulers.mainThread())//订阅者在哪个线程中接受事件，主线程
    .subscribe(new Consumer<String>() {
         // 每次接收到Observable的事件都会调用Consumer.accept（）
         @Override
         public void accept(String s) throws Exception {
             System.out.println(s);
         }
    });
}}
```





