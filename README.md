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
//如果没有命中数据，则调用如下方法
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
```xml
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