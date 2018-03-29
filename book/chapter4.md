# Service Fabric 部署结构

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在部署环节中，我们创建了`ApplicationManifest.xml`用来描述应用，`ServiceManifest.xml`用来描述服务，这些究竟指的什么，下面会详细的介绍。

## 部署结构

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;每个 **Service Fabric** 应用都是需要固定的部署结构，其中的资源结构如下图所示：

<center>
<img src="https://github.com/weipeng2k/service-fabric-guide/raw/master/resource/chapter-4-1.png" />
</center>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;对应的部署目录如下：

```sh
install.sh <== 安装脚本，能够将应用安装到service fabric集群
uninstall.sh <==  卸载脚本，从service fabric中将应用进行卸载
ApplicationProject
        |— ApplicationManifest.xml <== 应用程序类型是分配给服务类型集合的名称/版本，声明依赖或者暴露的服务
        |— XxxServicePkg <== 服务包目录
                |— ServiceManifest.xml <== 服务类型是分配给服务的代码包、数据包、配置包的名称/版本，在同级目录中会有代码与配置
                |— code/ <== 代码所在的位置，包括运行的脚本
                |— config/ <== 配置所在位置
                |- data/ <== 数据
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下面看一下`ApplicationManifest.xml`中声明的内容，我们以`ciao-springboot-web`项目为例：

```xml
?xml version="1.0" encoding="utf-8"?>
<ApplicationManifest  ApplicationTypeName="CiaoSpringbootWebType" ApplicationTypeVersion="1.0.0"
                      xmlns="http://schemas.microsoft.com/2011/01/fabric" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

   <ServiceManifestImport>
      <ServiceManifestRef ServiceManifestName="WebRuntimeServicePkg" ServiceManifestVersion="1.0.0" />
   </ServiceManifestImport>

   <DefaultServices>
      <Service Name="WebRuntimeService">
         <StatelessService ServiceTypeName="WebRuntimeServiceType" InstanceCount="1">
            <SingletonPartition />
         </StatelessService>
      </Service>
   </DefaultServices>

</ApplicationManifest>
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到文件定义了应用类型`CiaoSpringbootWebType`，而这个应用会具备一个服务`WebRuntimeServicePkg`，这里只是定义了一个资源位置，和服务相关的内容需要到`WebRuntimeServicePkg`这个目录中去找寻， **Service Fabric** 会完成这个过程。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`WebRuntimeService`定义了服务类型名，以及服务实例数，目前是1个，接下来是服务的定义，`ServiceManifest.xml`中定义如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<ServiceManifest Name="WebRuntimeServicePkg" Version="1.0.0"
                 xmlns="http://schemas.microsoft.com/2011/01/fabric" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" >
   <ServiceTypes>
      <StatelessServiceType ServiceTypeName="WebRuntimeServiceType" UseImplicitHost="true">
   </StatelessServiceType>
   </ServiceTypes>

   <CodePackage Name="code" Version="1.0.0">
      <EntryPoint>
         <ExeHost>
            <Program>entryPoint.sh</Program>
            <Arguments></Arguments>
            <WorkingFolder>CodePackage</WorkingFolder>
         </ExeHost>
      </EntryPoint>
   </CodePackage>
 </ServiceManifest>
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上述配置描述了服务的代码在`code`目录下，而入口是`entryPoint.sh`， **Service Fabric** 会运行该脚本启动服务，一个应用中的多个服务在部署上物理位置是相互独立的。

## 部署运行时

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;将应用程序包复制到映像存储后，可以通过指定应用程序包的应用程序类型（使用其名称/版本）在群集内创建应用程序的实例。 将为每个应用程序类型实例分配一个如下所示的 URI 名称：`fabric:/MyNamedApp`。 应用程序包下的内容会上传到 **Service Fabric** 集群中，并命名成为`fabric:/appname`，这个是一组服务概念上的集合，上述例子中应用就是：`fabric:/CiaoSpringbootWeb`。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;创建命名应用程序后，可以通过指定服务类型（使用其名称/版本），在群集中创建应用程序服务类型（命名服务）之一的实例。 需为每个服务类型实例分配一个 URI 名称，该名称归并到实例的命名应用程序的 URI 之下。例如，如果在命名应用程序“MyNamedApp”中创建命名服务“MyDatabase”，则 URI 将类似于：`fabric:/MyNamedApp/MyDatabase`，上述例子中服务就是：`fabric:/CiaoSpringbootWeb/WebRuntimeService`。
