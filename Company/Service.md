# Android Service完全解析
## Service的基本用法
1. 新建一个MyService继承Service，然后重写父类的onCreate()，onStartCommand()和onDestory()方法
```java
public class MyService extends Service {  
  
    public static final String TAG = "MyService";  
  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        Log.d(TAG, "onCreate() executed");  
    }  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.d(TAG, "onStartCommand() executed");  
        return super.onStartCommand(intent, flags, startId);  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        Log.d(TAG, "onDestroy() executed");  
    }  
  
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
  
}  
```
2. 启动服务和停止服务
```java
Intent startIntent = new Intent(this, MyService.class);  
startService(startIntent);  
```
```java
Intent stopIntent = new Intent(this, MyService.class);  
stopService(stopIntent);  
```
