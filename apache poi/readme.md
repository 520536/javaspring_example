### POI Excel

````xml
<dependency>
  <groupId>org.apache.poi</groupId>
  <artifactId>poi</artifactId>
  <version>3.14</version>
</dependency>
<dependency>
  <groupId>org.apache.poi</groupId>
  <artifactId>poi-ooxml</artifactId>
  <version>3.14</version>
</dependency>
````

POI结构：

```
HSSF － 提供读写Microsoft Excel XLS格式档案的功能
XSSF － 提供读写Microsoft Excel OOXML XLSX格式档案的功能
HWPF － 提供读写Microsoft Word DOC格式档案的功能
HSLF － 提供读写Microsoft PowerPoint格式档案的功能
HDGF － 提供读Microsoft Visio格式档案的功能
HPBF － 提供读Microsoft Publisher格式档案的功能
HSMF － 提供读Microsoft Outlook格式档案的功能
```

### excel读数据

````java
//创建工作簿  
XSSFWorkbook workbook = new XSSFWorkbook("D:\\hello.xlsx");
//获取工作表，既可以根据工作表的顺序获取，也可以根据工作表的名称获取
XSSFSheet sheet = workbook.getSheetAt(0);
//遍历工作表获得行对象
for (Row row : sheet) {
  //遍历行对象获取单元格对象
  for (Cell cell : row) {
    //获得单元格中的值
    String value = cell.getStringCellValue();
    System.out.println(value);
  }
}
workbook.close();

/*XSSFWorkbook：工作簿
XSSFSheet：工作表
Row：行
Cell：单元格*/
````

读取2 通过行获取最后一个单元格索引，从而根据单元格索引获取每行的一个单元格对象

````java
//创建工作簿
XSSFWorkbook workbook = new XSSFWorkbook("D:\\hello.xlsx");
//获取工作表，既可以根据工作表的顺序获取，也可以根据工作表的名称获取
XSSFSheet sheet = workbook.getSheetAt(0);
//获取当前工作表最后一行的行号，行号从0开始
int lastRowNum = sheet.getLastRowNum();
for(int i=0;i<=lastRowNum;i++){
  //根据行号获取行对象
  XSSFRow row = sheet.getRow(i);
  short lastCellNum = row.getLastCellNum();
  for(short j=0;j<lastCellNum;j++){
    String value = row.getCell(j).getStringCellValue();
    System.out.println(value);
  }
}
workbook.close();
````



### Excel写数据

````java
//在内存中创建一个Excel文件
XSSFWorkbook workbook = new XSSFWorkbook();
//创建工作表，指定工作表名称
XSSFSheet sheet = workbook.createSheet("传智播客");
​
//创建行，0表示第一行
XSSFRow row = sheet.createRow(0);
//创建单元格，0表示第一个单元格
row.createCell(0).setCellValue("编号");
row.createCell(1).setCellValue("名称");
row.createCell(2).setCellValue("年龄");
​
XSSFRow row1 = sheet.createRow(1);
row1.createCell(0).setCellValue("1");
row1.createCell(1).setCellValue("小明");
row1.createCell(2).setCellValue("10");
​
XSSFRow row2 = sheet.createRow(2);
row2.createCell(0).setCellValue("2");
row2.createCell(1).setCellValue("小王");
row2.createCell(2).setCellValue("20");
​
//通过输出流将workbook对象下载到磁盘
FileOutputStream out = new FileOutputStream("D:\\itcast.xlsx");
workbook.write(out);
out.flush();
out.close();
workbook.close();
````



### 引入POIUtils工具  并在POIUtils导poi包

### controller

```java
//文件上传（Excel） MultipartFile spring文件封装对象
@RequestMapping("/upload")
public Result upload(@RequestParam("excelFile") MultipartFile excelFile){
    try {
        List<String[]> list = POIUtils.readExcel(excelFile); //使用POIUtils读excel后close
        /*OrderSetting pojo设定 只有2个数据对应前端   
        public OrderSetting(Date orderDate, int number) {
        this.orderDate = orderDate;
        this.number = number;
    }*/
        List<OrderSetting> data = new ArrayList<>();  
        if(list != null && list.size() > 0){
            for (String[] strings : list) {
                String date = strings[0];
                String number = strings[1];
                OrderSetting orderSetting = new OrderSetting(new SimpleDateFormat("yyyy/MM/dd").parse(date),Integer.parseInt(number));
                data.add(orderSetting);/*写入数据并转化日期*/
            }
        }
        orderSettingService.add(data);
        return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
    } catch (Exception e) {
        e.printStackTrace();
        return new Result(false, MessageConstant.ORDERSETTING_FAIL);
    }
}
```

service-

### orderserviceimpl

````java
  //批量导入预约设置信息
    public void add(List<OrderSetting> list) {
        if(list != null && list.size() > 0){
            for (OrderSetting orderSetting : list) {
                //判断当前日期是否已经进行了设置
                long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if(count > 0){
                    //如果已经设置，执行更新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }else{
                    //如果没有设置，执行插入操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }
````



dao->dao.xml mapper

