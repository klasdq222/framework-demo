# 项目描述

```txt
framework-demo:
    common:整个项目常用的接口、对象、数据类
        enums:枚举对象
        pojo:普通对象
        utils:工具包

    domain:模型对象
        bean:与数据表映射的javabean

    persistence:持久层，与数据库交互，为service层提供数据
        dao:数据库访问对象，提供访问数据库接口
        dto:数据传输对象，从数据库中拿到的数据对象
        mybatis:mybatis配置、插件、常用数据类
            conf:mybatis配置类
            enums:mybatis需要使用的枚举类型
            intercepts:mybatis拦截器
        qo:查询对象

    service:服务层，为controller提供服务接口
        impl:服务实现接口

    web:web层
        conf:web层配置类
        controller:控制层，与页面进行交互
        vo:控制层与页面交互数据对象

resource/mybatis/
    /mappers/ mapper映射文件
```