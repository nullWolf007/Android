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
onCreate()只会在Service第一次被创建的时候调用，连续startService只会执行一次onCreate()，执行多次onStartCommand()

## Service和Activity通信
1. 实现onBind()方法
```java
public class MyService extends Service {  
  
    public static final String TAG = "MyService";  
  
    private MyBinder mBinder = new MyBinder();  
  
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
        return mBinder;  
    }  
  
    class MyBinder extends Binder {  
  
        public void startDownload() {  
            Log.d("TAG", "startDownload() executed");  
            // 执行具体的下载任务  
        }  
  
    }  
  
}  
```
2. 创建ServiceConnection的匿名类，重写onServiceConnected()方法和onServiceDisconnected()方法，这两个方法分别会在Activity与Service建立关联和解除关联的时候调用。
```java
public class MainActivity extends Activity implements OnClickListener {  
  
    private Button startService;  
  
    private Button stopService;  
  
    private Button bindService;  
  
    private Button unbindService;  
  
    private MyService.MyBinder myBinder;  
  
    private ServiceConnection connection = new ServiceConnection() {  
  
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
        }  
  
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {  
            myBinder = (MyService.MyBinder) service;  
            myBinder.startDownload();  
        }  
    };  
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        startService = (Button) findViewById(R.id.start_service);  
        stopService = (Button) findViewById(R.id.stop_service);  
        bindService = (Button) findViewById(R.id.bind_service);  
        unbindService = (Button) findViewById(R.id.unbind_service);  
        startService.setOnClickListener(this);  
        stopService.setOnClickListener(this);  
        bindService.setOnClickListener(this);  
        unbindService.setOnClickListener(this);  
    }  
  
    @Override  
    public void onClick(View v) {  
        switch (v.getId()) {  
        case R.id.start_service:  
            Intent startIntent = new Intent(this, MyService.class);  
            startService(startIntent);  
            break;  
        case R.id.stop_service:  
            Intent stopIntent = new Intent(this, MyService.class);  
            stopService(stopIntent);  
            break;  
        case R.id.bind_service:  
            Intent bindIntent = new Intent(this, MyService.class);  
            bindService(bindIntent, connection, BIND_AUTO_CREATE);  
            break;  
        case R.id.unbind_service:  
            unbindService(connection);  
            break;  
        default:  
            break;  
        }  
    }  
  
}  
```
* bindService()方法接收三个参数，第一个参数就是刚刚构建出的Intent对象，第二个参数是前面创建出的ServiceConnection的实例，第三个参数是一个标志位，这里传入BIND_AUTO_CREATE表示在Activity和Service建立关联后自动创建Service，这会使得MyService中的onCreate()方法得到执行，但onStartCommand()方法不会执行。
* 对于使用了startService又使用了bindService，使用stopService和unbindService才能销毁Service

## Service和Thread的关系
* Service和Thread没有关系
* Service运行在主线程中，是不能进行耗时操作的，但是可以在service中开启线程进行耗时操作
* Service它的运行完全不依赖UI，只要进程还在，Service就可以继续运行
* Activity很难对Thread进行控制，但是对于Service，所有的Activity可以与Service进行关联，因此Service来进行处理后台任务，Activity就可以放心的finish
### 标准的Service
```java
@Override  
public int onStartCommand(Intent intent, int flags, int startId) {  
    new Thread(new Runnable() {  
        @Override  
        public void run() {  
            // 开始执行后台任务  
        }  
    }).start();  
    return super.onStartCommand(intent, flags, startId);  
}  
  
class MyBinder extends Binder {  
  
    public void startDownload() {  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
                // 执行具体的下载任务  
            }  
        }).start();  
    }  
  
}  
```



