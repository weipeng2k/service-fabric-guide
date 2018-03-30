# Service Fabric 与 K8S

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Service Fabric** 这个分布式平台让人很自然的联想到了K8S，那么它们能够对比吗？答案是肯定的，而且 **Service Fabric** 的范畴更大。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在reddit.com上有人这么形容：

> Service Fabric is really about microservices and managing a distributed instance quorums, the framework/runtime provides the 'fabric' that holds everything together.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到 **Service Fabric** 不仅是一个构建微服务和管理分布式应用的平台，而且它从框架和运行时给予了开发、运维、测试以及配置人员一致的体验，做到真正的一站式解决方案，这些是之前的分布式平台所不能涵盖的，但是它也只是观察到了业界的工作，作为后发者，进行了融合补充，在说说它的优点和缺点之前，先主观的做个判断， **Service Fabric** 的一些概念会作为嫁衣让开源届尤其是CNCF中的一些项目所补充和学习，而自己只能向 Windows Phone一样，叫好不叫座，最终沉寂下去。因为从它糟糕的Demo和全范围铺开的功能，以微软一家只能去骗骗一些转型中的传统企业，留存一些.net用户，而自身却无力提升各方面的深度，最终巨大的平台发展只能越来越慢。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;更多可以参考：https://www.reddit.com/r/AZURE/comments/7oaq2e/what_are_the_differences_between_kubernetes_and/

## 优点

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;从示例中可以看到 **Service Fabric** 对开发考虑的非常周到，优点主要有以下：

|项目|描述|
|-----|-----|
|多语言支持|从构建服务或者运维工具的编写，提供多种语言的客户端，不干预用户的技术栈选型|
|项目构建工具|提供`yo`等工具，吸纳外部用户的同时能够方便的将项目迁移到 **Service Fabric** 所要求的部署结构|
|一致的体验|从开发、部署、测试以及扩缩容提供了一致的体验，有`Explorer`可以使用，也提供了对应的运维API方便接入，同时从本地部署到云上没有差异|
|免费的云上环境|提供了 Party Cluster 的方式，让用户体验 `Azure` 部署|
|运维工具平台化|部署、测试以及升级这些涉及到运维相关的内容都提供了SDK，方便用户进行定制|
|测试以及高可用|在测试环节整合了chaosmonkey之类的工具，能够让用户进行故障演练，这个是一个亮点|
|整合能力|在有状态服务中可以整合`Azure`其他的服务，形成合力，避免各个产品弱相关|
|有状态服务|这个编程模型是一个结合了数据分片的场景，类似互联网公司的主备、容灾或者单元化解决方案，在解决小体量的前提下，将部署高可用也考虑到位了|
|服务体现资源|用SDK编写服务时，能够从依赖的API中，让用户能够拿到部署的信息、traceId等资源信息，方便用户整合已有的技术栈|
|服务状况监控|在SDK中提供健康检查以及Metrics的收集入口，一致性的展示给用户|

## 缺点

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;说了一些好的地方，来讲讲它的不足之处，这些不足之处比较偏主观，包括：

|项目|描述|
|-----|-----|
|对SpringBoot支持不友好|Reliable Service Java版本在生成接口时采用的类生成技术在处理Jar-in-Jar的形式上存在问题，出现类找不到，也就是对来宾可执行文件支持较弱|
|本地无法运行服务|需要依赖.so文件，如下图所示，而且要使用特定的脚本将其加入到java.lib中，本机是无法运行的，只有在 **Service Fabric** 上才可以|
|调试Debug困难|程序的Debug困难，日志输出目录较为诡异|
|服务卸载容易出错|服务会时不时出现无法卸载的情况，这个在开发态很让人恼火，当然采用重新安装 **Service Fabric** 可以解决，否则非常鬼畜|
|SDK文档很弱|由于SDK实质没有开放源码，所以只能通过使用文档来查找，但是根据doc很难知道怎样调用，前文中的Health就是笔者在保有最后的兴趣下作的尝试，否则就放弃了|
|SDK API很乱|API分层很乱，组织的不好|

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-7-1.png" />
</center>
