#系统命令  
1.查询断点状态   
&ensp;&ensp;netstat /nao | findstr "端口号"  
2.根据PID查询应用程序名称  
&ensp;&ensp;tasklist -fi "pid eq 进程PID"    

#系统权限  
##横向越权  
&ensp;&ensp;攻击者尝试访问与攻击者拥有相同权限的用户资源
##纵向越权
&ensp;&ensp;低权限攻击者尝试访问高级别权限用户的资源

#对象序列化  
1.字段不参加序列化, @JsonIgnore  
2.若被注解的字段值为null，则序列化时忽略该字段, @JsonInclude(JsonInclude.Include.NON_NULL),
如果使用SpringBoot，还可以在Application.yml中配置
```
  spring： 
      jackson:
          default-property-inclusion: non_null  
```  

#本地缓存 - Guava  
1. initialCapacity 指的是记录的条数  
2. 如果没有命中数据，则调用如下方法  
```java  
private static LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
           .initialCapacity(1000)
           .maximumSize(10000)
           .expireAfterAccess(12, TimeUnit.HOURS)
//如果没有命中数据，则调用如下方法装载数据
           .build(new CacheLoader<String, String>() {
               @Override
               public String load(String s) throws Exception {
                   return null;
               }
//
            })
```

#中文字符乱码
1.检查web.xml中是否配置了过滤器,强制转换为UTF-8
```text
  <filter>
      <filter-name>characterEncodingFilter</filter-name>
      <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
      <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
      </init-param>
      <init-param>
        <param-name>forceEncoding</param-name>
        <param-value>true</param-value>
      </init-param>
  </filter>
  <filter-mapping>
      <filter-name>characterEncodingFilter</filter-name>
      <url-pattern>/*</url-pattern>
  </filter-mapping>
```  

2.修改tomcat服务器的配置文件 server.xml, 增加URIEncoding="UTF-8"
```xml
    <Connector URIEncoding="UTF-8" 
               port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
```

#JAVA小知识
1.类代码执行顺序  
&ensp;&ensp;静态代码块>普通代码块->构造函数
```java
    public class foo{
        static {
            //静态代码块
        }
    
        {
            //普通代码块
        }
    
        public foo() {
            //构造函数
        }
    }
```
2.BigDecimal的精度问题
重要的事情说3遍  
####&ensp;&ensp;一定要使用BigDecimal的字符串构造器，才可以保证进度  
####&ensp;&ensp;一定要使用BigDecimal的字符串构造器，才可以保证进度  
####&ensp;&ensp;一定要使用BigDecimal的字符串构造器，才可以保证进度  
```text
    BigDecimal b1 = new BigDecimal("0.06");
    BigDecimal b2 = new BigDecimal("0.02");
    System.out.println(b1.add(b2))
    //这里才能输出0.08, 而不是后面有许多数字
```
#PageHeper的使用步骤  
1.开始一个Page  
```text
    PageHelper.startPage(pageNumber, pageSize);
```
2.填充sql查询逻辑, 只有紧跟着startPage的第一个select方法会被分页
3.用PageInfo对结果进行包装
4.PageInfo的排序格式为 fieldName orderby, 切记中间有个空格

#路径
如果访问的URL是 http://localhost:8080/store/UserServlet?method=findByName
request.getServletPath() -> /UserServlet
request.getContextPath() -> /store
request.getRequestURI()  -> /store/UserServlet
request.getRequestURL()  -> http://localhost:8080/store/UserServlet
request.getRealPath("/") -> D:\apache-tomcat-6.0.13\webapps\WebDemo\

#文件上传
1.配置web-inf/dispatcher-servlet.xml
```xml
    <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
        <property name="maxUploadSize" value="10485760"/>
        <property name="maxInMemorySize" value="4096"/>
        <property name="defaultEncoding" value="UTF-8"/>
    </bean>
```
2.如果上传的是富文本，有特点返回格式, 并且要修改response的Header部分
&ensp;&ensp;以下是以simditor为目标返回的
```text
    {
        "success", true/false), #成功/失败
        "msg", "error message"  #选项
        "file_path", url        #文件路径
    }

    response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
```

