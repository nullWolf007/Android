图片加载库Glide和Picasso比较
===

## Import to project
**Picasso**
``` javascript  
    dependencies {
    compile 'com.squareup.picasso:picasso:2.5.1'
    }
```    
    
**Glide**
``` javascript 
    dependencies {
    compile 'com.github.bumptech.glide:glide:3.5.2'
    compile 'com.android.support:support-v4:22.0.0'
    }
```    
     
Glide的使用是基于support-v4的，所以也要导入(对于安卓项目，这几乎是必须的，V4包)。至于Picassoh和Glide的版本号，根据你的需要选择。


## Basic

如果只是简单的从一个 URL 中下载图片，然后显示到 imageView 中，那么两个库的使用方式基本相似，也都非常的简单。同时两个库也都支持动画和大小的剪切，也可以设置加载时候的预设图片等功能。

**Picasso**
``` javascript 
      Picasso.with(context)
     .load(url)
     .centerCrop()
     .placeholder(R.drawable.loading_spinner)
     .into(myImageView);
``` 

**Glide**
``` javascript
     Glide.with(context)
    .load(url)
    .centerCrop()
    .placeholder(R.drawable.loading_spinner)
    .crossFade()
    .into(myImageView);
```

但是GLide有个令人满意的地方Glide的.with()方法可以是Activity也可以是Fragment。

<img src="https://github.com/nullWolf007/android/blob/master/image/imageGlidePicasso/with.png" width="660" height="320"/>


## Different Default Bitmap Format
Here is the result of image loading by using Glide or Picasso

<img src="https://github.com/nullWolf007/android/blob/master/image/imageGlidePicasso/firstload.jpg"/>

From the picture,you can find that image loading by using Glide has the worse quality compared to Picasso,beacuse they has different default bitmap format.As for Glide,it is **RGB_565**;As for Picasso,it is **ARGB_8888**.You could get better quality if you cost more memory.So...

Here is the memory consumption graphs between Picasso and Glide,you can notice that the amount of memory that the Glide has cost is nearly the half of the Picasso.

<img src="https://github.com/nullWolf007/android/blob/master/image/imageGlidePicasso/ram1_1.png"/>

如果你不满意Glide的图片质量，你可以把其转换为ARGB_8888，创建一个implement GlideModule的class
```javascript
public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}
```
然后在AndroidManifest.xml中添加
```javascript
<meta-data android:name="com.inthecheesefactory.lab.glidepicasso.GlideConfiguration"
            android:value="GlideModule"/>
```
我们再次比较一下，Picasso和Glide所花费的内存，我们会发现，Glide还是会花费较少的内存

<img src="https://github.com/nullWolf007/android/blob/master/image/imageGlidePicasso/ram2_1.png"/>

这是因为Glide和Picasso两者的缓存机制是不一样的

## 缓存机制
**Picasso**
* Picasso使用的是下载图片然后缓存完整的大小到本地

**Glide**
* Glide使用的是下载图片，然后根据ImageView的大小，改变图片的大小，缓存到本地中。如果下载同一张图片到两个不同的ImageView中，会缓存两份

## 加载图片时间
* 对于下载开始而言：Picasso要快点，这是因为Glide要根据ImageView的大小，来缓存
* 对于从缓存中加载：Glide快点，因为Picasso还需要resize，而Glide已经是ImageView的大小了

## 库的大小和方法数
* Glide的库的大小和方法数都是Picasso的三四倍左右

## Glide比Picasso多的功能
* Glide可以支持GIF图片的显示，而Picasso不能

## [Glide的一些用法](http://www.jianshu.com/p/c9efd313e79e)

参考文章：
* [Introduction to Glide, Image Loader Library for Android, recommended by Google](https://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en)
* [深入对比Glide 和 Picasso](http://www.jianshu.com/p/fc72001dc18d)
* [Glide的一些用法（一）（写了一下午，其实几乎涵盖完了，欢迎收藏）](http://www.jianshu.com/p/c9efd313e79e)
