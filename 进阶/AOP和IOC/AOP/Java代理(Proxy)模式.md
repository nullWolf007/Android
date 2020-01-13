[TOC]
- <!-- TOC -->
- [ Java代理(Proxy)模式](#Java代理Proxy模式)
  - [ 参考链接](#参考链接)
  - [ 一、前言](#一前言)
    - [ 1.1 概述](#11-概述)
    - [ 1.2 三种代理](#12-三种代理)
      - [ 1.2.1 静态代理](#121-静态代理)
      - [ 1.2.2 JDK动态代理](#122-JDK动态代理)
      - [ 1.2.3 Cglib代理](#123-Cglib代理)
  - [ 二、实例](#二实例)
    - [ 2.1 静态代理](#21-静态代理)
      - [ 2.1.1 实例](#211-实例)
      - [ 2.1.2 说明](#212-说明)
    - [ 2.2 JDK动态代理](#22-JDK动态代理)
      - [ 2.2.1 特点](#221-特点)
      - [ 2.2.2 实例](#222-实例)
    - [ 2.3 Cglib代理](#23-Cglib代理)
      - [ 2.3.1 概述](#231-概述)
      - [ 2.3.2 实例](#232-实例)
  <!-- /TOC -->
# Java代理(Proxy)模式

### 参考链接

* [**Java代理（Proxy）模式**](https://www.jianshu.com/p/8ccdbe00ff06)

## 一、前言

### 1.1 概述

* 代理模式(Proxy)是通过代理对象访问目标对象，这样可以在目标对象基础上增强额外的功能，如添加权限，访问控制和审计等功能

### 1.2 三种代理

#### 1.2.1 静态代理

#### 1.2.2 JDK动态代理

#### 1.2.3 Cglib代理

## 二、实例

### 2.1 静态代理

#### 2.1.1 实例

**AdminService**

```java
public interface AdminService {
    void update();
    Object find();
}
```

**AdminServiceImpl**

```java
public class AdminServiceImpl implements AdminService {
    @Override
    public void update() {
        System.out.println("修改系统数据");
    }

    @Override
    public Object find() {
        System.out.println("查看系统数据");
        return new Object();
    }
}
```

**AdminServiceProxy**

```java
public class AdminServiceProxy implements AdminService {
    private AdminService adminService;

    public AdminServiceProxy(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void update() {
        System.out.println("判断用户是否有权限进行update操作");
        adminService.update();
        System.out.println("记录用户执行update操作的用户信息、更改内容和时间等");
    }

    @Override
    public Object find() {
        System.out.println("判断用户是否有权限进行find操作");
        System.out.println("记录用户执行find操作的用户信息、查看内容和时间等");
        return adminService.find();
    }
}
```

**Test**

```java
public class Test {
    public static void main(String[] args) {
        AdminService adminService = new AdminServiceImpl();
        AdminServiceProxy adminServiceProxy = new AdminServiceProxy(adminService);
        System.out.println("update:");
        adminServiceProxy.update();
        System.out.println("find:");
        adminServiceProxy.find();
    }
}
```

**输出**

```java
update:
判断用户是否有权限进行update操作
修改系统数据
记录用户执行update操作的用户信息、更改内容和时间等
find:
判断用户是否有权限进行find操作
记录用户执行find操作的用户信息、查看内容和时间等
查看系统数据
```

#### 2.1.2 说明

**优点**

* 静态代理模式在不改变目标对象的前提下，实现对目标对象的功能扩展

**不足**

* 静态代理实现了目标对象的所有方法，一旦目标接口增加方法，代理对象和目标对象都要进行相应的修改，增加了维护成本

### 2.2 JDK动态代理

#### 2.2.1 特点

* Proxy对象不需要implements接口

* Proxy对象的生成利用JDK的Api，在JVM内存中动态的构建Proxy对象。需要使用java.lang.reflect.Proxy类的方法

  ```java
  /**
  * @param   loader the class loader to define the proxy class
  * @param   interfaces the list of interfaces for the proxy class
  *          to implement
  * @param   h the invocation handler to dispatch method invocations to
  * @return  a proxy instance with the specified invocation handler of a
  *          proxy class that is defined by the specified class loader
  	*          and that implements the specified interfaces
  */
  public static Object newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h)
  ```

#### 2.2.2 实例

**AdminService同上**

**AdminServiceImpl同上**

**AdminServiceInvocation**

```java
public class AdminServiceInvocation implements InvocationHandler {
    private Object target;

    public AdminServiceInvocation(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("判断用户是否有权限进行操作");
        Object object = method.invoke(target);
        System.out.println("记录用户执行操作的用户信息、更改内容和时间等");
        return object;
    }
}
```

**AdminServiceDynamicProxy**

```java
public class AdminServiceDynamicProxy {
    private Object target;
    private InvocationHandler invocationHandler;

    public AdminServiceDynamicProxy(Object target, InvocationHandler invocationHandler) {
        this.target = target;
        this.invocationHandler = invocationHandler;
    }


    public Object getAdminProxy() {
        Object object = Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), invocationHandler);
        return object;
    }
}
```

**Test**

```java
public class Test {
    public static void main(String[] args) {
        // 方法一
        System.out.println("============ 方法一 ==============");
        AdminService adminService = new AdminServiceImpl();
        System.out.println("代理的目标对象：" + adminService.getClass());
        AdminServiceInvocation adminServiceInvocation = new AdminServiceInvocation(adminService);
        AdminService proxy = (AdminService) new AdminServiceDynamicProxy(adminService, adminServiceInvocation).getAdminProxy();
        System.out.println("代理对象：" + proxy.getClass());
        proxy.find();
        System.out.println("----------------------------------");
        proxy.update();

        //方法二
        System.out.println("============ 方法二 ==============");
        AdminService target = new AdminServiceImpl();
        AdminServiceInvocation invocation = new AdminServiceInvocation(adminService);
        AdminService proxy2 = (AdminService) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), invocation);
        proxy2.find();
        System.out.println("----------------------------------");
        proxy2.update();

        //方法三
        System.out.println("============ 方法三 ==============");
        final AdminService target3 = new AdminServiceImpl();
        AdminService proxy3 = (AdminService) Proxy.newProxyInstance(target3.getClass().getClassLoader(), target3.getClass().getInterfaces(), new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("判断用户是否有权限进行操作");
                Object obj = method.invoke(target3, args);
                System.out.println("记录用户执行操作的用户信息、更改内容和时间等");
                return obj;
            }
        });
        proxy3.find();
        System.out.println("----------------------------------");
        proxy3.update();
    }
}
```

**输出**

```java
============ 方法一 ==============
代理的目标对象：class aop.AdminServiceImpl
代理对象：class com.sun.proxy.$Proxy0
判断用户是否有权限进行操作
查看系统数据
记录用户执行操作的用户信息、更改内容和时间等
----------------------------------
判断用户是否有权限进行操作
修改系统数据
记录用户执行操作的用户信息、更改内容和时间等
============ 方法二 ==============
判断用户是否有权限进行操作
查看系统数据
记录用户执行操作的用户信息、更改内容和时间等
----------------------------------
判断用户是否有权限进行操作
修改系统数据
记录用户执行操作的用户信息、更改内容和时间等
============ 方法三 ==============
判断用户是否有权限进行操作
查看系统数据
记录用户执行操作的用户信息、更改内容和时间等
----------------------------------
判断用户是否有权限进行操作
修改系统数据
记录用户执行操作的用户信息、更改内容和时间等
```

**说明**

* 上面的三种方法本质上没有区别

### 2.3 Cglib代理

#### 2.3.1 概述

* JDK动态代理要求target目标对象是一个接口的实现对象，假如target只是一个单独的对象，并没有实现任何接口，这时候就会用到Cglib代理(Code Generation Library)，即通过构建一个子类对象，从而实现对target的代理，因此target不能是final类(报错)，且目标对象的方法不能是final或static(不执行代理功能)

#### 2.3.2 实例

**导入**

* org.chromattic:chromattic.cglib:1.0.0-beta8

**AdminServiceCglib**

```java
public class AdminServiceCglib {

    public void update() {
        System.out.println("修改系统数据");
    }

    public Object find() {
        System.out.println("查看系统数据");
        return new Object();
    }
}
```

**AdminServiceCglibProxy**

```java
public class AdminServiceCglibProxy implements MethodInterceptor {
    private Object target;

    public AdminServiceCglibProxy(Object target) {
        this.target = target;
    }

    public Object getProxyInstance() {
        //工具类
        Enhancer enhancer = new Enhancer();
        //设置父类
        enhancer.setSuperclass(target.getClass());
        //设置回调函数
        enhancer.setCallback(this);
        //创建子类代理对象
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("判断用户是否有权限进行操作");
        Object obj = method.invoke(target);
        System.out.println("记录用户执行操作的用户信息、更改内容和时间等");
        //通过代理修改返回的对象
        return new Integer(1);
    }
}
```

**Test**

```java
public class Test {
    public static void main(String[] args) {
        AdminServiceCglib target = new AdminServiceCglib();
        AdminServiceCglibProxy proxyFactory = new AdminServiceCglibProxy(target);
        AdminServiceCglib proxy = (AdminServiceCglib) proxyFactory.getProxyInstance();

        System.out.println("代理对象：" + proxy.getClass());
        Object obj = proxy.find();
        System.out.println("find 返回对象：" + obj.getClass());
        System.out.println("----------------------------------");
        proxy.update();
    }
}
```

**输出**

```java
代理对象：class aop.AdminServiceCglib$$EnhancerByCGLIB$$b0f0ca1d
判断用户是否有权限进行操作
查看系统数据
记录用户执行操作的用户信息、更改内容和时间等
find 返回对象：class java.lang.Integer
----------------------------------
判断用户是否有权限进行操作
修改系统数据
记录用户执行操作的用户信息、更改内容和时间等
```