#mybatis
1.插入时需要使用数据库产生的ID，使用useGeneratedKeys="true"
2.插入后需要返回插入的特定字段值，使用keyProperty="你要返回的字段名"

#转换二维码
1.参考https://github.com/MycroftWong/ZxingDemo

#支付
##支付宝
###参考文档  
1.沙箱登录 https://openhome.alipay.com/platform/appDaily.htm    
2.沙箱环境使用说明 https://docs.open.alipay.com/200/105311/  
3.如何使用沙箱环境 https://opensupport.alipay.com/support/knowCategory/20068    
4.当面付产品介绍 https://docs.open.alipay.com/194/105072  
5.扫码支付接入指引 https://docs.open.alipay.com/194/106078/  
6.当面付接入必读 https://docs.open.alipay.com/194/105322/  
7.当面付进阶功能 https://docs.open.alipay.com/194/105190/  
8.当面付的异步通知-仅用于扫码支付 https://docs.open.alipay.com/194/103296/  
9.当面付SDK&DEMO https://docs.open.alipay.com/194/105201/  
10.生成RSA秘钥 https://docs.open.alipay.com/291/106103/  
11.线上创建应用说明
###小细节
1.使用SDK接入是，两种签名方式  
A. 普通公钥方式  
B. 公钥证书方式  
如果使用普通公钥方式签名，不可以调用public DefaultAlipayClient(CertAlipayRequest certAlipayRequest)这个构造方法  
因为这样会导致构造器在读取根证书时为Null，产生一个异常

2.alipayClient.execute(request)返回的状态只需要判断10000和40004，如果10000，则subCode为空,  
所以可以直接调用response.isSuccess()判断是否调用成功, response.isSuccess判断的就是subCode为空  
  
  
##微信

#MAVEN进行环境隔离  
##步骤  
###1.在build节点中添加环境隔离牵涉到的资源 
```xml
    <resources>
      <resource>
          <!-- 此处为发布时各自的资源, 例如配置 -->
          <directory>src/main/resources.${deploy.type}</directory>
          <excludes>
            <!-- 排除全部的jsp文件 -->
            <exclude>*.jsp</exclude>
          </excludes>
      </resource>
      <resource>
          <!-- 此处为发布时公共的资源, 例如配置 -->
          <directory>src/main/resources</directory>
      </resource>
    </resources>
``` 
###2.在Project节点下，添加Profiles节点, 确定要发布的环境变量以及默认激活项目
```xml
  <profiles>
    <profile>
      <!-- 此处配置为dev发布环境 -->
      <id>dev</id>
      <activation>
        <!-- 开发环境为默认配置 -->
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <!-- 给上面resources->resource->${deploy.type}使用 -->
        <deploy.type>dev</deploy.type>
      </properties>
    </profile>

    <profile>
      <!-- 此处配置为测试发布环境 -->
      <id>test</id>
      <properties>
        <!-- 给上面resources->resource->${deploy.type}使用 -->
        <deploy.type>test</deploy.type>
      </properties>
    </profile>

    <profile>
      <!-- 此处配置为生产发布环境 -->
      <id>prod</id>
      <properties>
        <!-- 给上面resources->resource->${deploy.type}使用 -->
        <deploy.type>prod</deploy.type>
      </properties>
    </profile>
  </profiles>
```

