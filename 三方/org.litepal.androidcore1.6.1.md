# 数据库操作三方

## 操作步骤

### 1.在build.gradle中引入

```java
//数据库操作工具
compile 'org.litepal.android:core:1.6.1'
```

### 2.创建assets目录，新建litepal.xml文件夹

```xml
<?xml version="1.0" encoding="utf-8"?>
<litepal>
    <!--数据库名称-->
    <dbname value="dbname"></dbname>
    <!--数据库版本-->
    <version value="1"></version>
    <!--数据库存放地址-->
    <storage value="path"></storage>

    <list>
        <!--这里是类映射-->
        <mapping class="com.test.bean.name"/>
    </list>
</litepal>
```

### 3.litepal初始化

（1）将清单文件Application换成LitePalApplication

```xml
<application
        android:name="org.litepal.LitePalApplication"
        ...>
</application>
```

（2）推荐：将自定义的BaseApplication继承自Application，然后在onCreate中，进行litepal初始化

```java
public class BaseApplication extends Application {
    @Override public void onCreate() {
        super.onCreate(); 
        LitePal.initialize(this); 
    } 
	... 
} 
```

### 4.创建实体类（继承DataSupport，生成get和set方法）

```java
public class TestBean extends DataSupport{
    private String testName;
    @Column(unique = true)
    private String uuid;
    
     public String getUuid() {
        return uuid == null ? "" : uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? "" : uuid;
    }
}
```

```java
@Column(unique = true) //是否唯一 
@Column(defaultValue = "unknown") //指定字段默认值 
@Column(nullable = false) //是否可以为空
@Column(ignore = true) //是否可以忽略
```

### 5.新增数据

* 创建对应的实体类，然后调用save()方法


### 6.更新数据

- 指定id

  ```java
  TestBean bean = new TestBean();
  bean.setTestName("222");
  bean.update(1)//根据id更新
  ```

- 条件更新

  ```java
  TestBean bean = new TestBean();
  bean.setTestName("333");
  bean.updateAll("id=?","1")//查询条件更新
  ```

- 更新全部数据

  ```java
  TestBean bean = new TestBean();
  bean.setTestName("444");
  bean.updateAll(TestBean.class,bean)
  ```

- 异步更新数据

  ```java
  TestBean bean = new TestBean();
  bean.setUuid(1);
  bean.setTestName("111");
  bean.saveAsync().listen(new SaveCallback() {
      @Override
      public void onFinish(boolean success) {
  
      }
  });
  ```


### 7.删除数据

- 指定id

  ```java
  DataSupport.delete(TestBean.class,id)
  ```

- 条件删除

  ```java
  DataSupport.deleteAll(TestBean.class,"id=?","1")
  ```

- 删除全表数据

  ```java
  DataSupport.deleteAll(TestBean.class)
  ```

### 8.查询数据

- 查询第一条和最后一条

  ```java
  TestBean fBean = DataSupport.findFirst(TestBean.class);
  TestBean lBean = DataSupport.findLast(TestBean.class);
  ```

- 指定id

  ```java
  TestBean bean = DataSupport.find(TestBean.class, id); //指定多个id如下 
  //方式一： 
  List<TestBean> beanList = DataSupport.findAll(TestBean.class, "1", "3"); 
  //方式二：
  String[] ids = new String[] { "1", "3"};
  List<TestBean> beanList = DataSupport.findAll(News.class, ids); 
  ```

- 查询所有

  ```java
  List<TestBean> beanList = DataSupport.findAll(TestBean.class);
  ```

- 条件查询

  ```java
  List<TestBean> list = DataSupport.select("id", "name")//需要的条目
      .where("name=?", "444")//条件 
      .order("id desc")//倒序字段 
      .offset(5)//开始查询位置
      .limit(15)//数据长度 
      .find(TestBean.class);//查询表 
  ```

- 异步查询，用于代替findAll

  ```java
  DataSupport.findAllAsync(TestBean.class).listen(new FindMultiCallback() {
      @Override
      public <T> void onFinish(List<T> t) {
          List<TestBean> list = (List<TestBean>) t; 
      } 
  });
  ```

### 9.删除数据库

```java
LitePal.deleteDatabases("dbname")；//数据库名
```



### 参考文章

* [android数据库litepal使用记录](https://www.jianshu.com/p/fb9607831906)



