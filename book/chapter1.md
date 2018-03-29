# Service Fabric 快速一瞥

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Service Fabric** 在`2018年3月19日`的这一天，微软选择将其开源，微软称呼它为分布式系统平台，我们接下来看看这个分布式系统平台能做些什么。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;首先 **Service Fabric** 定位是一个“分布式系统平台”，意思就是说，它是用来写其他的分布式系统的，比如可以很容易写个Zookeeper、分布式Redis或者分布式数据库，更重要的是能够编写分布式服务。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;其次，它在协助开发人员编写分布式服务的基础上，还包办了打包、部署和伸缩微服务应用，并且提供了编程模型以支持Cloud-Native来设计微服务的，进而支撑从本机服务成长到包含数千节点的大规模服务。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;最后 **Service Fabric** 为应用提供了全面的运行时和生命周期管理，不仅如此还提供了面向运维的开放接口，能够使第三方使用这些接口去定制化自身的运维工具，以 **Service Fabric** 为核心打造不同平台下一致的开发运维体验。

> 目前从开源 **Service Fabric** 的文档来看，中文存在机翻的嫌疑<br>Java 客户端大部分的source包是没有提供的，在没有doc的帮助下很难理解<br>在后面的示例中会依赖so包，这些内容也没有真实开源，而微软的官方说法是预览

## 本地安装 **Service Fabric**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Service Fabric** 是平台无关的分布式系统平台，因此可以运行在大多数系统上，目前可以部署的系统主要包括`Ubuntu`、`Mac`和`Windows7以上`环境，`Mac`下安装会有一些限制，因此选择在`Ubuntu`环境下部署使用。

> `Mac`上主要采用Docker镜像方式驱动 **Service Fabric** ， 更偏向开发测试，笔者更希望登录系统直接命令操纵 **Service Fabric**，因此以下安装环境均在`Ubuntu 16.04`下运行

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;微软提供了一个脚本，可以设置安装软件源并安装 **Service Fabric** ， 该脚本用于连同 `sfctl CLI` 一起安装 **Service Fabric** 运行时和 **Service Fabric** 通用 SDK。 在以下部分中运行手动安装步骤，以确定正在安装的组件以及同意的许可证。 运行该脚本即认为你同意所要安装的所有软件的许可条款。

```sh
sudo curl -s https://raw.githubusercontent.com/Azure/service-fabric-scripts-and-templates/master/scripts/SetupServiceFabric/SetupServiceFabric.sh | sudo bash
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;安装完成后，运行群集安装程序脚本。

```sh
sudo /opt/microsoft/sdk/servicefabric/common/clustersetup/devclustersetup.sh
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在执行这个命令之后， **Service Fabric** 运行时将会作为系统服务并在系统启动后自动运行。通过`sudo service servicefabric restart`可以完成运行时重启，这点在其出现问题时可以有效解决。

> 微软的产品秉承了出现问题通过重启可以解决的优良传统，笔者在部署一个有状态服务出现问题时，始终无法删除该应用，当通过重新安装和重启后，得以修复

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Service Fabric** 对应用的部署结构有要求，因此需要安装一些工具协助生成这些文件，这个过程实际类似 `spring initializer` 或者 `jboss-forge`，任何云厂商都有一个高效简洁的应用部署搭建工具，将用户的应用能够适配到自己云上的部署环境。

```sh
sudo apt-get install npm
sudo apt install nodejs-legacy
sudo npm install -g yo
sudo npm install -g generator-azuresfcontainer
sudo npm install -g generator-azuresfguest
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;安装生成器后，应该可以分别运行 `yo azuresfguest` 或 `yo azuresfcontainer`，创建部署工程或容器服务。到现在为止，我们只会使用到`yo azuresfguest`。

## 一个简单的SpringBoot应用

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以下示例可以在`ciao-springboot-web`中找到，`ciao`是意大利语中打招呼的意思，中文音近似 **桥**。`ciao-springboot-web`和 **Service Fabric** 没有任何关系，但是我们将它尝试部署到其中，体会一下 **Service Fabric**。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;创建`HelloController`，访问该url直接输出本机的信息，包括启动的时间，这个我们可以在多实例部署后看到不同的数值。

```java
@Controller
@RequestMapping("/html")
public class HelloController {

    @RequestMapping("/hello")
    public String hello(Model model) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();

            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            long startTime = runtimeMXBean.getStartTime();
            long uptime = runtimeMXBean.getUptime();

            model.addAttribute("ip", localHost.getHostAddress());
            model.addAttribute("startTime", startTime);
            model.addAttribute("uptime", uptime);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return "template/hello";
    }
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;页面模板（部分）如下所示，使用`thymeleaf`做展示。

```html
<!--/*@thymesVar id="ip" type="java.lang.String"*/-->
<table>
    <tr>
        <th>Key</th>
        <th>Value</th>
    </tr>
    <tr>
        <td>localhost ip</td>
        <td><p th:text="${ip}"></p></td>
    </tr>
    <tr>
        <td>start time</td>
        <td><p th:text="${startTime}"></p></td>
    </tr>
    <tr>
        <td>uptime</td>
        <td><p th:text="${uptime}"></p></td>
    </tr>
</table>
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;运行`mvn clean package`后，在`target`目录下执行`java -jar ciao-springboot-web-0.1-SNAPSHOT.jar`，应用正常启动，访问`http://localhost:8080/html/hello`，输出如下：

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;应用正常工作，我们接下来将其部署到 **Service Fabric** 上。

## 部署SpringBoot应用

## 将应用部署到Azure
