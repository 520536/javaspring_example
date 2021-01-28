![Snipaste_2021-01-25_21-47-48](https://github.com/520536/javaspring_example/blob/master/netty/img/Snipaste_2021-01-25_21-47-48.png)



![Snipaste_2021-01-25_21-47-48](F:\projet\javaspring_example\netty\img\Snipaste_2021-01-25_21-47-48.png)

NettyConfig @bean (配置线程启动Nettyserver)

````java
@Configuration
public class NettyConfig {
    @Bean
    public NettyServer createNettyServer() {
        NettyServer nettyServer = new NettyServer();
        //启动Netty服务，使用新的线程启动
        new Thread(){
            @Override
            public void run() {
               nettyServer.start(1234);
            }
        }.start();
        return nettyServer;
    }
}
````



1. SysNoticeListener   (判断用户在线 配置listener和发送消息)

2. ````java
   import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
   import com.tensquare.notice.netty.MyWebSocketHandler;
   import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
   //实施rabbit监听器
   public class SysNoticeListener implements ChannelAwareMessageListener（）{
         io.netty.channel.Channel wsChannel = MyWebSocketHandler.userChannelMap.get(userId);
   		  if (wsChannel != null)//WebSocket 判断链接不为空 之后发送消息
             {
                 wsChannel.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(result)));
             } 
   ````

   

3. UserNoticeListener (判断用户在线 配置listener和发送消息) 和SysNoticeListener  一样

4. rabbitconfig 配置 @Bean 

   ````java
       @Bean("sysNoticeContainer")
       public SimpleMessageListenerContainer createSys(ConnectionFactory connectionFactory) {
           SimpleMessageListenerContainer container =
                   new SimpleMessageListenerContainer(connectionFactory);
           //使用Channel
           container.setExposeListenerChannel(true);
           //设置自己编写的监听器
           container.setMessageListener(new SysNoticeListener());
           return container;
       }
   ````

   

 MyWebSocketHandler 调用Bean <

````java
    // 送Spring容器中获取消息监听器容器,处理订阅消息sysNotice
    SimpleMessageListenerContainer sysNoticeContainer = (SimpleMessageListenerContainer) ApplicationContextProvider.getApplicationContext()
            .getBean("sysNoticeContainer");
````



![Snipaste_2021-01-26_21-21-50](F:\projet\javaspring_example\netty\img\Snipaste_2021-01-26_21-21-50.png)

````xml
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:6868/eureka/
  instance:
    prefer-ip-address: true  跨域调用
````





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