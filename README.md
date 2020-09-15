# DaceMiraiBot
适用于Mirai框架的可拓展Java插件 \~功能由你自己决定\~

# 如何运作
通过加载json文件获取功能列表及功能触发关键字（正则），通过反射来运行目标类的run方法从而实现自定义

# 主插件与功能的通信
通过Map将用到的信息进行传递，目前代码仅包括消息本身、发送者QQ、发送者昵称\群名片。

# 关于json
json结构为[FunctionBase](https://github.com/Grand-cocoa/DaceMiraiBot/blob/master/src/main/java/com/dace/base/FunctionBase.java "com.dace.base.FunctionBase")的集合

```Java
class FunctionBase { 
  String functionName; 
  String functionKey;
} //com.dace.base.FunctionBase
```

其中functionName为全名  举例"com.dace.base.FunctionBase"
json举例
```Json
[
  {
    functionName : "com.help.AllHelp",
    functionKey : "^help.*"
  }, 
  {
    functionName : "com.util.AtAll",
    functionKey : "atall"
  }
]
```

json和功能类都将从插件的文件夹中读取
    
    Mirai\plugins\DaceMiraiBot


