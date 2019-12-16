# Android线程详解

## 常见问题注意点
* 在子线程中不能进行UI操作

## 四种不同形式的线程
* Thread
* AsyncTask
* HandleThread
* IntentService

## 线程的基本用法
1. extends Thread
```java
     class MyThread extends Thread{
        @Override
        public void run() {
            //处理具体的逻辑
            super.run();
        }
    }
    
    //开启线程
    new MyThread().start();
```
2. implements Runnable
```java
    //分开写
    class MyThread implements Runnable{
        @Override
        public void run() {
            //处理具体的逻辑
        }
    }
    
    //开启线程
    MyThread myThread = new MyThread();
    new Thread(myThread).start();
```
```java
    //合在一起写
    new Thread(new Runnable() {
         @Override
         public void run() {
            //处理具体的逻辑     
         }
     }).start();
```

## 异步消息处理
### 使用方法
```java
       final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        //在这里面进行UI操作
                        break;
                    default:
                        break;
                }
            }
        };


        //在某种情况下开启线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();
```
### 异步消息处理机制
1. Message
* Message是在线程之间传递的消息，可以在内部携带少量的信息，例如上面的message.what = 1。也可以携带整型数据和Object对象
2. Handle
* Handle是处理者的意思，主要用于发送和处理消息的。发送消息使用sendMessage()，发出的消息会传递到Handle的handleMessage()方法中。
3. MessageQueue
* MessageQueue是消息队列的意思，用来存放所有通过Handle发送的消息，这部分消息会一直存在消息队列中，等待被处理。每个线程中只有一个MessageQueue。
4. Looper
* Looper是每个线程中MessageQueue的管家，调用Looper的loop()方法后，就会进入一个无限循环，然后每当发现MessageQueue中存在一条消息，就会把它取出，并传递到handle的handleMessage()方法中。每个线程中只能有一个MessageQueue对象


## AsyncTask
### 概述
* AsyncTask是一种轻量级异步任务类，封装了Thread和Handle。可以方便的执行后台任务，并且能够在主线程中对返回结果进行更新UI
### AsyncTask是一个抽象的泛型类
```java
public abstract class AsyncTask<Params, Progress, Result>
//Params表示参数的类型
//Progress表示后台任务执行进度的类型
//Result表示后台任务的返回结果的类型
```
### 核心方法
* onPreExecute()
```java
在主线程中执行，在异步任务执行前，调用这个方法，一般用于做一些准备工作
```
* doInBackground(Params...params)
```java
此方法用于执行异步任务。params参数表示异步任务的输入参数。需要返回计算结果给onPostExecute()方法。
这个方法通过publishProgress方法来更新任务的进度，publishProgress方法会调用onProgressUpdate方法
```
* onProgressUpdate(Progress...values)
```java
在主线程中执行，当后台任务的执行进度发生改变时，此方法会被调用
```
* onPostExecute(Result result)
```java
在主线程中执行，在异步任务执行之后，此方法被调用，其中result参数时后台任务的返回值，即doInBackground的返回值
```
### 执行
* 通过execute()方法来调用，传入的参数可以在doInBackground中获取传过去的参数

## 注意点
* AsyncTask的对象必须在主线程中创建
* execute方法必须在UI线程中调用



