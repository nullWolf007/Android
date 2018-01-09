# Parcelable详解

## 相比于Serializable的优势
* 效率至上，Parcelable的效率更好
* 存储媒介：Parcelable在内存中读写，而Serializable使用I/O读写存储在硬盘上。内存读写速度明显大于I/O读写速度

## Parcelable的基本使用方法
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
