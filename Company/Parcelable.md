# Parcelable详解

## 相比于Serializable的优势
* 效率至上，Parcelable的效率更好
* 存储媒介：Parcelable在内存中读写，而Serializable使用I/O读写存储在硬盘上。内存读写速度明显大于I/O读写速度

## Parcelable的基本使用方法
### 长写方法
```java
public int describeContents()：
//内容接口描述，默认返回0就可以
```
```java
public void writeToParcel(Parcel dest, int flags)：
//写入接口函数，打包。将我们的对象序列化为一个Parcelable对象，也就是将我们的对象存入Parcel中
```
```java
public interface Creator<T> {  
    public T createFromParcel(Parcel source);  
    public T[] newArray(int size);  
}  
//实现模板参数的传入，定义Creator嵌入接口，内含两个接口函数分别返回单个和多个继承类实例
```

### 实例
```java
public class TestParcelable implements Parcelable {
    String msg;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
    }

    TestParcelable(String msg) {
        this.msg = msg;
    }

    private TestParcelable(Parcel in) {
        this.msg = in.readString();
    }

    public static final Creator<TestParcelable> CREATOR = new Creator<TestParcelable>() {
        @Override
        public TestParcelable createFromParcel(Parcel source) {
            return new TestParcelable(source);
        }

        @Override
        public TestParcelable[] newArray(int size) {
            return new TestParcelable[size];
        }
    };
}
```
