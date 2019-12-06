[TOC]

# Android的消息机制

## ThreadLocal

### 概念

```text
ThreadLocal时一个线程内部的数据存储类，通过它可以在指定的线程中存储数据，数据存储以后，只有在指定的线程中可以获取到存储的数据，对于其他线程来说则无法获取到数据。
```

### 实例

```java
public class TestActivity extends AppCompatActivity {

    private ThreadLocal<Boolean> booleanThreadLocal = new ThreadLocal<>();
    private String TAG = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        booleanThreadLocal.set(true);
        Log.d(TAG, "thread#main: " + booleanThreadLocal.get());

        new Thread("thread#1") {
            @Override
            public void run() {
                booleanThreadLocal.set(false);
                Log.d(TAG, "thread#1: " + booleanThreadLocal.get());
            }
        }.start();

        new Thread("thread#2") {
            @Override
            public void run() {
                Log.d(TAG, "thread#2: " + booleanThreadLocal.get());
            }
        }.start();
    }
}

//TestActivity: thread#main: true
//TestActivity: thread#1: false
//TestActivity: thread#2: null
```

