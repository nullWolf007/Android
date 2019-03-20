[TOC]



# OkHttp3原理

## 源码

### 注释

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

### builder源码

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



## 参考资料

* ![**OkHttp解析(一)从用法看清原理**](https://www.jianshu.com/p/7b29b89cd7b5)