# Service Fabric 基本概念

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在一个简单的示例演示之后，接下来我们介绍 **Service Fabric** 的基本概念，它首先是一个分布式平台，打包、部署和伸缩微服务应用，以支持Cloud-Native来设计的，能够从本机服务成长到包含数千节点的大规模服务，并且为应用提供了全面的运行时和生命周期管理。下面是 **Service Fabric** 的架构图：

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-2-1.png" />
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;从虚拟机移动到容器可能使密度出现数量级增长。 同样，如果从容器迁移到这些容器中的微服务，也可能会出现另一个密度数量级。例如：单个 Azure SQL 数据库群集包含数百台计算机，这些计算机运行数以万计的容器，这些容器总共托管数十万个数据库。每个数据库都是一个 **Service Fabric** 有状态微服务。这里有个类比： **Service Fabric** 中提到的容器可能应对于 k8s 中的 Pod，在其中会运行多个容器实例，它们共享Pod的资源，因此能够提供比容器更加细粒度的控制力。而一个 **Service Fabric** 服务，就是一个细粒度资源。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;从架构图上可以看出 **Service Fabric** 兑现了平台无关的特性，回忆之前的例子，我们可以将它部署到任意公有云（专有云）或者私有云中，使用它一致性带来的好处，而它也必须解决从开发到部署的全部问题，这也是 **Service Fabric** 的核心所在。之前介绍的`yo`工具的应用生成，这个是最先导的工作，在此之后还会有以下内容需要解决。

## 编程模型支持

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Service Fabric** 提供了对多种编程模型的支持，主要有以下的编程模型。

|编程模型|使用SDK|介绍|
|-----|-----|-----|
|来宾可执行文件|否|类似之前的SpringBoot示例，它不直接调用 **Service Fabric** SDK API。 但是，它们仍受益于平台提供的功能，如服务可发现性、自定义运行状况和负载报告（通过调用 **Service Fabric** 公开的 REST API）。它们还具有完整的应用程序生命周期支持|
|容器|否|跑类似Docker的示例，用户交付的是镜像，但是生命周期管理还是支持的|
|Services|是|是一个用于编写与 **Service Fabric** 平台集成的服务的轻型框架，并且受益于完整的平台功能集。SDK 提供最小 API 集合，该集合允许 **Service Fabric** 运行时管理服务的生命周期，以及允许服务与运行时进行交互|
|ASP.NET|是|跑类似Docker的示例，用户交付的是镜像，但是生命周期管理还是支持的|
|Actors|是|在Services的基础上构建，是根据执行组件设计模式实现虚拟执行组件模式的应用程序框架。Actor框架使用称为执行组件的单线程执行的独立的计算单元和状态|

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;从这些编程模型可以看出，来宾可执行文件或者容器是将已有的系统部署到 **Service Fabric** 集群上，利用了它的集群管理能力，做到伸缩。而只有使用了 **Service Fabric** SDK API 后的应用，或者说依赖了客户端API后的应用（与 **Service Fabric** 产生技术绑定）将会获得额外的能力，诸如：服务发现，服务与 **Service Fabric** 集群交互等。而`Actors`更像是`Amazon lambda`一样，类似一种serverless的解决方案。

## 环境适应性

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;支持所有环境，Azure，本地，linux或者其他云，SDK中的开发环境与生产环境完全相同，都不涉及模拟器。 也就是说，在本地开发群集上运行的内容会部署到其他环境中的群集。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;就像示例中演示的，只要 **Service Fabric** 可以部署的地方，就能够提供一致的使用、部署和开发体验。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;目前 **Service Fabric** 提供了主流操作系统的安装和部署，用户可以轻易的搭建起来，这点是 k8s 等已有平台不具备，至少从便捷程度上来讲，如果搭建过k8s，哪怕是minikube，都会知道这是个麻烦的事情，除了跟GFW作斗争还需要和不同组件的版本兼容性做斗争。

## 应用生命周期

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;与其他平台一样，**Service Fabric** 上的应用程序通常将经历以下几个阶段：设计、开发、测试、部署、升级、维护和删除。 **Service Fabric** 为云应用程序的整个应用程序生命周期提供le 支持：从开发到部署、到日常管理和维护，再到最终卸载删除。服务模型使多个不同角色可以独立参与到应用程序生命周期中，但是不同开发人员、应用配置管理人员以及运维人员所操作的系统都是一致的。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以应用的开发和部署为例，在开发态，开发人员依赖 **Service Fabric** SDK 来进行编程，该 SDK 提供了多语言版本，支持 `Java`、`Python`、`C#`、`Go`、`PHP`以及`Node.js`这些版本。开发人员根据自己合适的语言体系，选择SDK开发服务，并将其部署到各个环境。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在部署阶段，操作人员可以使用`install.sh` 脚本，或者类似如下命令来完成部署：

