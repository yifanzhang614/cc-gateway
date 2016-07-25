# cc-gateway
服务网关在当今服务化的背景下，还是很用的一个组件。一般来说很多公司选用高性能的nginx来实现，不过，这里是我实现的一个Java版的服务网关，至于原因，我会在后面介绍。该组件集成了Netflix开源的很多套件，来帮忙做服务的治理。

重新开发这个组件的原因：
1.nginx的开发使用的是C或者lua这样的语言，需要有专门这个技术栈的人


2.我们的主要系统语言是Java，如果对服务网关单独使用一种语言，不好做统一的控制和管理


3.依赖于Netflix的很多套件，我们可以非常方便的做负载均衡，服务化，监控和服务治理，如果使用nginx，需要开发的工作量太大，或者是我不了解这块有类似的开源解决方法


4.nginx的系统性能不用多说，单机肯定会高于cc-gateway，cc-gateway可用于性能不那么敏感，或者可以分布式部署多个，或者需要很多定制化的功能，而又不想或没条件基于nginx开发的情况


主要功能：
1.基于zuul的网关过滤服务，用来做策略、规则的动态的发布或回滚


2.基于archaius，来做配置的动态更新


3.基于ribbon实现网关对内部服务的负责均衡


4.基于Hystrix的服务访问控制和监控，同时对每个服务的参数可以定制


5.基于yaml的自定义服务访问配置，支持服务Id的抽象


    zuul: 
      ignoredPatterns:
        - /**/admin/**
      routes:
        first:
          path: /first/**
          url: http://httpbin.org/

        second:
          path: /second/**
          retryable: true
          #serviceId 和url只能取其一
          
          serviceId: service1

        third:
          path: /third/**
          retryable: true
          serviceId: service2
          
        legacy:
          path: /**
          url: forward:/error/
    services:
      service1:
        ribbon:
          listOfServers: http://httpbin.org, https://httpbin.org:443
          ConnectTimeout: 50000
          ReadTimeout: 50000
          MaxAutoRetries: 1
          MaxHttpConnectionsPerHost: 200
          MaxTotalHttpConnections: 500
          
      service2:
        ribbon:
          listOfServers: http://www.baidu.com, http://www.baidu.com
          ConnectTimeout: 50000
          ReadTimeout: 50000
          MaxAutoRetries: 1
          MaxHttpConnectionsPerHost: 200
          MaxTotalHttpConnections: 500

    
6.基于HttpClient的反向代理实现，后面可以替换为netty，提高性能
