# Android线程详解

## 四种不同形式的线程
* Thread
* AsyncTask
* HandleThread
* IntentService

## AsyncTask
### 概述：AsyncTask是一种轻量级异步任务类，封装了Thread和Handle。可以方便的执行后台任务，并且能够在主线程中对返回结果进行更新UI
### AsyncTask是一个抽象的泛型类
```java
public abstract class AsyncTask<Params, Progress, Result>
//Params表示参数的类型
//Progress表示后台任务执行进度的类型
//Result表示后台任务的返回结果的类型
```
### 核心方法



