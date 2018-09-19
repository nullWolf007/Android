

[TOC]

# View的事件体系

## 基础知识

### 一、View的位置参数

> top、left、right、bottom     x、y、translation X、translationY
>
> 这些坐标位置都是相对坐标。相对于此View的父容器而言的
>
> top表示此view的上部距离父容器上部的距离
>
> left表示此view的左部距离父容器左部的距离
>
> bottom表示此view的下部距离父容器上部的距离
>
> right表示此view的右部距离父容器左部的距离
>
> x、y表示此View左上角相对于父容器的坐标
>
> translation X、translationY表示相对于自身的偏移量
>
> View在平移的过程中，top、left、right、bottom不会改变， x、y、translation X、translationY会发生变化

### 二、MotionEvent、TouchSlop

1. MotionEvent:手指接触屏幕系列事件
   - ACTION_DWON-------手指刚接触屏幕
   - ACTION_MOVE--------手指在屏幕上移动
   - ACTION_UP---------手指从屏幕上松开的一瞬间

> 通过这些事件，我们可以得到点击事件发生的x和y坐标。getX/getY返回相对于当前View的左上角坐标。getRawX/getRawY返回相对于手机屏幕的左上角坐标

1. TouchSlop

> TouchSlop是系统所能识别出的被认为是滑动的最小距离
>
> 可以通过ViewConfiguration.get(getContext()).getScaledTouchSlop()来获得当前的TouchSlop的值

### 三、VelocityTracker、GestureDetector、Scroller

1. VelocityTracker

> 速度追踪：用于追踪手指在滑动过程中的速度

1. GestureDetector

> 手势检测：用于辅助检测用户的单击、滑动、长按、双击等行为

1. Scroller

> 弹性滑动对象，用于实现View的弹性滑动，给用户更好的交互体验。和View的computeScroll方法配合使用

```java
Scroller scroller = new Scroller(context);

//缓慢滚动到指定位置
private void smoothScrollTo(int destX,int destY){
    int scrollX = getScrollX();
    int delta = destX - scrollX;
    //1000ms内滑到指定位置
    scroller.startScroll(scrollX,0,delta,0,1000);
    invalidate();
}

@Override
public void computeScroll(){
    if(scoller.computeScrollOffset()){
        scrollTo(scroller.getCurrX(),scroller.getCurrY());
        postInvalidate();
    }
}
```

## View的滑动-三种滑动方式

### 一、使用scrollTo/scrollBy

- scrollTo实现基于所传递参数的绝对滑动
- scrollBy实现了基于当前位置的相对滑动
- scrollTo/scrollBy只能改变View内容的位置而不能改变View在布局中的位置

### 二、使用动画

1. View动画

- View动画并不会改变此View的位置信息，所以点击事件还是在动画之前的View区域内
- fillAfter为false的话，动画会在完成的一瞬间回到动画前的状态；如果fillAfter为true的话，会保持在动画的结束状态

1. 属性动画

- OnjectAnimator.ofFloat(targetView,"translationX",0,100).setDuration(100).start

### 三、改变布局参数

- 通过改变LayoutParams的值来实现，真正的改变了View的位置信息

### 四、总结

- scrollTo/scrollBy：能比较方便的实现滑动效果并且不影响内部元素的单击事件，但是他只能滑动View的内容
- 动画：属性动画的话没啥缺点。View动画的话不能改变View本省的属性，会影响点击事件，适用于没有交互的View
- 改变布局参数：操作稍微复杂，适用于有交互的View

## 弹性滑动

### 一、使用Scroller

Scroller本身不能实现View的滑动；它需要配合View的computeScroll方法去完成弹性滑动，它不断地让View重绘，每次重绘都有一个较小的时间间隔；这样Scroller就可以根据这个时间间隔计算出View的滑动位置，在通过scrollTo方法来完成View的滑动。从而形成每一次重绘都有小幅度的滑动，因此感官上形成弹性滑动。

### 二、通过动画-属性动画

本身就是弹性滑动

### 三、使用延时策略

滑动一定的距离之后延时一段时间再进行滑动，如此反复，这样连贯起来，感官上就是弹性滑动。

## View的事件分发机制

### 一、点击事件的传递规则

- 点击事件的分发过程由三个方法来共同完成

  1. public boolean dispatchTouchEvent(MotionEvent ev)

     > 用来进行事件的分发。如果事件能够传递给当前View，那么此方法就一定会被调用，返回结果受当前View的onTouchEvent和下级View的dispatchTouchEvent方法影响，表示是否消耗当前事件

  2. public boolean onInterceptTouchEvent(MotionEvent event)

     > 在上述方法内部调用，用来判断是否拦截某个事件，如果当前View拦截了某个事件，那么在同一个事件序列中，此方法不会被再次调用，返回结果表示是否拦截当前事件

  3. public boolean onTouchEvent(MotionEvent event)

     > 在dispatchTouchEvent方法中调用，用来处理点击事件，返回结果表示是否消耗当前事件，如果不消耗，则在同一个事件序列中，当前View无法再次接收到事件

- 总结：对于根ViewGroup而言，点击事件产生之后，它的dispatchTouchEvent就会被调用。此时判断根ViewGroup的onInterceptTouchEvent方法的返回值，如果返回值为true，拦截当前事件，这个事件就会交给根ViewGroup，即它的onTouchEvent方法就会被调用；如果返回值为false，则当前时间传递给它的子元素，此时子元素的dispatchTouchEvent方法会被调用，反复下去直到被处理。

- 当一个点击事件产生后，它的传递过程遵循如下顺序：Activity----Window----View。如果该View下的所有元素都不处理这个事件（onTouchEvent为false），此时会传递给上层的View，如果一直都不处理这个事件，最后会交给Activity，此时Activity的onTouchEvent方法会被调用。

- 结论：

  1. 同一个事件序列指的是从手指接触到屏幕起，到手指离开屏幕结束，这个过程中所产生的一系列事件。从down开始，多个move，up结束。
  2. 除去特殊手段处理，一个事件序列只能被一个View拦截并消耗。
  3. 某个View一旦决定拦截，那么这一个事件序列都只能由它处理，并且它的onInterceptTouchEvent不会再被调(因为再被调用也失去了意义)。
  4. 某个View一旦开始处理事件，如果他不消耗ACTION_DOWN事件（onTouchEvent返回了false），那么同一事件序列都不会给他处理。此时事件会重新交给它的父元素处理，即父元素的onTouchEvent会被调用。
  5. 如果View不消耗除ACTION_DOWN以外的其他事件，那么这个点击事件会消失，此时父元素的onTouchEvent并不会被调用，并且当前View可以持续收到后续的事件，最终这些消失的点击事件会传递给Activity处理。
  6. ViewGroup默认不拦截任何事件。
  7. View没有onInterceptTouchEvent方法，一旦有点击事件传递给他，那么它的onTouchEvent方法就会被调用。
  8. View的onTouchEvent默认都会消耗事件（返回true），除非它是不可点击的(clickable和longClickable同时为false)。View的longClickable属性默认为false，clickable属性要分情况，Button为true，TextView为false。
  9. View的enable属性不影响onTouchEvent的默认返回值。
  10. onClick会发生的前提是当前View时可点击的，并且它收到了down和up的事件。
  11. 事件传递过程是由外向内的，即事件总是传递给父元素，然后再有父元素分发给子View，通过requestDisallowInterceptTouchEvent方法可以再子元素中干预父元素的事件分发过程，除了ACTION_DOWN事件。

### 二、事件分发源码分析



