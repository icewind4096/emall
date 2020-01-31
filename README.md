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