#集群部署
##Tomcat集群部署
###原理
通过nginx负载均衡进行请求转发
###多集群部署的问题
1.Session登录信息存储以及读取的问题
####解决方法1.可以通过nginx ip hash policy，实现，保证每个用户走的是同一个服务器
&ensp;&ensp;优点：可以不改变当前架构,直接实现横向扩展  
&ensp;&ensp;缺点：1.导致服务器负载均衡不平均,完全依赖IPHASH
&ensp;&ensp;&ensp;&ensp;2.如果客户的网络环境不好，IP容易发生变化，将无法服务
####解决方法2.可以通过分布式redis实现一个分布式session以及分布式锁
2.服务器任务定时并发的问题
3.其他业务场景
###好处
1.可以提高服务性能，并发能力，以及高可用性  
2.提高项目的横项扩展能力
###部署方式  
1.单机部署多应用  
2.机部署多应用  
###单机部署多应用安装方法
####linux下安装
1.编辑/etc/profile文件,添加如下语句  
```text
vim /etc/profile
export CATALINA_1_HOME=/usr/local/tomcat85_1
export CATALINA_1_BASE=/usr/local/tomcat85_1
export TOMCAT_1_HOME=/usr/local/tomcat85_1
```
2.编辑Tomcat配置文件,添加如下语句
```text
vim catalina.sh
export CATALINA_HOME=$CATALINA_1_HOME   #与etc/profile里面饿定义一致即可
export CATALINA_BASE=$CATALINA_1_BASE   #与etc/profile里面饿定义一致即可
```
3.编辑服务器配置文件,修改如下语句
```text
vim server.xml
<Server port="端口不重复就可以" shutdown="SHUTDOWN">

<Connector port="端口不重复就可以" protocol="HTTP/1.1"
               connectionTimeout="20000"
               URIEncoding="UTF-8"          #防止中文乱码  
               redirectPort="8443" />

<Connector protocol="AJP/1.3"
              port="端口不重复就可以"
              redirectPort="8443" />
```
  
####windows下安装
1.设置系统变量,添加如下语句  
```text
CATALINA_1_HOME=c:\tomcat85_1
CATALINA_1_BASE=c:\tomcat85_1
CATALINA_1_TMPDIR=c:\tomcat85_1
```
2.编辑Tomcat配置文件,添加如下语句
```text
vim catalina.bat
echo Using CATALINA_BASE:   "%CATALINA_1_BASE%"
echo Using CATALINA_HOME:   "%CATALINA_1_HOME%"
echo Using CATALINA_TMPDIR: "%CATALINA_1_TMPDIR%"
```
3.编辑服务器配置文件,修改如下语句
```text
vim server.xml
<Server port="端口不重复就可以" shutdown="SHUTDOWN">

<Connector port="端口不重复就可以" protocol="HTTP/1.1"
               connectionTimeout="20000"
               URIEncoding="UTF-8"          #防止中文乱码  
               redirectPort="8443" />

<Connector protocol="AJP/1.3"
              port="端口不重复就可以"
              redirectPort="8443" />
```
###多机部署单应用安装方法
没有特别要求

##Nginx集群部署
###配置策略  
重点upstream后面的名字，必须与location/proxy_pass后的字符串一致

&ensp;&ensp;1.) 轮询(默认配置)  
&ensp;&ensp;&ensp;&ensp;优点： 实现简单
&ensp;&ensp;&ensp;&ensp;缺点: 不考虑每台机器的性能
```text
upstream www.foo.com{
    server www.foo.com:8080
    server www.foo.com:9080
}
```      
&ensp;&ensp;2.) 权重  
&ensp;&ensp;&ensp;&ensp;优点： 考虑了每台服务器的性能
```text
upstream www.foo.com{
    server www.foo.com:8080 weight = 15;
    server www.foo.com:9080 weight = 10;
}
访问8080端口的概率是访问9080端口的1.5倍
```      
&ensp;&ensp;3.) ip hash  
&ensp;&ensp;&ensp;&ensp;优点： 能实现一个客户访问同一台服务器，前提，用户ip地址访问全程不会改变
&ensp;&ensp;&ensp;&ensp;缺点: IP Hash不一定平均
```text
upstream www.foo.com{
    ip_hash;
    server www.foo.com:8080
    server www.foo.com:9080
}
```      
&ensp;&ensp;4.) url hash(第三方)
&ensp;&ensp;&ensp;&ensp;优点： 保证一个服务访问同一服务器
&ensp;&ensp;&ensp;&ensp;缺点: 根据urlhash分配，可能请求不平均，请求频繁的url会请求到同一个服务器上
```text
upstream www.foo.com{
    server www.foo.com:8080
    server www.foo.com:9080
    hash $requestURI
}
```      
&ensp;&ensp;5.) fair(第三方)
&ensp;&ensp;&ensp;&ensp;优点： 按照后端服务器的响应时间来分配请求，响应时间短的优先分配
```text
upstream www.foo.com{
    server www.foo.com:8080
    server www.foo.com:9080
    fair;
}
```      
###例子
```text
upstream www.foo.com{
    ip_hash;
    server www.foo.com:9080 down;           #down表示当前服务器不参加负载
    server www.foo.com:8080 weight=2;       #weight默认为1，weight值越大，负载权重越大    
    server www.foo.com:7080;
    server www.foo.com:6080 backup;         #其他所有非backup机器down或者忙时，请求backup机器
}
```

