[TOC]

# OkHttp3原理（一）

## 1.常用方法

```java
 OkHttpClient okHttpClient =  new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS) .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) 
                    {
                        //...
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        //...
                        return null;
                    }
                }).build();
        Request request = new Request.Builder().url("www.baidu.com").build();
        Call call = okHttpClient.newCall(request);
        //异步
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
        //同步
        try {
            Response response =  call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
```



## 2.源码

### 2.1 注释

```java
/**
 * Factory for {@linkplain Call calls}, which can be used to send HTTP requests and read their
 * responses.
 *
 * <h3>OkHttpClients should be shared</h3>
 *
 * <p>OkHttp performs best when you create a single {@code OkHttpClient} instance and reuse it for
 * all of your HTTP calls. This is because each client holds its own connection pool and thread
 * pools. Reusing connections and threads reduces latency and saves memory. Conversely, creating a
 * client for each request wastes resources on idle pools.
 *
 * <p>Use {@code new OkHttpClient()} to create a shared instance with the default settings:
 * <pre>   {@code
 *
 *   // The singleton HTTP client.
 *   public final OkHttpClient client = new OkHttpClient();
 * }</pre>
 *
 * <p>Or use {@code new OkHttpClient.Builder()} to create a shared instance with custom settings:
 * <pre>   {@code
 *
 *   // The singleton HTTP client.
 *   public final OkHttpClient client = new OkHttpClient.Builder()
 *       .addInterceptor(new HttpLoggingInterceptor())
 *       .cache(new Cache(cacheDir, cacheSize))
 *       .build();
 * }</pre>
 */
//发送http请求和读取返回数据的一个执行调用请求的工厂类
//最好创建一个单例OkHttpClient实例，重复使用它。因为每个Client都有它自己的连接池connection pool和线程池thread pool，重用这些连接池和线程池可以有效的减少延迟和节约内存
```

### 2.2 builder源码 属性

