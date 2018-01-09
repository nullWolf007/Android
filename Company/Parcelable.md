# Parcelable详解

## 相比于Serializable的优势
* 效率至上，Parcelable的效率更好
* 存储媒介：Parcelable在内存中读写，而Serializable使用I/O读写存储在硬盘上。内存读写速度明显大于I/O读写速度

## Parcelable的基本使用方法

