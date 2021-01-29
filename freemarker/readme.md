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



### 商品/套餐添加时生成静态

health_service_provider 包下

SetmealServiceImpl

 商品添加的同时add()-> generateMobileStaticHtml() ->generateMobileSetmealListHtml()/generateMobileSetmealDetailHtml()->generteHtml()

````java
 @Value("${out_put_path}")
  private String outPutPath;//从属性文件中读取要生成的html对应的目录   
 @Autowired
  private FreeMarkerConfigurer freeMarkerConfigurer;
	//通用的方法，用于生成静态页面
    public void generteHtml(String templateName,String htmlPageName,Map map){
        Configuration configuration = freeMarkerConfigurer.getConfiguration();//获得配置对象
        Writer out = null;
        try {
            Template template = configuration.getTemplate(templateName);
            //构造输出流
            out = new FileWriter(new File(outPutPath + "/" + htmlPageName));
            //输出文件
            template.process(map,out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
````

spring-service下注入bean

````xml
    <bean id="freemarkerConfig"
          class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer">
        <!--指定模板文件所在目录-->
        <property name="templateLoaderPath" value="/WEB-INF/ftl/" />
        <!--指定字符集-->
        <property name="defaultEncoding" value="UTF-8" />
    </bean>
````

模板文件health_service_provider\src\main\webapp\WEB-INF\ftl   mobile_setmeal.ftl mobile_setmeal_detail.ftl

模板超链接

````html
<!--IF指令-->
<#if success=true>
  你已通过实名认证
<#else>  
  你未通过实名认证
</#if>
    
<!--include指令--> 
    <#include "head.ftl"/>
    <#list goodsList as goods>
  商品名称： ${goods.name} 价格：${goods.price}<br>
        
 <!--list指令-->       
</#list>
    <！--定义对象类型-->
    <#assign info={"mobile":"13812345678",'address':'北京市昌平区'} >
电话：${info.mobile}  地址：${info.address}
````





### 商城项目中使用freemarker生成静态页面html 使用绝对路径

````java
1.引入 jar 包

<!-- https://mvnrepository.com/artifact/org.freemarker/freemarker -->
<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.23</version>
</dependency>
1

2.创建 service 接口和实现

package com.p7.framework.service;
import java.util.Map;
public interface StaticPageService {
    void createGoodsStaticHtml(Map<String, Object> root, String id);
}
1


package com.p7.framework.service.impl;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import com.p7.framework.service.StaticPageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class StaticPageServiceImpl implements StaticPageService, ServletContextAware {
    private static final Logger logger = Logger.getLogger(StaticPageServiceImpl.class);
    private ServletContext servletContext;
    private Configuration conf;
    /**
     * 注入 SpringMVC 的 FreeMarkerConfigurer 启动配置类
     */
    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.conf = freeMarkerConfigurer.getConfiguration();
    }
    /**
     * 注入 ServletContext ，获取路径
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    /**
     * 
     * @param root
     *            商品信息
     * @param id
     *            商品 id
     */
    public void createGoodsStaticHtml(Map<String, Object> root, String id) {
        // 获取到路径
        String path = servletContext.getRealPath("/html/goods/detail/" + id + ".html");
        // 创建文件
        File f = new File(path);
        // 判断父文件夹存在不存在
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        // 输出流
        Writer out = null;
        // 创建输出流并指定编码UTF-8，此处的编码与
        try {
            out = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            // 指定模板 返回模板对象 读UTF-8
            Template template = conf.getTemplate("freemarker.html");

            template.process(root, out);
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException: " + e.getMessage());
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage());
        } catch (TemplateException e) {
            logger.error("TemplateException: " + e.getMessage());
        }
    }
}
1
2
3

3.在 SpringMVC 配置文件中配置 FreeMarkerConfigurer

// 在 Freemarker 入门程序中创建的 Configuration ，用来设置模板的加载目录，这个路径必须写成绝对路径。
// Configuration conf = new Configuration();
// conf.setDirectoryForTemplateLoading(new File("E:/ws/framework/demo/ftl/"));
// 但是在实际的项目开发中，我们不可能使用这样一个路径，
// 因此，我们需要借助其他方法，例如 SpringMVC 中有 FreeMarkerConfigurer 这样的一个类
<!-- Freemarker 配置 -->
<bean id="staticPageService" class="com.p7.framework.service.impl.StaticPageServiceImpl">
    <!-- 注入FreeMarkerConfigurer -->
    <property name="freeMarkerConfigurer">
        <bean class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer">
            <!-- 采用相对路径来设置模板目录 -->
            <property name="templateLoaderPath" value="/WEB-INF/ftl/"/>
        </bean>
    </property>
</bean>

<!-- 配置静态资源访问 -->
<mvc:resources mapping="/resources/**" location="/" />
````

