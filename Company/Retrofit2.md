# Retrofit2

## 前言

- Retrofi是一个RESTful的HTTP网络请求框架的封装
- Retrofit时基于OkHttp的，网络请求的工作本质时OkHttp完成的，而Retrofit仅负责网络请求接口的封装

## 使用步骤

### 步骤1：添加Retrofit库依赖

- 在build.gradle中加入Retrofit库依赖

  ```java
  dependencies { 
  	implementation 'com.squareup.retrofit2:retrofit:2.4.0'
  }
  ```

- 添加网络权限

  ```xml
  <uses-permission android:name="android.permission.INTERNET"/>
  ```

### 步骤2：创建接收服务器返回数据的类

### 步骤3：创建用于描述网络请求的接口

- 采用 注解 描述网络请求参数和配置网络请求参数

- 注解类型

  1. 网络请求方法

     （1）@GET：

     （2）@POST：

     （3）@PUT：

     （4）@DELETE：

     （5）@PATH：

     （6）@HEAD：

     （7）@HTTP：用来替换上面的，可以设置method、path、hsaBody等属性

     - 网络请求完整Url = baseUrl() + 接口中 注解 的值

  2. 标记类

     （1）@FormUrlEncoded：表示请求体是一个Form表单；每个键值对需要用@Filed注解键名，随后提供值

     （2）@Multipart：表示请求体是一个支持文件上传的Form表单；每个键值对需要用@Part来注解键名，随后提供值

     （3）@Streaming：表示返回的数据以流的形式返回，适用于返回数据较大的场景（如果没有该注释，默认把数据全部载入内存；之后获取数据也是从内存中读取）

  3. 网络请求参数

     （1）@Headers：添加请求头，作用于网络接口

     （2）@Header：添加不固定值的Header，作用于接口的参数

     （3）@Body：用于非表单请求体，以Post方式

     （4）@Field和@FieldMap：向Post表单传入键值对，与@FormUrlEncoded配合使用

     （5）@Part和@PartMap：用于表单字段；适用于文件上传的情况，与@Multipart配合使用

     （6）@Query和@QueryMap：用于表单字段：功能和@Field和@FieldMap类似（区别时@Query和@QueryMap的数据体现在URL上，@Field与@FieldMap的数据体现在请求体上；但生成的数据是一致的）

     （7）@Path：URL缺省值

     （8）@URL：URL设置

### 步骤4：创建Retrofit实例

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("http://fanyi.youdao.com/") //设置网络请求的Url地址
    .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器 		  
    .addCallAdapterFactory(RxJavaCallAdapterFactory.create()); // 支持RxJava平台 
	.build(); 
```

### 步骤5：创建网络请求接口实例并配置网络请求参数

### 步骤6：发送网络请求（异步/同步）

### 步骤7：处理服务器返回的数据





### 参考文章

- [Android：手把手带你 深入读懂 Retrofit 2.0 源码](https://www.jianshu.com/p/0c055ad46b6c)