```java
public static final class Builder {
    Dispatcher dispatcher; //调度器，里面包含了线程池和三个队列（readyAsyncCalls：保存等待执行的异步请求
    
    Proxy proxy; //代理类，默认有三种代理模式DIRECT(直连),HTTP（http代理）,SOCKS（socks代理），这三种模式
    
    List<Protocol> protocols; //协议集合，协议类，用来表示使用的协议版本，比如`http/1.0,`http/1.1,`spdy/3.1,`h2等
    
    List<ConnectionSpec> connectionSpecs; //连接规范，用于配置Socket连接层。对于HTTPS，还能配置安全传输层协议（TLS）版本和密码套件
    
    final List<Interceptor> interceptors = new ArrayList<>(); //拦截器，用来监听请求
    final List<Interceptor> networkInterceptors = new ArrayList<>();
    
    ProxySelector proxySelector; //代理选择类，默认不使用代理，即使用直连方式，当然，我们可以自定义配置，以指定URI使用某种代理，类似代理软件的PAC功能。
    
    CookieJar cookieJar; //Cookie的保存获取
    
    Cache cache; //缓存类，内部使用了DiskLruCache来进行管理缓存，匹配缓存的机制不仅仅是根据url，而且会根据请求方法和请求头来验证是否可以响应缓存。此外，仅支持GET请求的缓存。
    
    InternalCache internalCache;  //内置缓存
    
    SocketFactory socketFactory; //Socket的抽象创建工厂，通过`createSocket来创建Socket
    。
    SSLSocketFactory sslSocketFactory; //安全套接层工厂，HTTPS相关，用于创建SSLSocket。一般配置HTTPS证书信任问题都需要从这里着手。对于不受信任的证书一般会提示javax.net.ssl.SSLHandshakeException异常。
    
    CertificateChainCleaner certificateChainCleaner; //证书链清洁器，HTTPS相关，用于从[Java]的TLS API构建的原始数组中统计有效的证书链，然后清除跟TLS握手不相关的证书，提取可信任的证书以便可以受益于证书锁机制。
    
    HostnameVerifier hostnameVerifier; //主机名验证器，与HTTPS中的SSL相关，当握手时如果URL的主机名不是可识别的主机，就会要求进行主机名验证
    
    CertificatePinner certificatePinner; // 证书锁，HTTPS相关，用于约束哪些证书可以被信任，可以防止一些已知或未知的中间证书机构带来的攻击行为。如果所有证书都不被信任将抛出SSLPeerUnverifiedException异常。
    
    Authenticator proxyAuthenticator; //身份认证器，当连接提示未授权时，可以通过重新设置请求头来响应一个新的Request。状态码401表示远程服务器请求授权，407表示代理服务器请求授权。该认证器在需要时会被RetryAndFollowUpInterceptor触发。
    
    Authenticator authenticator;
    ConnectionPool connectionPool; //连接池
    Dns dns;
    boolean followSslRedirects; //是否遵循SSL重定向
    boolean followRedirects; //是否重定向
    boolean retryOnConnectionFailure; //失败是否重新连接
    int connectTimeout; //连接超时
    int readTimeout; //读取超时
    int writeTimeout; //写入超时
    ...
}
```

### 2.3 builder构造器--默认

```java
public Builder() {
      dispatcher = new Dispatcher();
      protocols = DEFAULT_PROTOCOLS; //默认支持的协议
      connectionSpecs = DEFAULT_CONNECTION_SPECS; //默认的连接规范
      proxySelector = ProxySelector.getDefault(); //默认的代理选择器，直连
      cookieJar = CookieJar.NO_COOKIES; //默认不进行管理Cookie
      socketFactory = SocketFactory.getDefault();
      hostnameVerifier = OkHostnameVerifier.INSTANCE; //主机验证
      certificatePinner = CertificatePinner.DEFAULT; //证书锁，默认不开启
      proxyAuthenticator = Authenticator.NONE; //默认不进行授权
      authenticator = Authenticator.NONE;
      connectionPool = new ConnectionPool(); //连接池
      dns = Dns.SYSTEM;
      followSslRedirects = true;
      followRedirects = true;
      retryOnConnectionFailure = true;
      //超时时间
      connectTimeout = 10_000;
      readTimeout = 10_000;
      writeTimeout = 10_000;
}
```

### 2.4 Dispatcher

```java
/**
 * Policy on when async requests are executed.
 *
 * <p>Each dispatcher uses an {@link ExecutorService} to run calls internally. If you supply your
 * own executor, it should be able to run {@linkplain #getMaxRequests the configured maximum} number
 * of calls concurrently.
 */
/**
* 它是一个异步请求执行政策。
* 当我们用OkHttpClient.newCall(request)进行execute/enenqueue时，实际是将请求Call放到了Dispatcher中，
okhttp使用Dispatcher进行线程分发
* 它有两种方法，一个是普通的同步单线程；另一种是使用了队列进行并发任务的分发(Dispatch)与回调。
* 另外，在Dispatcher中每一个请求都是使用 ExecutorService 来执行的。
*/
public final class Dispatcher {
  private int maxRequests = 64;//最大并发数为64，同时请求
  private int maxRequestsPerHost = 5;//每个主机的最大请求数为5
  private @Nullable Runnable idleCallback;//闲置接口

  /** Executes calls. Created lazily. */
  private @Nullable ExecutorService executorService;//线程池

  /** Ready async calls in the order they'll be run. */
  //缓存好的异步调用，都是放在队列里保存
  private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque<>();

  /** Running asynchronous calls. Includes canceled calls that haven't finished yet. */
  //运行中的异步调用，都是放在队列里保存
  private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>();

  /** Running synchronous calls. Includes canceled calls that haven't finished yet. */
  //运行中的同步调用，都是放在队列里保存
  private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>();
  ...
}
```

前面的okHttpClient.newCall(request)将请求request构造成Call，进行发起请求

```java
 @Override public Call newCall(Request request) {
    return RealCall.newRealCall(this, request, false /* for web socket */);//创建一个RealCall对象
 }