##Nginx+Tomcat运行集群  
1.启动n个Tomcat服务器  
2.非调试此步骤忽略, 修改浏览器所在计算机的host文件,这步是因为没有域名，本地修改模拟域名解析
3.启动Nginx

#Redis
##安装
###linux
1. sudo yum install tcl  #如果不安装，第6步骤的make test可能会出错
2. wget http://download.redis.io/releases/redis-2.8.0.tar.gz  
3. tar xzf redis-2.8.0.tar.gz  
4. cd redis-2.8.0/  
5. make 
6. make test            #测试是否安装成功

##redis服务/客户端启动
###./redis-server                       正常服务模式启动 port= 6379
    ./redis-cli                                 //客户端启动
    ./redis-cli shutdown                        //关闭服务
###./redis-server ../redis.conf         指定配置文件启动
    修改redis.conf文件中
    port=xxxx                                    //指定默认启动端口  
    requirepass password                         //指定密码
    ./redis-server ..redis.conf                  //指定配置文件启动服务
### ./redis-server --port 6380          指定端口启动 port= 6380
    ./redis-cli -p 6380                          //客户端指定端口启动
    ./redis-cli -p 6380 shutdown                 //关闭服务
### ./redis-cli -p 6379 -h 127.0.0.1    连接指定ip port的redis服务
    ./redis-cli -p 6379 -h 127.0.0.1 shutdown    //关闭指定ip port的redis服务
### ./redis-cli -p 6379 -a password     使用密码连接

##命令
### 基础命令  
    a. info                                         //系统信息
    b. select ${number}                             //选择DB
    c. flushdb                                      //清除当前选择的db数据
    d. flushall                                     //清除全部db数据
    e. ping                                         //回音，返回pong
    f. dbsize                                       //当前db大小
    g. save                                         //使redis数据持久化
    h. quit                                         //退出redis-cli连接
    i. clear                                        //清除屏幕
    j. monitor                                      //查看日志
### redis键命令
    a. keys *                                       //显示当前db中的全部键
    b. set key data                                 //设置一个键值对
       set test 测试数据
    c. del key                                      //删除一个键值对
       del test        
    d. exists key                                   //判断一个键值是否存在
       exists test                                  //存在返回1 不存在返回0
    e. ttl key                                      //time to level 查看键的剩余生存时间, 单位为秒, 如果返回值为-1，表示无过期时间, -2表示键不存在
       ttl test
    f. expire test time                             //设置键的生存时间，单位秒
       expire test 10  
    h. type key                                     //返回键的类型
    i. randomkey                                    //随机键
    j. rename oldKey newKey                         //把oldkey替换为newkey, 如果newKey存在于db中，则newKey会覆盖db中存在的键值
       rename oldTest newTest
    k. renameNX oldKey newKey                       //nx的命令，都带条件判断, 把oldkey替换为newkey,  如果newKey存在于db中，则rename不成功
       renameNX oldTest newTest
