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

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果成功运行，可以尝试访问`http://yourIP:19080/Explorer/index.html`，会看到类似如下界面：

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-1-2.png" />
</center>

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

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-1-1.png" width="50%" height="50%" />
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;应用正常工作，我们接下来将其部署到 **Service Fabric** 上。

## 部署SpringBoot应用

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;环境已经切换到`Ubuntu`下，在`ciao-springboot-web`目录下，运行`yo azuresfguest`，会有类似对话模式内容出现。

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-1-3.png" />
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;填写的内容和上面的不一样，具体如下表：

|内容|值|含义|
|----|-----|-----|
|Name your application|CiaoSpringbootWeb|应用名|
|Name of the application service|WebRuntimeService|应用中提供的服务|
|Source folder of guest binary artifacts|target/|应用的可执行文件或者二进制内容|
|Relative path to guest binary in source folder|entryPoint.sh|应用的启动脚本，**Service Fabric** 会调用它来启动应用|
|Parameters to use when calling guest binary|不需要|启动参数|
|Number of instances of guest binary|1|实例数，这里类似k8s的复制控制器中定义的实例数|

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;运行完成之后会生成该应用的程序清单，它包含了应用名、服务名以及启动脚本和部署实例数等信息，混合应用与配置信息。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`entryPoint.sh`需要自己编写，进入到
`ciao-springboot-web/CiaoSpringbootWeb/CiaoSpringbootWeb/WebRuntimeServicePkg/code`目录下，创建`entryPoint.sh`，内容如下：

```sh
#!/bin/bash
BASEDIR=$(dirname $0)
cd $BASEDIR
java -jar ciao-springboot-web-0.1-SNAPSHOT.jar
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;内容很简单就是启动了当前程序，这里没有类似Docker镜像的方式去描述应用，而是采用一组松散、固定的目录以及配置对应用进行描述，但运行时都是以容器的方式进行运行，比如：Docker或者Hyper-V。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在`CiaoSpringbootWeb`目录下运行`sfctl cluster select --endpoint http://localhost:19080`，这个操作将会选择集群到对应的 **Service Fabric** 端点。然后直接运行`CiaoSpringbootWeb`目录下的`install.sh`，就会将应用部署到 **Service Fabric** 集群上。

```sh
$ ./install.sh
[1/31] files, ApplicationManifest.xml
[2/31] files, _.dir
[3/31] files, WebRuntimeServicePkg/ServiceManifest.xml
[4/31] files, WebRuntimeServicePkg/_.dir
[5/31] files, WebRuntimeServicePkg/config/Settings.xml
[6/31] files, WebRuntimeServicePkg/config/_.dir
[7/31] files, WebRuntimeServicePkg/code/ciao-springboot-web-0.1-SNAPSHOT.jar
[8/31] files, WebRuntimeServicePkg/code/entryPoint.sh
[9/31] files, WebRuntimeServicePkg/code/ciao-springboot-web-0.1-SNAPSHOT.jar.original
[10/31] files, WebRuntimeServicePkg/code/_.dir
[11/31] files, WebRuntimeServicePkg/code/maven-status/_.dir
[12/31] files, WebRuntimeServicePkg/code/maven-status/maven-compiler-plugin/_.dir
[13/31] files, WebRuntimeServicePkg/code/maven-status/maven-compiler-plugin/compile/_.dir
Complete
```
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到其过程是将整个`CiaoSpringbootWeb`目录都拷贝到集群上，然后再运行`entryPoint.sh`脚本加以执行，而程序的运行空间是在容器中分配。我们访问`http://localhost:8080/html/hello`，可以看到以下输出：

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-1-7.png" />
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以想象如果在内网或者线下环境搭建了 **Service Fabric** 集群，那么在开发者环境中也只需要执行以下`install.sh`就可以完成部署，下面将介绍如何将应用部署到`Azure`上。

## 将应用部署到Azure

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Service Fabric** 团队提供了免费限时的环境让大家体验，称之为 `Party Cluster`，它是免费、限时的服务托管集群，用来跑 **Service Fabric** 。只需要签署了协议，比如github账户等，就可以使用，部署的集群会运行一小时，然后自动销毁，后需要再使用就需要重新连接到一个新的集群。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;访问 `https://try.servicefabric.azure.co/`，通过`github`登录，就可以试用一下，类似如下界面：

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-1-4.png" width="50%" height="50%" />
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下载`PFX`，导入浏览器后就可以访问`Service Fabric Explorer`，笔者分配了一个美西的环境，有三个节点可以用，一小时后收回，再申请再提供，当然这些操作需要再做一遍。

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-1-5.png" />
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;既然`Azure`的endpoint已经有了，我们就可以在项目`CiaoSpringbootWeb`下执行``sfctl cluster select --endpoint http://Your-Connection-endpoint` ，待连接之后再执行`install.sh`脚本将应用部署到`Azure`上，但是笔者机器无法连接，原因不详，还需要微软修复完善。

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-1-6.png" />
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这个流程开发流程是值得借鉴和思考的，一致的开发部署体验，只是将部署的endpoint集群指向不同的位置，一致的运行部署体验减轻开发人员在不同环境切换的痛苦。普通开发人员在以往的云环境下，都是通过本地部署程序然后进行测试，当测试通过后通过构建工具打包成镜像，然后将镜像部署到测试集成或者线上环境，这天然的割裂开了开发到生产的过程，存在不一致性，而 **Service Fabric** 就标榜其一致的体验，不管在哪里。

## 小结

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; **Service Fabric** 提供了便捷的搭建方式，再次基础上通过`yo`等工具协助开发者将已有的应用转换成为能够被 **Service Fabric** 所识别的形式，并提供了对应的部署和退部署脚本，当用户指定 **Service Fabric** 的endpoint后，就可以将程序部署到其中，并且能够在`Explorer`中完成扩缩容，同时一样的操作模式可以将应用部署到`Azure`云上，完成生产环境部署。
