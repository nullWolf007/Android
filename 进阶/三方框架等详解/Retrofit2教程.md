[TOC]
- <!-- TOC -->
- [ Retrofit2教程](#Retrofit2教程)
  - [ 参考链接](#参考链接)
  - [ 一、前言](#一前言)
    - [ 1.1 简介](#11-简介)
    - [ 1.2 与其他网络请求开源库比较](#12-与其他网络请求开源库比较)
  - [ 二、使用步骤](#二使用步骤)
    - [ 步骤1：添加Retrofit库依赖](#步骤1添加Retrofit库依赖)
    - [ 步骤2：创建接收服务器返回数据的类](#步骤2创建接收服务器返回数据的类)
    - [ 步骤3：创建用于描述网络请求的接口](#步骤3创建用于描述网络请求的接口)
      - [ 3.3.1 添加路径类](#331-添加路径类)
      - [ 3.3.2 添加接口](#332-添加接口)
        - [ 说明](#说明)
        - [ 注解类型](#注解类型)
    - [ 步骤4：创建Retrofit实例](#步骤4创建Retrofit实例)
    - [ 步骤5：创建网络请求接口实例并配置网络请求参数](#步骤5创建网络请求接口实例并配置网络请求参数)
    - [ 步骤6：发送网络请求（异步/同步）](#步骤6发送网络请求异步同步)
    - [ 步骤7：处理服务器返回的数据](#步骤7处理服务器返回的数据)
  <!-- /TOC -->
# Retrofit2教程

### 参考链接

* [Android：手把手带你 深入读懂 Retrofit 2.0 源码](https://www.jianshu.com/p/0c055ad46b6c)
* [**Android Retrofit 2.0 的详细 使用攻略（含实例讲解）**](https://www.jianshu.com/p/a3e162261ab6)

## 一、前言

### 1.1 简介

![**Retrofit2简介图解**](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/%E4%B8%89%E6%96%B9%E6%A1%86%E6%9E%B6/Retrofit2%E7%AE%80%E4%BB%8B%E5%9B%BE%E8%A7%A3.png)

- 准确来说：Retrofit是一个RESTful的HTTP网络请求框架的封装
- 原因：网络请求的工作本质时OkHttp完成的，而Retrofit仅负责网络请求接口的封装

![Retrofit2本质过程](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/%E4%B8%89%E6%96%B9%E6%A1%86%E6%9E%B6/Retrofit2%E6%9C%AC%E8%B4%A8%E8%BF%87%E7%A8%8B.png)

* App应用程序通过 Retrofit 请求网络，实际上是使用 Retrofit 接口层封装请求参数、Header、Url 等信息，之后由 OkHttp 完成后续的请求操作

* 在服务端返回数据之后，OkHttp 将原始的结果交给 Retrofit，Retrofit根据用户的需求对结果进行解析

### 1.2 与其他网络请求开源库比较

![网络请求库比较](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/%E4%B8%89%E6%96%B9%E6%A1%86%E6%9E%B6/%E7%BD%91%E7%BB%9C%E8%AF%B7%E6%B1%82%E5%BA%93%E6%AF%94%E8%BE%83.png)

## 二、使用步骤

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

```java
public class ResultBean {
    private int code;
    private String data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
```

### 步骤3：创建用于描述网络请求的接口

#### 3.3.1 添加路径类

```java
public class NetUrl {
    public static final String TEST_PATH = "/test";
}
```

#### 3.3.2 添加接口

```java
public interface Api {
    @GET(NetUrl.TEST_PATH)
    Call<ResultBean> getTest(@Header("token") String token);
}
```

##### 说明

* 采用 注解 描述网络请求参数和配置网络请求参数

##### 注解类型

* **网络请求方法**

  * @GET：对应网络请求get方法

  * @POST：对应网络请求post方法

  * @PUT：对应网络请求put方法

  * @DELETE：对应网络请求delete方法

  * @PATH：

  * @HEAD：

  * @HTTP：用来替换上面的，可以设置method、path、hsaBody等属性

  * **网络请求完整Url = baseUrl() + 接口中 注解 的值**

* **标记类**

  * @FormUrlEncoded：表示请求体是一个Form表单；每个键值对需要用@Filed注解键名，随后提供值

  * @Multipart：表示请求体是一个支持文件上传的Form表单；每个键值对需要用@Part来注解键名，随后提供值

  * @Streaming：表示返回的数据以流的形式返回，适用于返回数据较大的场景（如果没有该注释，默认把数据全部载入内存；之后获取数据也是从内存中读取）

* **网络请求参数**

  * @Headers：添加请求头，作用于网络接口

  * @Header：添加不固定值的Header，作用于接口的参数

  * @Body：用于非表单请求体，以Post方式

  * @Field和@FieldMap：向Post表单传入键值对，与@FormUrlEncoded配合使用

    ```java
    /**
    * Map的key作为表单的键
    */
    @POST("/form")
    @FormUrlEncoded
    Call<ResultBean> test(@FieldMap Map<String, Object> map);
    
     // @FieldMap
    Map<String, Object> map = new HashMap<>();
    map.put("username", "Carson");
    map.put("age", 24);
    Call<ResultBean> call = service.test(map);
    ```

  * @Part和@PartMap：用于表单字段；适用于文件上传的情况，与@Multipart配合使用

  * @Query和@QueryMap：用于表单字段：功能和@Field和@FieldMap类似（区别是@Query和@QueryMap的数据体现在URL上，@Field与@FieldMap的数据体现在请求体上；但生成的数据是一致的）

  * @Path：URL缺省值

    ```java
    @GET("users/{user}/repos")
    Call<ResponseBody> getBlog（@Path("user") String user ）;
    // 访问的API是：https://api.github.com/users/{user}/repos
    // 在发起请求时， {user} 会被替换为方法的第一个参数 user（被@Path注解作用）
    ```

  * @URL：URL设置

### 步骤4：创建Retrofit实例

```java
//需要添加依赖
//implementation 'com.squareup.retrofit2:adapter-rxjava:2.0.2'
//implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("http://nullWolf.study.com/") //设置网络请求的Url地址
    .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器 		  
    .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 支持RxJava平台 
	.build(); 
```

### 步骤5：创建网络请求接口实例并配置网络请求参数

```java
Api api = retrofit.create(Api.class);
Call<ResultBean> call = api.getTest("111");
```

### 步骤6：发送网络请求（异步/同步）

```java
call.enqueue(new Callback<ResultBean>() {
	@Override
    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
    	// 对返回数据进行处理
	}

    @Override
    public void onFailure(Call<ResultBean> call, Throwable t) {
    	//请求失败
	}
});
```

### 步骤7：处理服务器返回的数据


