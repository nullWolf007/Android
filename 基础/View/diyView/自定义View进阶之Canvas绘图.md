# 安卓自定义View进阶之Canvas绘制图形

## 前言
Canvas：叫做画布，能够绘制很多东西，是安卓平台2D绘制的基础

## Canvas的常用操作

操作类型       | 相关API | 备注
--------------|-------- |----
绘制颜色       | drawColor, drawRGB, drawARGB  | 使用单一颜色填充整个画布
绘制基本形状   | drawPoint, drawPoints, drawLine, drawLines, drawRect, drawRoundRect, drawOval, drawCircle, drawArc  | 依次为 点、线、矩形、圆角矩形、椭圆、圆、圆弧
绘制图片      | drawBitmap, drawPicture  | 绘制位图和图片
绘制文本      | drawText, drawPosText, drawTextOnPath  | 依次为 绘制文字、绘制文字时指定每个文字位置、根据路径绘制文字
绘制路径      | drawPath  | 绘制路径，绘制贝塞尔曲线时也需要用到该函数
顶点操作      | drawVertices, drawBitmapMesh  | 通过对顶点操作可以使图像形变，drawVertices直接对画布作用、 drawBitmapMesh只对绘制的Bitmap作用
画布剪裁      | clipPath, clipRect | 设置画布的显示区域
画布快照      | save, restore, saveLayerXxx, restoreToCount, getSaveCount,getSaveCount | 依次为 保存当前状态、 回滚到上一次保存的状态、 保存图层状态、 回滚到指定状态、 获取保存次数
画布变换      | translate, scale, rotate, skew  | 依次为 位移、缩放、 旋转、错切
Matrix(矩阵)  | getMatrix, setMatrix, concat  | 实际上画布的位移，缩放等操作的都是图像矩阵Matrix， 只不过Matrix比较难以理解和使用，故封装了一些常用的方法。

## 画笔(Paint)的三种模式
```text
STROKE                //描边
FILL                  //填充
FILL_AND_STROKE       //描边加填充
```

## Canvas画布操作
所有画布操作只影响后续的绘制，对之前绘制的内容不会影响
### 位移(translate)
* translate是坐标系的移动，可以为图形绘制选择一个合适的坐标系。**位移是基于当前位置的移动，而不是基于原点的移动，而且移动是可以叠加的**
```java
// 省略了创建画笔的代码

// 在坐标原点绘制一个黑色圆形
mPaint.setColor(Color.BLACK);
canvas.translate(200,200);
canvas.drawCircle(0,0,100,mPaint);

// 在坐标原点绘制一个蓝色圆形
mPaint.setColor(Color.BLUE);
canvas.translate(200,200);
canvas.drawCircle(0,0,100,mPaint);
```
<img src="https://github.com/nullWolf007/knowledge/blob/master/image/diyZujian/cf673337jw1f8mhcii1o2j208c0etdfs.jpg" width="270" height="460"/>

### 缩放(scale)
两个方法
```java
public void scale (float sx, float sy)

public final void scale (float sx, float sy, float px, float py)
```
**sx和sy分别对应x轴和y轴的缩放比例，px和py用来控制缩放中心位置的**

sx和sy的取值含义

取值范围(n) | 说明
---------- | -----
(-∞, -1)   | 先根据缩放中心放大n倍，再根据中心轴进行翻转  
-1         | 根据缩放中心轴进行翻转
(-1, 0)    | 先根据缩放中心缩小到n，再根据中心轴进行翻转
0          | 不会显示，若sx为0，则宽度为0，不会显示，sy同理
(0, 1)     | 根据缩放中心缩小到n
1          | 没有变化
(1, +∞)    | 根据缩放中心放大n倍

**缩放过程中默认的缩放中心点位坐标原点，默认的缩放中心轴就是坐标轴**
```java
// 将坐标系原点移动到画布正中心
canvas.translate(mWidth / 2, mHeight / 2);

RectF rect = new RectF(0,-400,400,0);   // 矩形区域

mPaint.setColor(Color.BLACK);           // 绘制黑色矩形
canvas.drawRect(rect,mPaint);

canvas.scale(0.5f,0.5f);                // 画布缩放

mPaint.setColor(Color.BLUE);            // 绘制蓝色矩形
canvas.drawRect(rect,mPaint);
```
<img src="https://github.com/nullWolf007/knowledge/blob/master/image/diyZujian/cf673337jw1f8mhkom4zrj208c0etaa5.jpg" widht="270" height="460"/>

