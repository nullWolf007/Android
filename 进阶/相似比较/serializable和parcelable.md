[TOC]

# Serializable和Parcelable的理解和区别

## 1.前言

### 1.1序列化和反序列化
* 序列化：由于内存中的对象是暂时的，无法长期保存，为了把对象的状态保存下来，写入磁盘或者其他介质中的操作，这就是序列化。序列化的对象可以在网络上进行传输，也可以保存在本地。
* 反序列化：反序列化就是序列化的反向操作，也就是说把在磁盘或者其他介质中的对象，反序列化到内存中去，以便进行操作。

### 1.2.为什么需要序列化？
* 在Android开发中，无法将对象的引用传给Activities或者Fragments，这时候我们需要将这些对象放进一个Intent或者[Bundle](https://github.com/nullWolf007/android/blob/master/android/bundle.md)中去，然后进行传递
* 永久性保存对象,保存对象的字节序列到本地文件中

### 1.3.怎么进行序列化？
* 一个对象需要实现序列化操作，该类就必须实现了Serializable接口或者Parcelable接口

## 2.Serializable
* Serializable是java提供的一个序列化接口，它是一个空接口，专门为对象提供标准的序列化和反序列化操作，使用Serializable实现类的序列化比较简单，只要在类声明中实现Serializable接口即可，同时强烈建议声明序列化标识

* 对于序列化和反序列化的时候，serialVersionUID必须是相同的，不然反序列化会报错的

## 3.Parcelable
* Parcelable时安卓专有的，Parcelable方式的实现原理是将一个完整的对象进行分解，而分解后的每一部分都是Intent所支持的数据类型，这样也就实现传递对象的功能了。

### 3.1使用说明

* **注意写入和读取的顺序一致**

* implements Parcelable
* 重写writeToParcel方法,序列化写入(writeInt之类的)
* 重写describeContents方法
* 实例化静态内部对象CREATOR,实现接口Parcelable.Creator,反序列化读取(readInt之类的)

### 3.2实例代码

```java
public class Book implements Parcelable {
    private int id;
    private String name;
    private double price;

    public Book(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    protected Book(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readDouble();
    }

    //反序列化
    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    //内容描述
    @Override
    public int describeContents() {
        return 0;
    }


    //序列化
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeDouble(price);
    }
}
```

## 4.两者的区别

* Parcelable实在内存中读写,Serializable是通过IO存储在磁盘上,所以Parcelable要快

* Parcelable的出现就是为了提高效率，减少消耗。所以在可以的情况下，最好使用Parcelable。但在数据的持久化上(比如数据保存到磁盘上类似的情况)，Serializable具有一点优势，但Parcelable也是可以做到的。

参考文章
* [序列化Serializable和Parcelable的理解和区别](http://www.jianshu.com/p/a60b609ec7e7)
* [序列化与反序列化之Parcelable和Serializable浅析 ](http://blog.csdn.net/javazejian/article/details/52665164)
