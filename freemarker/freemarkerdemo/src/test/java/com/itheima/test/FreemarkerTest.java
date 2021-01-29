package com.itheima.test;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreemarkerTest {
    public static void main(String[] args) throws Exception{
        //创建freemarker的配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板文件所在目录
        configuration.setDirectoryForTemplateLoading(new File("d:\\ftl"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //加载模板文件
        Template template = configuration.getTemplate("test.ftl");
        //准备模板文件中所需要的数据，通常是通过Map进行构造
        Map map = new HashMap();
        map.put("name","itcast");
        map.put("message","hello freemarker!!");
        map.put("success",true);

        List goodsList=new ArrayList();

        Map goods1=new HashMap();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);

        Map goods2=new HashMap();
        goods2.put("name", "香蕉");
        goods2.put("price", 2.5);

        Map goods3=new HashMap();
        goods3.put("name", "橘子");
        goods3.put("price", 3.2);

        goodsList.add(goods1);
        goodsList.add(goods2);
        goodsList.add(goods3);

        map.put("goodsList", goodsList);

        //准备输出流对象，用于输出静态文件
        Writer writer = new FileWriter("d:\\ftl\\test.html");
        //输出
        template.process(map,writer);
        //关闭流
        writer.close();
    }
}
