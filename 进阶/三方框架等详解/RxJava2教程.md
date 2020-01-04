[TOC]

# RxJava2教程

### 参考文章

* [**这可能是最好的RxJava 2.x 教程（完结版）**](https://www.jianshu.com/p/0cd258eecf60)
* [**RxJava2 系列 -1：一篇的比较全面的 RxJava2 方法总结**](https://www.jianshu.com/p/823252f110b0)
* [RxJava官方wiki](https://github.com/ReactiveX/RxJava/wiki/)

## 一、前言

### 1.1 简介

* RxJava是一个在Java VM上使用可观测的序列来组成**异步**的、基于事件的程序的库。

### 1.2 RxJava和AsyncTask区别

* 在Android中，我们可以使用AsyncTask来完成异步任务操作，但是当任务的梳理比较多的时候，我们要为每个任务定义一个AsyncTask就变得非常繁琐。 RxJava能帮助我们在实现异步执行的前提下保持代码的清晰。
  它的原理就是创建一个`Observable`来完成异步任务，组合使用各种不同的链式操作，来实现各种复杂的操作，最终将任务的执行结果发射给`Observer`进行处理。

### 1.3 特点

- 简洁、简单、优雅
- 比Handler,AsyncTask灵活

## 二、解析

### 2.1 常用基础类

#### 2.1.1 Observable

* 被观察者：产生事件

* 多个流，无背压

#### 2.1.2 Flowable

* 被观察者：产生事件

* 多个流，响应式流和背压

#### 2.1.3 Event

* 事件：观察者，被观察者 沟通的载体

#### 2.1.4 Observer

* 观察者：接收事件

#### 2.1.5 Subscriber

* 观察者：接收事件

#### 2.1.6 Subscribe

* 订阅：连接 被观察者和观察者

#### 2.1.7 Disposable

* isDisposed()：该方法用来判断否停止了观察指定的流
* dispose()：该方法用来放弃观察指定的流
* 一般在Activity的onDestory中需要调用dispose方法，防止内存泄漏

#### 2.1.8 Single

* 只有一个元素或者错误的流

#### 2.1.9 Completable

* 没有任何元素，只有一个完成和错误信号的流

#### 2.1.10 Maybe

* 没有任何元素或者只有一个元素或者只有一个错误的流

### 2.2 背压(backpressure)

#### 2.2.1 含义

* 背压是流速控制的一种策略
* 背压是指在异步场景中，被观察者发送事件速度远快于观察者的处理速度的情况下，一种告诉上游的被观察者降低发送速度的策略。

### 2.3 两种观察者模式图解

![**RxJava两种观察者模式**](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/%E4%B8%89%E6%96%B9%E6%A1%86%E6%9E%B6/RxJava%E4%B8%A4%E7%A7%8D%E8%A7%82%E5%AF%9F%E8%80%85%E6%A8%A1%E5%BC%8F.png)

## 三、API说明

### 3.1 Observable

#### 3.1.1 interval和intervalRange

* interval：表示每隔3秒发送一个整数，整数从0开始

  ```java
  Observable.interval(3, TimeUnit.SECONDS).subscribe(System.out::println);
  ```

* intervalRange：表示发送第一个整数停顿1秒，每隔3秒。发送一个整数。整数从2开始，发送10个数字，每次加1

  ```java
  Observable.intervalRange(2, 10, 1, 3, TimeUnit.SECONDS).subscribe(System.out::println);
  ```

#### 3.1.2 range和rangeLong

* range：整数从5开始，发送10个数字，每次加1。

  ```java
  Observable.range(5, 10).subscribe(i -> System.out.println("test" + i + ""));
  ```

* rangeLong：整数从5开始，发送10个数字，每次加1。区别是long类型的。

  ```java
  Observable.rangeLong(5, 10).subscribe(i -> System.out.println("test" + i + ""));
  ```

#### 3.1.3 create

* 创建Observable对象，需要传入发射器ObservableEmmiter

* 通用写法

  ```java
  //输出 test1 test2
  Observable.create(new ObservableOnSubscribe<Integer>() {
      @Override
      public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
      	emitter.onNext(1);
          emitter.onNext(2);
          emitter.onComplete();
  	}
  }).subscribe(new Consumer<Integer>() {
  	// 每次接收到Observable的事件都会调用Consumer.accept（）
      @Override
      public void accept(Integer i) throws Exception {
      	System.out.println("test" + i);
  	}
  });
  
  //输出 test:onSubscribe test1 test2 test:onComplete
  Observable.create(new ObservableOnSubscribe<Integer>() {
  	@Override
      public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
      	emitter.onNext(1);
          emitter.onNext(2);
          emitter.onComplete();
  	}
  }).subscribe(new Observer<Integer>() {
  	@Override
      public void onSubscribe(Disposable d) {
      	System.out.println("test:onSubscribe");
  	}
  
      @Override
      public void onNext(Integer integer) {
      	System.out.println("test" + integer);
  	}
  
      @Override
      public void onError(Throwable e) {
  	}
  
      @Override
      public void onComplete() {
      	System.out.println("test:onComplete");
  	}
  });
  ```

* lambda写法

  ```java
  //输出 test4 test5
  Observable.create(emitter -> {
  	emitter.onNext(4);
      emitter.onNext(5);
      emitter.onComplete();
  }).subscribe(i -> System.out.println("test" + i));
  ```

## 三、基本使用

### 1.1 创建被观察者和生产事件

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

### 1.2 创建观察者 （`Observer` ）并 定义响应事件的行为

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

### 1.3 通过订阅（Subscribe）连接观察者和被观察者

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





