# Service Fabric 深入实践

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;之前SpringBoot的例子是展示编程模型中的来宾文件模式，下面将会使用`sf-services`来构造基于 **Service Fabric** API的应用。在介绍之前首先说明一下，目前 **Service Fabric** 对SpringBoot应用支持是存在问题的，客户端应用在生成代理时，会走到 **Service Fabric** 构造的classloader中，而它进行写类文件，在处理SpringBoot这种Jar-in-Jar的场景就存在问题，会出现类型找不到。

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-3-1.png"/>
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;因此Java的部署都需要用开放目录部署，目前 **Service Fabric** 官方的Java示例都存在问题，在最新的 **Service Fabric** 上是无法工作的。由于Demo编写时间都在几个月之前，目录结构也比较奇怪，看了一下都是三哥的作品，写的很烂，而且关注度非常低，所以有问题也没有人反馈。

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-3-2.png"/>
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;其中有状态的服务是无法跑起来的，因为要加入`so`包的支持，当加入后，一旦运行触发到有状态服务的发布，就会直接虚拟机崩溃。

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-3-3.png"/>
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;因此下面的例子是以无状态服务来演示的，首先定义了接口层`ciao-vote-api`，然后接口的实现`ciao-vote-service`和展示投票结果的`ciao-vote-web`。

## 定义API

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以下内容可以在`ciao-vote-api`中找到，首先要依赖 **Service Fabric** SDK，从坐标名看，其实现在都是预览版。

```xml
<dependency>
    <groupId>com.microsoft.servicefabric</groupId>
    <artifactId>sf-actors</artifactId>
    <version>1.0.0-preview2</version>
    <scope>compile</scope>
</dependency>
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;定义服务，需要继承自`Service`。

```java
package com.murdock.examples.servicefabric.service;

import microsoft.servicefabric.services.remoting.Service;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * @author weipeng2k 2018年03月27日 下午13:05:58
 */
public interface VoteRPC extends Service {

    /**
     * 返回投票列表
     *
     * @return
     */
    CompletableFuture<HashMap<String, String>> getList();

    CompletableFuture<Integer> addItem(String itemToAdd);

    CompletableFuture<Integer> removeItem(String itemToRemove);
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这一点可以算作 **Service Fabric** 的侵入性吧，主要是服务在订阅或者发布过程中需要做一些处理，总之一个API jar包就可以构造出来了，然后`mvn clean install` 安装到本地。

## 实现服务端

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以下内容可以在`ciao-vote-service`中找到，前面说过由于不能支持Jar-In-Jar的场景，所以只能用`maven-jar-plugins`来打包，最终生成在target目录下，如下所示。

```sh
$ pwd
/Users/weipeng2k/Documents/workspace/service-fabric-guide/ciao-vote-service/target/release
$ ls
lib       start.jar
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;运行`java -jar start.jar`就可以启动程序，在启动之前我们还需要看一下服务提供方该如何编程，首先服务的实现需要继承`StatelessService`。

```java
package com.murdock.examples.servicefabric.service;

import microsoft.servicefabric.services.runtime.StatelessService;

/**
 * @author weipeng2k 2018年03月27日 下午13:44:19
 */
public class VoteServiceImpl extends StatelessService implements VoteRPC {
    // 略
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到服务需要继承自`StatelessService`，我们看一下它的超类`StatelessServiceBase`具备的成员变量。

```java
public abstract class StatelessServiceBase {
    private Map<String, String> addresses = new HashMap();
    private StatelessServiceContext context;
    private StatelessServicePartition partition;
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;其中`StatelessServiceContext`可以获取到部署的节点、配置以及当前实例的traceId等系统信息，而`StatelessServicePartition`可以汇报健康状况、load以及自定义的Metrics。也就是说只要继承了 **Service Fabric** 提供的 SDK，就可以获取到部署信息，并且能够通过父类提供的方法完成信息汇报、分布式跟踪的埋点，方便做到应用的运行时、监控、跟踪等信息的集成工作，并且提供一致的体验。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;启动类很简单，就是一个`main`完成了服务的注册。

```java
public class VoteApplication {

    private static final Logger log = LoggerFactory.getLogger(VoteApplication.class.getName());

    public static void main(String[] args) throws Exception {
        try {
            ServiceRuntime.registerStatelessServiceAsync("VoteServiceType",
                    (context) -> new VoteServiceImpl(), Duration.ofSeconds(10));
            log.info("Registered stateless service of type DataServiceType");
            Thread.sleep(Long.MAX_VALUE);
        } catch (Throwable ex) {
            log.warn("Exception occurred", ex);
            throw ex;
        }
    }
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到 **Service Fabric** 提供了现有服务框架很多不具备的考量，它通过平台一致性，将资源、分布式跟踪、信息汇报以及监控等信息以服务的方式统一提供给了应用开发，让其能够方便的使用，虽然有了平台绑定的嫌疑，但是可以看到这是一个很有吸引力的特性。

## 部署服务端

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以查看`ciao-vote-service`下的`CiaoVoteService`中的配置以及脚本，这里需要强调两个点。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;第一，不要期望能够在本地跑起来，因为纵使你加入了所有依赖的so包，也最终会报一个`jFrabricRuntime.so`之类的包找不到，如果看到这个错误，恭喜你，你可以在 **Service Fabric** 上部署起来了。至于这些so包哪里能够得到，可以下载`sf-native.jar`，解压缩可以得到4个，如果还是不全可以尝试在 **Service Fabric** 的安装目录上找，网上是搜不到的。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;第二，脚本需要加入`java.library.path`定义，这点不要遗漏，否则也跑不起来，可以看一下`entryPoint.sh`中的定义。

```sh
#!/bin/bash
BASEDIR=$(dirname $0)
cd $BASEDIR
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$(pwd)/lib
echo $LD_LIBRARY_PATH
java -Djava.library.path=$LD_LIBRARY_PATH -jar start.jar
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;运行`install.sh`，可以将服务安装到 **Service Fabric**。

## 实现客户端

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以下内容可以在`ciao-vote-web`中找到，对于消费端来说订阅了服务就可以发起调用，这和大多数框架类似，以下是部分代码。

```java
import com.murdock.examples.servicefabric.service.VoteRPC;
import microsoft.servicefabric.services.remoting.client.FabricServiceProxyFactory;

/**
 * @author weipeng2k 2018年03月25日 下午17:35:44
 */
public class WebSpringApplication {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/votes", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) {
                try {
                    FabricServiceProxyFactory fabricServiceProxyFactory = new FabricServiceProxyFactory();
                    VoteRPC serviceProxy = fabricServiceProxyFactory.createServiceProxy(VoteRPC.class,
                            new URI("fabric:/CiaoVoteService/VoteService"));

                    CompletableFuture<HashMap<String, String>> list = serviceProxy.getList();
                    HashMap<String, String> stringStringHashMap = list.get();

                    // 略
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
      }
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这里可以看到`fabricServiceProxyFactory.createServiceProxy(VoteRPC.class, new URI("fabric:/CiaoVoteService/VoteService"))`创建了服务的代理，而服务是通过URI来描述的，这里是：`fabric:/CiaoVoteService/VoteService`，其中`CiaoVoteService`是应用名，而`VoteService`是服务名。

## 部署客户端

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;部署客户端的过程与部署服务端类似，具体的内容可以在`ciao-vote-web`中的`CiaoVoteWeb`中找到。

> 关于so包的具体个数，在最后章节有提到。