```sh
sfctl application upload --path CiaoSpringbootWeb --show-progress
sfctl application provision --application-type-build-path CiaoSpringbootWeb
sfctl application create --app-name fabric:/CiaoSpringbootWeb --app-type CiaoSpringbootWebType --app-version 1.0.0
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;更让人激动的是， **Service Fabric** 将部署、运维和监控等特性都以API的形式暴露出来，并提供了与SDK对等的运维SDK，同样它们也是支持多语言的，以`Java`为例，在依赖了`sf`运维SDK后，可以通过如下代码完成应用的上传工作。

```java
system.fabric.ApplicationManagementClient.copyApplicationPackage(null, "CiaoSpringbootWeb", null);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这就意味着运维层面的开放，有条件的用户将可以依赖这些运维SDK打造适合于自身的应用生命周期以及运维系统。

## 自动伸缩

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在环境适应性的基础上，基于容器技术，提供了编程模型（当然不限）为应用的构建和开发提供更好的支持，在此基础上对微服务应用的生命周期提供了全阶段的支持，保证应用运行。提供了内置的工具对程序进行度量和监控，以及应用自动的伸缩支持。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在`Explorer`中可以通过点击服务的扩缩容按钮来完成服务的伸缩控制。

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-2-2.png" />
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以上操作用运维SDK也可以轻松完成，下面是将服务扩容到3个实例。

```java
StatelessServiceUpdateDescription updateDescription = new StatelessServiceUpdateDescription();
updateDescription.setReplicaOrInstanceCount(3);
system.fabric.FabricClient.getServiceManager().updateService(new Uri("fabric:/CiaoSpringbootWeb/WebRuntimeService"), updateDescription);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;一致的体验不仅是在开发过程中，在运维过程中 **Service Fabric** 也提供了SDK帮助搭建一致的体验。

## 不限于此

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Service Fabric** 认为分布式的可用性是关键，因此还提供了对单点故障的测试工具，用来帮助开发人员验证当前的应用能否提供高可用的服务。当开发人员将应用部署到本地开发群集或测试群集后，服务开发人员使用 `FailoverTestScenarioParameters` 和 `FailoverTestScenario` 类或 `Invoke ServiceFabricFailoverTestScenario cmdlet` 命令行工具运行内置的故障转移测试方案。故障转移测试方案在重要转换和故障转移中运行指定的服务，以确保其仍然可用并正在工作。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;然后，服务开发者使用 `ChaosTestScenarioParameters` 和 `ChaosTestScenario` 类或 `Invoke-ServiceFabricChaosTestScenario cmdlet`，运行内置的 Chaos 测试方案。任意混合测试方案会将多个节点、代码包和副本错误包括到群集中。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这些工具同样是集成在SDK中，有条件的使用者可以利用它们，将其集成到自有系统中。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;总之，通过使用 **Service Fabric** ，可以：
* 部署到 Azure 或部署到运行 Windows 或 Linux 的本地数据中心，而无需改变任何代码。只需编写一次，即可部署到 **Service Fabric** 群集的任意位置
* 使用 **Service Fabric** 编程模型、容器或任意代码，开发由微服务组成的可缩放应用程序
* 开发高度可靠的无状态和有状态微服务。使用有状态微服务，简化应用程序设计
* 使用新 Reliable Actors 编程模型，创建具有独立式代码和状态的云对象
* 部署和安排容器，包括 Windows 容器和 Linux 容器
* 几秒内就可以高密度部署应用程序，即每台计算机部署数百或数千个应用程序或容器
* 同时部署各种不同版本的相同应用程序，且可以单独升级每个应用程序
* 无需停机，即可管理应用程序生命周期，包括重大升级和非重大升级
* 缩放群集中的节点数。 缩放节点数的同时，应用程序也会随之自动缩放
* 监视并诊断应用程序的运行状况，并设置策略以执行自动修复
* 观察资源均衡器如何跨群集安排重新分发应用程序
