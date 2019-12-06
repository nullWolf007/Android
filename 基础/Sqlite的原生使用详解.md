# Sqlite的原生使用详解

## 创建帮助类
```java
public class DBOpenHelper extends SQLiteOpenHelper {

    public DBOpenHelper(Context context) {
        super(context, "my.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * 进行建表操作
       */
        db.execSQL("CREATE TABLE people_record(_id integer primary key autoincrement,time INTEGER,name TEXT,pic TEXT,data TEXT)");

        //创建唯一索引
        db.execSQL("CREATE UNIQUE INDEX unique_index_people_record ON people_record (_id)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //更新表操作
        db.execSQL("DROP INDEX IF EXISTS unique_index_people_record");

        db.execSQL("DROP TABLE IF EXISTS people_record");

        onCreate(db);
    }
}

```

## 实现具体的数据库增删改查
```java
public class PeopleRecordService {
    private static DBOpenHelper dbOpenHelper;
    private static final String TABLE_NAME = "people_record";
    private static PeopleRecordService instance;

    public PeopleRecordService(Context context) {
        if (dbOpenHelper == null) {
            dbOpenHelper = new DBOpenHelper(context);
        }
    }

    public static PeopleRecordService getInstance(Context context) {
        if (instance == null) {
            instance = new PeopleRecordService(context);
        }
        return instance;
    }

    /**
     * 插入记录，插入bean
     *
     * @param bean
     * @return 唯一标志id
     * time INTEGER,name TEXT,pic TEXT,data TEXT
     */
    public int insert(PeopleInfo bean) {
        long ts = System.currentTimeMillis();
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME +
                "(time,name,pic,data) " +
                "values(?,?,?,?)";
        db.execSQL(sql, new Object[]{
                bean.getTime(),
                bean.getName(),
                bean.getPic(),
                bean.getData()});

        Cursor cursor = db.rawQuery("select last_insert_rowid() from " + TABLE_NAME, null);
        int id = 1;
        if (cursor.moveToFirst()) id = cursor.getInt(0);

        cursor.close();
        db.close();

        return id;
    }

    /**
     * 更新data
     *
     * @param id
     * @param data data TEXT
     */
    public void updateDataById(int id, String data) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET data=? WHERE _id=?",
                new Object[]{data, id});
        db.close();
    }

    /**
     * 删表操作
     *
     * @param id
     */
    public void delete(int id) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where _id=?", new Object[]{id});
        db.close();
    }

    //清空表并释放存储空间
    public void clean() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        //删除所有记录
        db.execSQL("delete from " + TABLE_NAME);
        //回收存储空间
        db.execSQL("VACUUM");
        db.close();
    }

    /**
     * 取指定记录号和身份证号的记录
     *
     * @param recordId
     * @return
     */
    public PeopleInfo findById(int recordId) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE _id=" + String.valueOf(recordId);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        PeopleInfo bean = null;
        if (!cursor.isAfterLast()) {
            //time INTEGER,name TEXT,pic TEXT,data TEXT
            bean = new PeopleInfo();
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String pic = cursor.getString(cursor.getColumnIndex("pic"));
            String data = cursor.getString(cursor.getColumnIndex("data"));

            bean.setId(recordId);
            bean.setTime(time);
            bean.setName(name);
            bean.setPic(pic);
            bean.setData(data);

        }
        cursor.close();
        db.close();

        return bean;
    }
}

```
