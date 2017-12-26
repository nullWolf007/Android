# Android线程详解

## 四种不同形式的线程
* Thread
* AsyncTask
* HandleThread
* IntentService

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
* onPreExecute()：在主线程中执行，在异步任务执行前，调用这个方法，一般用于做一些准备工作
* doInBackground(Params...params)：此方法用于执行异步任务。params参数表示异步任务的输入参数。需要返回计算结果给onPostExecute()方法。这个方法通过publishProgress方法来更新任务的进度，publishProgress方法会调用onProgressUpdate方法
* onProgressUpdate(Progress...values)：在主线程中执行，当后台任务的执行进度发生改变时，此方法会被调用
* onPostExecute(Result result)：在主线程中执行，在异步任务执行之后，此方法被调用，其中result参数时后台任务的返回值，即doInBackground的返回值