**然后我们移动缩放中心，并选择负值，实现翻转**
```java
// 将坐标系原点移动到画布正中心
canvas.translate(mWidth / 2, mHeight / 2);

RectF rect = new RectF(0,-400,400,0);   // 矩形区域

mPaint.setColor(Color.BLACK);           // 绘制黑色矩形
canvas.drawRect(rect,mPaint);

canvas.scale(-0.5f,-0.5f,200,0);          // 画布缩放  <-- 缩放中心向右偏移了200个单位

mPaint.setColor(Color.BLUE);            // 绘制蓝色矩形
canvas.drawRect(rect,mPaint);
```
<img src="https://github.com/nullWolf007/knowledge/blob/master/image/diyZujian/suofang2.jpg" width="270" height="460"/>

**缩放和移动一样都是可以叠加的**

### 旋转(rotate)
两种方法
```java
public void rotate (float degrees)

public final void rotate (float degrees, float px, float py)
```
degress表示旋转的角度，后面两个参数表示旋转中心点。

**旋转也是可以叠加的**至于效果和上面都是一样的，就不展示了

### 错切(skew)
**错切是特殊的线性变换**
```java
public void skew (float sx, float sy)
```
* float sx:将画布在x方向上倾斜相应的角度，sx倾斜角度的tan值，
* float sy:将画布在y轴方向上倾斜相应的角度，sy为倾斜角度的tan值.
变换后的X和Y的值
```java
X = x + sx * y
Y = sy * x + y
```
**错切也是可以叠加的**

### 快照(save)和回滚(restore)
画布的操作是不可逆的，所以我们选择对有些画布状态进行保存或者回滚

相关API |   简介
--------|-------
save | 把当前的画布的状态进行保存，然后放入特定的栈中
saveLayerXxx | 新建一个图层，并放入特定的栈中
restore | 把栈中最顶层的画布状态取出来，并按照这个状态恢复当前的画布
restoreToCount | 弹出指定位置及其以上所有的状态，并按照指定位置的状态进行恢复
getSaveCount | 获取栈中内容的数量(即保存次数)

**save**
两种方法
```java
// 保存全部状态
public int save ()

// 根据saveFlags参数保存一部分状态
public int save (int saveFlags)
```
**SaveFlags**

名称 | 简介
-----|-----
ALL_SAVE_FLAG | 默认，保存全部状态
CLIP_SAVE_FLAG | 保存剪辑区
CLIP_TO_LAYER_SAVE_FLAG | 剪裁区作为图层保存
FULL_COLOR_LAYER_SAVE_FLAG | 保存图层的全部色彩通道
HAS_ALPHA_LAYER_SAVE_FLAG | 保存图层的alpha(不透明度)通道
MATRIX_SAVE_FLAG | 保存Matrix信息( translate, rotate, scale, skew)

**saveLayerXxx**
```java
 无图层alpha(不透明度)通道
public int saveLayer (RectF bounds, Paint paint)
public int saveLayer (RectF bounds, Paint paint, int saveFlags)
public int saveLayer (float left, float top, float right, float bottom, Paint paint)
public int saveLayer (float left, float top, float right, float bottom, Paint paint, int saveFlags)

// 有图层alpha(不透明度)通道
public int saveLayerAlpha (RectF bounds, int alpha)
public int saveLayerAlpha (RectF bounds, int alpha, int saveFlags)
public int saveLayerAlpha (float left, float top, float right, float bottom, int alpha)
public int saveLayerAlpha (float left, float top, float right, float bottom, int alpha, int saveFlags)
```

**restore**
```text
将当前的画布状态更新为栈顶的画布状态
```
**restoreToCount**
```text
弹出指定位置以上的所有状态，并把当前的画布状态更新为指定位置的画布状态
```
**getSaveCount**
```text
返回保存的次数，不过这个的最小值为1，即使没有save，结果也是1
```


参考文章
* [安卓自定义View进阶-Canvas之绘制图形](http://www.gcssloop.com/customview/Canvas_BasicGraphics)
* [安卓自定义View进阶-Canvas之画布操作](http://www.gcssloop.com/customview/Canvas_Convert)
