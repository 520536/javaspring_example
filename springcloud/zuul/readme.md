zuul加密

````xml
zuul:
  routes:
    tensquare-article: # 文章
      path: /article/** # 配置请求URL的请求规则
      serviceId: tensquare-article #指定Eureka注册中心的服务id
      strip-prefix: true #所有的article的请求都进行转发
      sentiviteHeaders:
      customSensitiveHeaders: true #让zuul网关处理cookie和重定向
````

放key（加密要是） service(加解密) filter（过滤器）