```

### 2.5 Call

```java
/**
 * A call is a request that has been prepared for execution. A call can be canceled. As this object
 * represents a single request/response pair (stream), it cannot be executed twice.
 */
/**
* 一个定义了各种Http连接请求方法的接口
*/
public interface Call extends Cloneable {
  /** Returns the original request that initiated this call. */
  Request request();
    
  /**
   * Invokes the request immediately, and blocks until the response can be processed or is in
   * error.
   *
   * <p>To avoid leaking resources callers should close the {@link Response} which in turn will
   * close the underlying {@link ResponseBody}.
   *
   * <pre>@{code
   *
   *   // ensure the response (and underlying response body) is closed
   *   try (Response response = client.newCall(request).execute()) {
   *     ...
   *   }
   *
   * }</pre>
   *
   * <p>The caller may read the response body with the response's {@link Response#body} method. To
   * avoid leaking resources callers must {@linkplain ResponseBody close the response body} or the
   * Response.
   *
   * <p>Note that transport-layer success (receiving a HTTP response code, headers and body) does
   * not necessarily indicate application-layer success: {@code response} may still indicate an
   * unhappy HTTP response code like 404 or 500.
   *
   * @throws IOException if the request could not be executed due to cancellation, a connectivity
   * problem or timeout. Because networks can fail during an exchange, it is possible that the
   * remote server accepted the request before the failure.
   * @throws IllegalStateException when the call has already been executed.
   */
  Response execute() throws IOException;

  /**
   * Schedules the request to be executed at some point in the future.
   *
   * <p>The {@link OkHttpClient#dispatcher dispatcher} defines when the request will run: usually
   * immediately unless there are several other requests currently being executed.
   *
   * <p>This client will later call back {@code responseCallback} with either an HTTP response or a
   * failure exception.
   *
   * @throws IllegalStateException when the call has already been executed.
   */
  void enqueue(Callback responseCallback);

  /** Cancels the request, if possible. Requests that are already complete cannot be canceled. */
  void cancel();

  /**
   * Returns true if this call has been either {@linkplain #execute() executed} or {@linkplain
   * #enqueue(Callback) enqueued}. It is an error to execute a call more than once.
   */
  boolean isExecuted();

  boolean isCanceled();

  /**
   * Create a new, identical call to this one which can be enqueued or executed even if this call
   * has already been.
   */
  Call clone();

  interface Factory {
    Call newCall(Request request);
  }
}

```

可以通过request()方法获取自己的请求体，调用enqueue发起异步请求，调用execute发起同步请求

### 2.6 RealCall---Call的实现类

```java
final class RealCall implements Call {
    ...
    @Override public Response execute() throws IOException {
        
    //检查这个Call是否已经被执行，每个Call只能执行一次，否则会抛出IllegalStateException异常。
    //如果想要完全一样的Call，可以使用call.clone方法进行克隆
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    //跟踪调用栈的信息
    captureCallStackTrace();
    //EventListener 用来触发事件 接收非常细的时间回调
    eventListener.callStart(this);
    try {
      //实际执行 同步的话和dispatcher有关
      client.dispatcher().executed(this);
      //获取http返回结果 同时会进行一系列的“拦截”操作
      Response result = getResponseWithInterceptorChain();
      if (result == null) throw new IOException("Canceled");
      return result;
    } catch (IOException e) {
      //失败的回调
      eventListener.callFailed(this, e);
      throw e;
    } finally {
      //执行完毕
      client.dispatcher().finished(this);
    }
    }
    
    @Override public void enqueue(Callback responseCallback) {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    captureCallStackTrace();
    eventListener.callStart(this);
    client.dispatcher().enqueue(new AsyncCall(responseCallback));
    }
    ...
}
```

RealCall中实现了execute和enqueue等方法，而在RealCall的execute和enqueue方法中都调用了dispatcher.enqueue/execute



## 参考资料

* [**OkHttp解析(一)从用法看清原理**](https://www.jianshu.com/p/7b29b89cd7b5)