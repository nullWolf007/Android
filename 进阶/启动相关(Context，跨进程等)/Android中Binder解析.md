[TOC]

# Android中Binder解析

### 参考文章

* [ **Android Binder机制全面解析** ](https://www.jianshu.com/p/b5cc1ef9f917)
* [**3分钟带你看懂android中的Binder机制**](https://segmentfault.com/a/1190000018317841)

## 一、前言

### 1.1 Binder定义

* 是一种Android实现跨进程通讯的方式

### 1.2 Liunx相关知识

#### 1.2.1 Liunx进程空间划分

* 一个进程空间分为**用户空间**和**内核空间(Kernel)**，即把进程内用户和内核隔离开，所有进程公用一个内核空间

* 进程间，用户空间的数据不可共享

* 进程间，内核空间的数据共享

* 进程内，用户空间和内核空间进行交互需要通过系统调用，主要函数

  > copy_from_user()：将用户空间的数据拷贝到内核空间
  >
  > copy_to_user()：将内核空间的数据拷贝到用户空间

* 为了保证**安全性**和**独立性**，一个进程不能直接操作或者访问另一个进程，即Android的进程是相互独立、隔离的

* 传统跨进程通信——管道队列模式

  ![管道队列模式](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/%E7%AE%A1%E9%81%93%E9%98%9F%E5%88%97%E6%A8%A1%E5%BC%8F%E8%B7%A8%E8%BF%9B%E7%A8%8B.png)

#### 1.2.2 Binder作用

* Binder的作用就是连接两个进程，实现了mmap()系统调用，主要负责创建数据接收的缓存空间和管理数据接收缓存。相对于上述的管道队列模式需拷贝数据**两次**，而Binder机制执行需要**一次**，主要是使用了**内存映射**

#### 1.2.3内存映射

![内存映射1](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/%E5%86%85%E5%AD%98%E6%98%A0%E5%B0%841.png)

* 内存映射的实现过程主要是通过Linux系统下的函数：mmap()。该函数的作用就是**创建虚拟内存区域**和**与共享对象建立映射关系**

* **内存映射的左右**

  > 1. 实现内存共享：如跨进程通信
  > 2. 提高数据读/写效率：如文件读/写操作

  

## 二、Binder概述

### 2.1 Android为什么选择Binder

* Android是基于Liunx内核的，所以Android为了实现进程间通信，有liunx的许多方法，如管道、socket等方式。既然Android选择Binder，则说明其他方式存在一些问题。
* 进程间通信考虑两个方面：一个是**性能**，一个是**安全**。
* 性能方面：传统的管道队列模式采用内存缓冲区的方式，数据需要拷贝两次，而Binder只用拷贝一次；scoket传输效率低，开销大
* 安全方面：Android作为一个开放式，拥有众多开发者的的平台，应用程序的来源广泛，确保终端安全是非常重要的，传统的IPC通信方式没有任何措施，基本依靠上层协议，其一无法确认对方可靠的身份，Android为每个安装好的应用程序分配了自己的UID，故进程的UID是鉴别进程身份的重要标志，传统的IPC要发送类似的UID也只能放在数据包里，但也容易被拦截，恶意进攻，socket则需要暴露自己的ip和端口，知道这些恶意程序则可以进行任意接入。
* Binder只需要拷贝一次，性能也不低，而且采用传统的C/S结构，稳定性强，发送添加UID/PID，安全性强

### 2.2 Binder跨进程概述图

![**Binder跨进程通信**](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/Binder%E8%B7%A8%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1.png)





  