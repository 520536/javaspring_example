•下面是一些常见查询表达式示例：

// 以下语句查询 Id 介于 100 至 200 之间的订单。

select o from Orders o where o.id between 100 and 200

// 以下语句查询国籍为的 'US'、'CN'或'JP' 的客户。

select c from Customers c where c.county in ('US','CN','JP')

### where

 以下语句查询手机号以139开头的客户。%表示任意多个字符序列，包括0个。

select c from Customers c where c.phone like '139%'

// 以下语句查询名字包含4个字符，且234位为ose的客户。_表示任意单个字符。

select c from Customers c where c.lname like '_ose' 

// 以下语句查询电话号码未知的客户。Nul l用于测试单值是否为空。

select c from Customers c where c.phone is null

// 以下语句查询尚未输入订单项的订单。empty用于测试集合是否为空。

select o from Orders o where o.orderItems is empty



### order

•order by子句用于对查询结果集进行排序。和SQL的用法类似，可以用 “asc“ 和 "desc“ 指定升降序。如果不显式注明，默认为升序。

select o from Orders o order by o.id

select o from Orders o order by o.address.streetNumber desc

select o from Orders o order by o.customer asc, o.id desc



### group by子句与聚合查询

•group by 子句用于对查询结果分组统计，通常需要使用聚合函数。常用的聚合函数主要有 AVG、SUM、COUNT、MAX、MIN 等，它们的含义与SQL相同。例如：

select max(o.id) from Orders o

•没有 group by 子句的查询是基于整个实体类的，使用聚合函数将返回单个结果值，可以使用Query.getSingleResult()得到查询结果。例如：

Query query = entityManager.createQuery(

 "select max(o.id) from Orders o");

Object result = query.getSingleResult();

Long max = (Long)result;

### having子句

•Having 子句用于对 group by 分组设置约束条件，用法与where 子句基本相同，不同是 where 子句作用于基表或视图，以便从中选择满足条件的记录；having 子句则作用于分组，用于选择满足条件的组，其条件表达式中通常会使用聚合函数。

•例如，以下语句用于查询订购总数大于100的商家所售商品及数量：

select o.seller, o.goodId, sum(o.amount) from V_Orders o group by 

o.seller, o.goodId having sum(o.amount) > 100

•having子句与where子句一样都可以使用参数。



