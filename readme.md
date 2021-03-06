### PushLayer(也可以支持基础IM)

#### 推送层的设计

0.独立进程单独管理 还能减少单进程内存占用
1.进程保活
    1.1进程优先级
    1.2降低进程的内存占用 减少被MemoryKiller杀掉的风险
    1.3
2.网络层设计 弱网情况 高延迟情况
3.传输加密支持
4.最重要的 心跳机制
5.鉴权
6.封包解包工作


#### 中间层设计

1.进程间数据通信 Binder
2.多进程安全
3.数据存储 Storage(DB..) Cache HttpCache

#### 应用层设计

1.代理
2.接口设计
3.订阅通知设计
4.回调
5.后台服务 监听push消息


#### 三方库借鉴

1.网易云信InstantMessage

网络连接管理
登录认证服务
消息传输服务
消息存储服务
消息检索服务
群组管理/聊天室/会话服务
用户资料、好友关系托管服务
智能对话机器人服务

#### 后续扩展

1.后台电量消耗 数据监控 采集和优化
2.消息的到达率


#### 底层原理


1.长连接？
2.协议格式 优点或者缺点


#### SDK
```groovy
compile "com.mpaas.aar:common:10.1.20_adapter"
compile "com.mpaas.aar:rpc:10.1.20.4"
compile 'com.mpaas.aar:logging:10.1.32.0'
compile 'com.mpaas.aar:monitor:10.1.32.0'
compile 'com.mpaas.aar:pushsdk-build:10.1.32.2'
```

公共抽象层
RPC层
日志层
LoggerFactory日志工厂封装了不同类型的日志类
初始化时需要注意多线程并发

监控层
ClientMonitor
初始化时使用MonitorFactoryBinder工厂进行多方面监控的初始化

PUSHSDK
1.首先收集用户 手机 和包信息
2.启动推送进程服务 监听推送



#### 附录

https://www.cnblogs.com/lixiansheng/p/11359922.html
https://www.jianshu.com/p/98f3a09124bd