# Activity的生命周期和启动模式

## 前提知识

### Instrumentation

### ActivityThread

### ActivityManagerService



## Activity生命周期

### 跳转流程（最普通的startActivity）

1.A-onpuse   

2.B-onCreate

3.B-onStart

4.B-onResume

5.A-onStop

### 异常情况生命周期分析

1.Activity异常结束的时候，会在onStop之前调用onSaveInstanceState方法，可以在这个方法保存当前数据和状态。当Activity重新创建之后，会在onStart方法后面调用onRestoreInstanceState，并把之前保存的数据传递进来

2.屏蔽某些情况下Activity的异常销毁：

```xml
<activity 
         android:configChanges="orientation|screenSize">
</activity>
<!--locale：设备的本地位置发生改变，一般指切换了系统语言-->
<!--keyboardHidden：键盘的可访问性发送了改变，比如用户调出了键盘-->
<!--orientation：屏幕方向发生了改变，比如旋转了手机屏幕-->
<!--screenSize：当屏幕的尺寸信息发送了改变，当旋转屏幕时，屏幕尺寸也会发生变化（API13及以后）-->
```

此时不会销毁，但是会调用onConfigurationChanged



## Activity的启动模式

1.standard:标准模式，每次启动Activity都会重新创建一个新的实例

2.singleTop:栈顶复用模式，如果新的Activity实例位于任务栈的栈顶，则不会重新创建，onNewIntent会回调

3.singleTask:栈内复用模式，如果栈中存在新的Activity所需的栈和实例，则不会重新创建，onNewIntent会回调。同时具有clearTop功能，会将栈内所有在此之上的Activity出栈；如果存在所需的栈而没有实例，直接在该栈中创建新的实例，不会调用onNewIntent

4.singleInstance:单实例模式，只能单独的位于一个栈里面



## Activity的Flags

1.FLAG_ACTIVITY_NEW_TASK：指定为singleTask模式

2.FLAG_ACTIVITY_SINGLE_TOP：指定为singleTop模式

3.FLAG_ACTIVITY_CLEAR_TOP：具有此标志的Activity，位于上方的都要出栈

4.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS：具有这个标记的Activity不会出现在历史Activity的列表中



## IntentFilter的匹配规则

### 1.action匹配规则

Intent中的action存在一个且必须和过滤规则中的其中一个action相同

### 2.category的匹配规则

Intent没有出现category；如果有category，则所有的category必须都和过滤规则中的其中一个category相同

### 3.data的匹配规则

Intent中的data存在一个且必须和过滤规则中的其中一个data相同

data的语法：

```xml
<data android:scheme="string"
      android:host="string"
      android:port="string"
      android:path="string"
      android:pathPattern="string"
      android:pathPrefix="string"
      android:mimeType="string"/>

URI：<scheme>://<host>:<port>/[<path>|<pathPrefix>|<pathPattern>]
mimetype：媒体类型
```

方法：

```java
setData();//设置一个，另外一个置null
setType();//设置一个，另外一个置null
setDataAndType();//设置两个
```

### 4.判断该Activity是否匹配

PackageManager的resolveActivity方法或者Intent的resolveActivity方法

```java
resolveActivity(Intent intent,int flags);
//flags为MATCH_DEFAULT_ONLY仅仅匹配在inter-filter中声明
//<category android:name="android.intent.DEFAULT">的Activity，保证不为null的都能正确的startActivity

```




