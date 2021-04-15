# VERSION 2.2

1. socket使用channel方式，交互使用DMA策略

2. 监听端口、控制端口获得新连接后使用子线程方式进行异步操作

3. 优化了http路由算法，提高了路由选择时的速度

4. passWay使用了nio策略，统一交付NioHallows处理

5. ServerListenThread（当前长连接存在问题，故而目前使用bio形式获取）、ClientServiceThread使用了nio策略，统一交付NioHallows处理

6. 增加了CountWaitLatch（对标CountDownLatch，但增加的countUp，不再依赖初始化数量），用于NioHallows的等待注册过程

7. 修改pom.xml，去除无用的包，改为dependencyManagement方式进行版本管理，避免和引入项目发生版本冲突

# VERSION 2.1

支持http路由了，可以像nginx那样监听同一个端口，根据访问域名路由目标应用

注意http标准，需要头部有Host，并且注意换行严格要符合http的标准，且如果是长连接路由目标只会在第一次连接时选择目标

虽然上面有很多要求，但是一般使用标准的浏览器都不会出现问题的