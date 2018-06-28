
# Serializable和Parcelable的理解和区别
---

## 前言
### 序列化和反序列化
* 序列化：由于内存中的对象是暂时的，无法长期保存，为了把对象的状态保存下来，写入磁盘或者其他介质中的操作，这就是序列化。序列化的对象可以在网络上进行传输，也可以保存在本地。
* 反序列化：反序列化就是序列化的反向操作，也就是说把在磁盘或者其他介质中的对象，反序列化到内存中去，以便进行操作。

### 为什么需要序列化？
* 有时候，在Android开发中，无法将对象的引用传给Activities或者Fragments，这时候我们需要将这些对象放进一个Intent或者[Bundle](https://github.com/nullWolf007/android/blob/master/android/bundle.md)中去，然后进行传递

### 怎么进行序列化？
* 一个对象需要实现序列化操作，该类就必须实现了Serializable接口或者Parcelable接口

## Serializable
&emsp;&emsp; Serializable是java提供的一个序列化接口，它是一个空接口，专门为对象提供标准的序列化和反序列化操作，使用Serializable实现类的序列化比较简单，只要在类声明中实现Serializable接口即可，同时强烈建议声明序列化标识
* 对于序列化和反序列化的时候，serialVersionUID必须是相同的，不然反序列化会报错的

## Parcelable
&emsp;&emsp; Parcelable时安卓专有的，Parcelable方式的实现原理是将一个完整的对象进行分解，而分解后的每一部分都是Intent所支持的数据类型，这样也就实现传递对象的功能了。

## 两者的区别
* Parcelable的出现就是为了提高效率，减少消耗。所以在可以的情况下，最好使用Parcelable。但在数据的持久化上，Serializable具有一点优势，但Parcelable也是可以做到的。

参考文章
* [序列化Serializable和Parcelable的理解和区别](http://www.jianshu.com/p/a60b609ec7e7)
* [序列化与反序列化之Parcelable和Serializable浅析 ](http://blog.csdn.net/javazejian/article/details/52665164)
