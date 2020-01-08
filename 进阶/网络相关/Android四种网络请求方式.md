# Android四种网络请求方式

### 参考链接

## 一、前言

### 1.1 四种常用的网络请求方式

#### 1.1.1 HttpClient

* java开发用HttpClient，由于在android 2.2及以下版本中HttpUrlConnection存在着一些bug，所以建议在android 2.3以后使用HttpUrlConnection，2.3之前使用HttpClient。
* 现在市面上已经没有Android2.3的手机了，所以HttpClient不推荐使用。并且Android6.0之后，HttpClient的功能已经移除了，所以更加不推荐。

#### 1.1.2 HttpUrlConnection

* android 2.3以后官方推荐Android开发用HttpUrlConnection，它的API简单，体积较小，因而非常适用于Android项目

#### 1.1.3 Volley

* Volley由谷歌开发，是一个简化网络任务的库。他负责处理请求，加载，缓存，线程，同步等问题。它可以处理JSON，图片，缓存，文本源，支持一定程度的自定义。Volley在Android 2.3及以上版本，使用的是HttpURLConnection，而在Android 2.2及以下版本，使用的是HttpClient。不过再怎么封装Volley在功能拓展性上始终无法与OkHttp相比。

#### 1.1.4 OkHttp

* OkHttp是一款优秀的HTTP框架，它支持get请求和post请求，支持基于Http的文件上传和下载，支持加载图片，支持下载文件透明的GZIP压缩，支持响应缓存避免重复的网络请求，支持使用连接池来降低响应延迟问题。Android 2.3以上支持OkHttp，Android4.4开始HttpURLConnection的底层实现采用的是okHttp，已经集成在Android sdk中。
* 推荐使用

