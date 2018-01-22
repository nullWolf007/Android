# 将Sqlite数据库中的数据以Excel格式导出

## 基本步骤
* 创建excel表
```java
//创建常住人口excel
SimpleDateFormat fsdf = new SimpleDateFormat("yyyyMMddHHmmSS");
String excelFile = fsdf.format(System.currentTimeMillis()) + "czrk" + ".xls";
String filePath = Constants.USB_DISK_PATH + "/" + excelFile;
WritableWorkbook book = null;
```
* 打开文件，设置表头，写入数据，关闭文件，抛出异常
```java
try {
    // 打开常住人口文件
    book = Workbook.createWorkbook(new File(filePath));
    // 生成名为"常住人口"的工作表，参数0表示这是第一页
    WritableSheet sheet = book.createSheet("访客记录常住人口", 0);

    //设置列宽,设置第一列宽度为102，参数1：列数，参数2：宽度
    sheet.setColumnView(0, 12);
    
    //固定第一行为表头
    sheet.getSettings().setVerticalFreeze(1);
    Label label = new Label(0, 0, "照片");
    sheet.addCell(label);
    label = new Label(1, 0, "姓名");
    sheet.addCell(label);
    //以此类推
    
    //写入数据
    label = new Label(1,1, bean.getName());
    sheet.addCell(label);
    //以此类推
    
    //写入数据
    if(book != null){
      book.write();
    }
}catch(Exception e){
  //抛出异常  异常处理
}finally{
  if(book != null){
    try{
      //关闭文件
      book.close();
    }catch(Exception e){
      //抛出异常 异常处理
    }
  }
}
```
