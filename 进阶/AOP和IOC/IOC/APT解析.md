[TOC]

# APT解析

### 参考链接

* [**Java编译时注解处理器（APT）详解**](https://blog.csdn.net/qq_20521573/article/details/82321755)
* [**【Android】注解框架（三）-- 编译时注解，手写ButterKnife**](https://www.jianshu.com/p/57211e053d0c)

## 一、APT简介

### 1.1 什么是APT

* APT即为Annotation Processing Tool，它是javac的一个工具，即编译时注解处理器。APT可以用来在编译时扫描和处理注解。通过APT可以获取到注解和被注解对象的相关信息，在拿到这些信息后我们可以根据需求来自动的生成一些代码，省去了手动编写。注意，获取注解及生成代码都是在代码编译时完成的，相比反射在运行时处理注解大大提高了程序性能。APT的核心时AbstractProcessor类

### 1.2 哪里用到APT？

* APT技术被广范的运用在Java框架中，包括Android项目以及Java后台项目，对于Android项目像EventBus、ButterKnife、Dagger2等都是用到了APT技术

### 1.3 如何在AS中构建APT项目

#### 1.3.1 说明

* 需要两个Java Library(new一个Module就行)，一般就是Annotation模块和Compiler模块

* Annotation模块，用来存放自定义的注解

* Compiler模块，这个模块依赖Annotation模块

* 主项目模块，需要依赖Annotation模块，同时需要通过annotationProcessor依赖Compiler模块

* 为什么要强调上述两个模块一定要是Java Library？如果创建Android Library模块你会发现不能找到AbstractProcessor这个类，这是因为Android平台是基于OpenJDK的，而OpenJDK中不包含APT的相关代码。因此，在使用APT时，必须在Java Library中进行。

#### 1.3.2 APT流程图

![APT流程图](https://github.com/nullWolf007/images/raw/master/android/%E8%BF%9B%E9%98%B6/AOP%E5%92%8CIOC/APT%E6%B5%81%E7%A8%8B%E5%9B%BE.png)

  