###string命令
    a. setex key sec value                          //setex(set expire)  时间单位为秒 
       setex c 100 c
    b. psetex key msec value                        //setex(set expire)  时间单位为毫秒 
       psetex d 10000 d
    c. getrange key start end                       //取value，从start开始 end结束 以0开始
       set country china
       getrange country 0 2                         //返回 chi
    d. getset key value                             //先get后set
       set a a
       setget a aa                                  //返回a
    e. mset key1 value1 key2 value2 key3 value3     //批量设置键值对
       mset a a1 b b1 c c1
    f. mget key1 key2 key3                          //批量取得多个值
       mget a b c
    g. setNx key value                              //先判断，如果Key存在于db中，则set不成功
    g. msetNx key value                             //批量设置 先判断，如果Key存在于db中，则set不成功 必须全部不存在（原子操作）
    i. strlen value                                 //返回key对应的value的长度
    j. incr key                                     //如果key对应的是数值，则把key对应的value加1
    k. decr key                                     //如果key对应的是数值，则把key对应的value减1
    l. incrby key step                              //如果key对应的是数值，则把key对应的value加step个
    m. decrby key step                              //如果key对应的是数值，则把key对应的value减step个
    n. append key appendValue                       //把key对应的value拼接上appendValue
###hash命令
    a. hset map key value                           //设置一个hash key是map 值是 key value的键值对
       hset map name wangjian
    b. hexists key key1                             //返回为key的hash中的key1是否存在
    b. hget key key1                                //返回为key的hash中的key1对应的值
    c. hgetall map                                  //返回全部的hash内的key和value
    d. hkeys key                                    //返回全部的key对应的hash中的key值
    e. hvals key                                    //返回全部的key对应的hash中的value值    
    f. hlen key                                     //返回key对应的hash中的数量
    g. hmget key key1 key2                          //返回key对应的hash中的key1 key2对应的值
    h. hmset key key1 value1 key2 value2            //设置key对应的hash中的key1 key2对应的 value1 value2
    i. hdel key key1 key2                           //删除key对应的hash中的key1 key2
    i. hsetnx key key1 value1                       //批量设置 先判断，如果Key对应的hash存在于db中，并且key1存在,  则set不成功（原子操作）
###list命令(允许出现重复值, 以stack方式存放，先放的在最后)
    a. lpush key value1 value2 value3 .. valuen     //批量设置名称为key的list中的value值
    b. llen key                                     //返回名称为key的list的长度
    c. lrange key start end                         //返回名称为key的list的单元从start开始到end结束的值 0为起始
    d. lset key pos value                           //设置名称为key的list的第pos个单元的值为value
    e. lindex key pos                               //返回名称为key的list的第pos个单元的值
    f. lpop key                                     //移除名称为key的list的第1个单元
    g. rpop key                                     //移除名称为key的list的最后1个单元
###set命令(不允许出现重复值)
    a. sadd key value1 value2 ... valuen            //批量添加名称为key的set中的value值, 如果value已经存在，不添加，不存在的value值会继续添加，不会报错
    b. scard key                                    //返回名称为key的set的数量
    c. smembers key                                 //返回名称为key的set的成员
    d. sdiff key1 key2                              //返回名称为key1的set对于key2的set的不同点
    e. sinter key1 key2                             //返回名称为key1的set和key2的set的交集
    f. sunion key1 key2                             //返回名称为key1的set和key2的set的并集
    g. srandomember key number                      //返回名称为key的set中随机Number个元素
    h. sismember key value                          //返回名称为key的set中, value是不是其成员元素   1存在，0不存在
    i. srem key value1, value2 .. valuen            //移除名称为key的set中, value1, value2, ... valuen
    j. spop key                                     //移除名称为key的set中的一个随机value，并返回改value
###card命令(有序，并且允许出现重复值， 数据以键值对方式存放)
    a. zadd key value1 key1 value2 key2 ... valuen keyn//批量添加名称为key的card中的key value
    b. zcard key                                    //返回名称为key的card中的元素数量
    c. zscore key key1                              //返回名称为key的card中的键值为key1的元素
    d. zcount key rang0 rang1                       //返回名称为key的card中的值的区间在rang0到rang1的元素
    d. zrank key key1                               //返回名称为key的card中的key值对应的位置索引值 以0起始
    e. zincrby key number key1                      //把名称为key的card中的key1值对应的value值+number
    f. zrange key index0 index1                     //返回名称为key的card中，index0-index1区间中元素的key
    f. zrange key index0 index1 withscores          //返回名称为key的card中，index0-index1区间中元素的key和value

##数据结构(5种)
###String(字符串)
###List(链表)
###Set(无序集合)
###Sort Set(有序集合)
###Hash(Hash表)