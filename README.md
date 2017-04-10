# android_component
this is my own component for android.

### 目录结构说明

  * 'module'

    具体的组件模块,其是一个完整的Android工程,具体结构包括

    * app,一般用来测试组件功能的默认模块

    * library,核心组件模块，可以直接maven打包上传(尽量避免添加多余的内容在其中)

  * common_android_library.gradle

    公共的Android Library组件工程构建配置文件,引用方式：`apply from: '../../common_android_library.gradle'`

  * common_repo.gradle

    maven仓库gradle配置文件，引用方式：`apply from: '../../common_repo.gradle'`

  * gradle.properties

    基础参数配置文件，放于组件(library)目录下，并修改成对应的配置

### 注意事项

  * 如果只单独运行下载测试某一个library，注意要将公共的２个配置文件引入，并且注意**`apply from: 'xxx'`的路径**
