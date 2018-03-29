# Service Fabric 生命周期与运行状况
https://docs.microsoft.com/zh-cn/azure/service-fabric/service-fabric-application-lifecycle
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

## 开发、部署和测试

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在开发阶段，服务开发人员使用 `Reliable Actors` 或 `Reliable Services` 编程模型开发不同类型的服务。服务开发人员以声明的方式描述包含一个或多个代码、配置和数据包的服务清单文件中的开发服务类型，随后，应用程序开发人员构建使用不同服务类型的应用程序。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;应用程序开发人员以声明的方式，通过引用构成服务的服务清单并相应地重写并参数化构成服务的不同配置与部署设置，描述了应用程序清单中的应用程序类型。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;当应用开发完成，并且按照 **Service Fabric** 部署格式描述完成后，就可以通过`sfctl`工具或者运维SDK提供的API将应用部署到 **Service Fabric** 集群。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;应用部署到本地开发群集或测试群集后，服务开发人员使用 `FailoverTestScenarioParameters` 和 `FailoverTestScenario` 类或 `Invoke ServiceFabricFailoverTestScenario cmdlet` 运行内置的故障转移测试方案。故障转移测试方案在重要转换和故障转移中运行指定的服务，以确保其仍然可用并正在工作。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;然后，服务开发者使用 `ChaosTestScenarioParameters` 和 `ChaosTestScenario` 类或 `Invoke-ServiceFabricChaosTestScenario cmdlet`，运行内置的 `Chaos` 测试方案。 任意混合测试方案会将多个节点、代码包和副本错误包括到群集中。类似Netflix的ChaosMonkey，这些分散的工具都被整合到了 **Service Fabric** 的工具链中。

## 运行状况监视
