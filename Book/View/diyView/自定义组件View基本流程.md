# 自定义组件View基本流程

## 引言
&emsp;&emsp; 有的时候系统的组件满足不了我们的项目的要求或者对于常用的页面我们想封装成组件，我们都可以选择自定义组件

## 自定义View的基本步骤
### 目录
1. 创建View
2. 处理View的布局
3. 绘制View(Draw)
4. 与用户进行交互
5. 优化View

![](https://github.com/nullWolf007/android/blob/master/image/diyZujian/20160617150747985.png)

## 创建View
### (1). 我们需要创建extends View的类
```java
class Demo extends View {//继承View
    public Demo(Context context) {
        this(context, null);
    }

    public Demo(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Demo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    public Demo(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes)
    }
}
```
上述的四个构造函数分别是
* 构造函数1：在代码中生成控件的时候调用(也就是new)，可以使用this(context, null)调用构造函数2
* 构造函数2：由于有AttributeSet，通过过布局文件xml创建一个view时，这个参数会将xml里设定的属性传递给构造函数，在xml运用组件，构造函数1就不能使用，可以使用this(context, attrs, 0)来调用构造函数3
* 构造函数3：在代码比如xml里通过某种方式指定了view的style时，defStyleAttr可以接受style，也可以使用this(context, attrs, defStyleAttr，0)来调用构造函数4
* 构造函数4：如果没有没有传进来style，可以使用默认的defStyleRes,要求sdk在21以上

**属性优先级：xml定义 > xml的style定义 > defStyleAttr > defStyleRes > theme**


**AttributeSet的含义：接收xml中定义的属性信息**

### (2). 定义自定义属性
在values/attrs.xml中定义
```html
   <declare-styleable name="Demo">
        <attr name="demo1"  format="color"></attr>
        <attr name="demo2"  format="dimension"></attr>
    </declare-styleable>
```
可以在xml页面文件中使用这些属性，使用这些自定义属性的时候需要指定命名空间http://schemas.android.com/apk/res/res-auto

### (3). 获取自定义属性
在你选择的构造方法中可以使用下面代码获取自定义属性
```java
 TypedArray a = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.demo,
        0, 0);

   try {
       mShowText = a.getColor(R.styleable.Demo_demo1, Color.WHITE);
       mTextPos = a.getDimensionPixelSize(R.styleable.Demo_demo2, ···);
   } finally {
       a.recycle();
   }
```
由于TypeArray对象是共享的资源，所以使用recycle方法来回收

### (4). 添加设置属性事件(getter和setter)
```java
public boolean isDemo1() {
   return mDemo1;
}

public void setShowText(boolean demo1) {
   mDemo1 = demo1;
   invalidate();
   requestLayout();
}
```
由于属性的改变，所以我们需要上述的两个方法，invalidate方法是调用view的onDraw()方法去重新绘制，requestLayout()是测量获取一个新的布局位置


## 创建View的布局
### 测量
View有他的宽高，在不同的情况下需要进行适配，所以onMeasure方法来根据情况来确定宽高
```java
@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if mode is EXACTLY{
            父布局已经告诉了我们当前布局应该是多大的宽高, 所以我们直接返回从measureSpec中获取到的size 
        }else{
            计算出希望的desiredSize
            if mode is AT_MOST
                返回desireSize和specSize当中的最小值
            else:
                返回计算出的desireSize
        }
    }
```
**重要的两个信息:mode和size**
1. mode代表我们当前控件的父控件告诉我们，应该按怎样的方式布局
mode有三个值：EXACTLY, AT_MOST, UNSPECIFIED
* EXACTLY:父控件告诉我们子控件确定的大小
* AT_MOST：父控件告诉我们一个最大值，不能超过这个最大值
* UNSPECIFIED：当前控件没有限制，这种情况很少
2. size就是父布局传过来的一个大小，父布局希望当前布局的大小

**注意点**
* 调用resolveSizeAndState方法可以简化上面的，传两个参数，测量的大小，父布局希望的大小
* 计算出height和width之后在onMeasure中别忘记调用setMeasuredDimension()方法。否则会出现运行时异常

### onSizeChange()
在view第一次被指定大小值，或者view的大小发生改变的时候调用

## 绘制View(Draw)
**onDraw()方法**
* Canvas：决定画什么(比如线还是矩形什么的)
* Paint：决定怎么画(线的颜色还是举行的实心空心啥的)

**注意：初始化不要在onDraw中，因为重新绘制是比较频繁的，所以提前做好初始化**

## 与用户进行交互
我们需要的不仅仅是页面的显示，我们要实现对用户的时间进行反馈，也就是用户交互。最常见的就是触摸事件了
```java
    @Override
   public boolean onTouchEvent(MotionEvent event) {
    return super.onTouchEvent(event);
   }
```

## 优化View
* 避免不必要的代码 
* 在onDraw()方法中不应该有会导致垃圾回收的代码。 
* 尽可能少让onDraw()方法调用，大多数onDraw()方法调用都是手动调用了invalidate()的结果，所以如果不是必须，不要调用invalidate()方法

参考文章
* [Android自定义View的官方套路 ]{http://blog.csdn.net/yissan/article/details/51136088}
