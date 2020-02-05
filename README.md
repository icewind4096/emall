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

#JAVA小知识
1.类代码执行顺序  
&ensp;&ensp;静态代码块>普通代码块->构造函数
```java
    public foo{
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
```java
    BigDecimal b1 = new BigDecimal("0.06");
    BigDecimal b2 = new BigDecimal("0.02");
    System.out.println(b1.add(b2))
    //这里才能输出0.08, 而不是后面有许多数字
```
#PageHeper的使用步骤  
1.开始一个Page  
```java
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
```java
    <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
        <property name="maxUploadSize" value="10485760"/>
        <property name="maxInMemorySize" value="4096"/>
        <property name="defaultEncoding" value="UTF-8"/>
    </bean>
```
2.如果上传的是富文本，有特点返回格式, 并且要修改response的Header部分
&ensp;&ensp;以下是以simditor为目标返回的
```java
    {
        "success", true/false), #成功/失败
        "msg", "error message"  #选项
        "file_path", url        #文件路径
    }

    response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
```
