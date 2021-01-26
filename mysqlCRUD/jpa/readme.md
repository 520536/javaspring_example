### 1. oshoping_service_api   pojo   

```java
/*pojo*/
package com.oshoping.goods.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * brand实体类
 * @author 黑马架构师2.5
 *
 */
@Table(name="tb_brand")
public class Brand implements Serializable {

   @Id
   private Integer id;//品牌id
```

2.  oshoping_service   controller ->service->(impl ->Dao)

   ```java
   /*controller*/
   package com.oshoping.goods.controller;
   import com.oshoping.entity.PageResult;
   import com.oshoping.entity.Result;
   import com.oshoping.entity.StatusCode;
   import com.oshoping.goods.service.BrandService;
   import com.oshoping.goods.pojo.Brand;
   import com.github.pagehelper.Page;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.web.bind.annotation.*;
   import java.util.List;
   import java.util.Map;
   @RestController
   @CrossOrigin
   @RequestMapping("/brand")
   public class BrandController {
   
   
       @Autowired
       private BrandService brandService;
   
       /**
        * 查询全部数据
        * @return
        */
       @GetMapping
       public Result findAll(){}
   ```

   ```JAVA
   /*service*/
   package com.oshoping.goods.service;
   
   import com.oshoping.goods.pojo.Brand;
   import com.github.pagehelper.Page;
   
   import java.util.List;
   import java.util.Map;
   
   public interface BrandService {
   ```

   ```java
   /*serviceimpl*/
   package com.oshoping.goods.service.impl;
   
   import com.oshoping.goods.dao.BrandMapper;
   import com.oshoping.goods.service.BrandService;
   import com.oshoping.goods.pojo.Brand;
   import com.github.pagehelper.Page;
   import com.github.pagehelper.PageHelper;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.stereotype.Service;
   import tk.mybatis.mapper.entity.Example;
   
   import java.util.List;
   import java.util.Map;
   
   @Service
   public class BrandServiceImpl implements BrandService {
   ```

   ```java
   /*dao mapper*/
   package com.oshoping.goods.dao;
   
   import com.oshoping.goods.pojo.Brand;
   import org.apache.ibatis.annotations.Param;
   import org.apache.ibatis.annotations.Select;
   import tk.mybatis.mapper.common.Mapper;
   
   import java.util.List;
   import java.util.Map;
   
   public interface BrandMapper extends Mapper<Brand> {
   
       @Select("SELECT name,image FROM tb_brand where id in( SELECT brand_id FROM tb_category_brand WHERE category_id in ( SELECT id from tb_category where name=#{categoryName}))")
       public List<Map> findBrandListByCategoryName(@Param("categoryName")String categoryName);
   }
   
   ```