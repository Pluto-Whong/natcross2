# natcross2
内网穿透工具

*********************

## natcross是做什么的？
- 需要自己提供硬件支持、部署的内网穿透工具
- 提供TCP协议类型的内网穿透服务，包括但不限于http(s)、数据库连接、ssh等协议
- 支持https协议转http协议与应用交互方式
- 支持无加密、控制端口加密交互、数据加密交互方式
- 主要服务场景，需要将内网的应用开放到公网，如微信小程序开发调试、支付回调等
- 支持HTTP根据host进行反向代理；目标依然是内网应用，只是可以根据HTTP协议header中的host字段区分选择目标应用（注意：只是有人提出来了HTTP监听统一端口并用域名访问的问题，并且有做的价值才补充的该功能；没做负载功能，这个是内网穿透，不是nginx，更不建议直接用在生产上，需要负载的可以自己去实现）

## 打包使用

服务端-ServerApp打包：
```
修改ServerApp.java中serviceIp为公网服务器的IP

mvn clean compile package -PserverApp
```
客户端-ClientApp打包：
```
mvn clean compile package -PclientApp
```

## 安装
1. 需要一台可在公网访问的机器，并将控制端口（默认 servicePort 10010）、数据端口（如tomcat，开放公网 listenPort 8081端口，外网连接时要对应数据端口8081）开放出来
2. 将ServerApp.jar包放在公网可访问的机器并运行，打包前需要修改serviceIp为公网服务器的IP
3. 将clientApp为入口进行运行，destIp:destPort为要开放的内网应用，如本地的tomcat（127.0.0.1:8080）
4. 用浏览器访问serviceIp:listenPort便可以访问到内网的tomcat

## 参数解释
ServerApp：

|字段|解释|
|:-:|:-|
|serviceIp|公网服务器的IP|
|servicePort|服务端的控制端口，主要用来与客户端进行指令交互|
|listenPort|服务端的监听端口，也就是部署完成后，在外网访问 serviceIp:listenPort 的方式对内网应用进行访问|
|aesKey|交互密钥key，保证数据的秘密性，可以查看 SecretInteractiveModel.java 中的fullMessage和checkAutograph中确认密钥的使用方式。<br>如果你使用了secretAll方式进行部署，这个key还是数据加密的key，可以在 SecretPassway.java 中确认密钥的使用方式|
|tokenKey|交互签名key，签名同aesKey|
|sslKeyStorePath|ssl证书的路径，默认方法只支持pkcs12的证书格式，使用这个证书可以做到https协议转http协议|
|sslKeyStorePassword|证书密码|
|createServerSocket|创建socket的方式，主要针对普通socket和sslSocket的方式进行封装，结合ssl证书使用|

ClientApp:

|字段|解释|
|:-:|:-|
|destIp|要开放到公网的内网应用所在机器的IP|
|destPort|要开放到公网的内网应用的端口|
|serviceIp|复用ServerApp的字段，所以要保证部署包和内网包的参数一致|
|servicePort|复用ServerApp的字段，所以要保证部署包和内网包的参数一致|
|listenPort|复用ServerApp的字段，所以要保证部署包和内网包的参数一致|
|aesKey|复用ServerApp的字段，所以要保证部署包和内网包的参数一致|
|tokenKey|复用ServerApp的字段，所以要保证部署包和内网包的参数一致|

## 内网穿透思路

因NAT网内CLIENT可以正常连接到SERVER端，并且能够保持一段时间的长连接，则由CLIENT发起连接，建立SOCKET对，在SERVER收到外部请求时，可以通过已经建立好的SOCKET将数据传输给CLIENT，CLIENT使用相同的方式将数据发送给指定的网络程序，网络程序回发数据后则按原路返回给请求方。
![时序图](./doc/sequence.svg)