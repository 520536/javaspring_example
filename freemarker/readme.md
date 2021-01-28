### POM

````xml
<dependency>
  <groupId>org.freemarker</groupId>
  <artifactId>freemarker</artifactId>
  <version>2.3.23</version>
</dependency>
````



### 流程

````java
public static void main(String[] args) throws Exception{
    //1.创建配置类
    Configuration configuration=new Configuration(Configuration.getVersion());
    //2.设置模板所在的目录 
    configuration.setDirectoryForTemplateLoading(new File("D:\\ftl"));
    //3.设置字符集
    configuration.setDefaultEncoding("utf-8");
    //4.加载模板
    Template template = configuration.getTemplate("test.ftl");
    //5.创建数据模型
    Map map=new HashMap();
    map.put("name", "张三");
    map.put("message", "欢迎来到传智播客！");
    //6.创建Writer对象
    Writer out =new FileWriter(new File("d:\\test.html"));
    //7.输出
    template.process(map, out);
    //8.关闭Writer对象
    out.close();
}
````

