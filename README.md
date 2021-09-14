# DaceMiraiBot
适用于Mirai框架的可拓展Java插件 \~功能由你自己决定\~

# 注意
  本插件已停止开发，目前正在开发新的插件框架，不再依赖于MiraiOK

# 如何运作
通过加载json文件获取功能列表及功能触发关键字（正则），通过反射来运行目标类的run方法从而实现自定义

# 主插件与功能的通信
通过Map将用到的信息进行传递，目前代码仅包括消息本身、发送者QQ、发送者昵称\群名片。

功能的返回值可以是:

    String（只能进行简单的MiraiCode解析）
    
    Map（主消息key为"message"）,目前支持图片，使用[image]替换图片位置，在key为"image"中添加File、URL或其List
        注意：若list中对象过多会舍弃多余对象
        
# 关于自动回复
使用json进行存储，位于插件文件夹中

    Mirai\plugins\DaceMiraiBot\Reply.json
    
json结构为Map<String, List<String>>

json举例：
```json
{
    "纸片人":[
        "真的有人喜欢那东西吗，好蠢哦",
        "噫！好宅哦"
    ],
    "@一般路过鲮鱼子":[
        "at？"
    ]
}
```

# 关于功能json
json名为functionList.json，位于插件文件夹中

    Mirai\plugins\DaceMiraiBot\functionList.json

json结构为[FunctionBase](https://github.com/Grand-cocoa/DaceMiraiBot/blob/master/src/main/java/com/dace/base/FunctionBase.java "com.dace.base.FunctionBase")的集合


```Java
class FunctionBase { 
  String functionName; 
  String functionKey;
} //com.dace.base.FunctionBase
```

其中functionName为全名  举例"com.dace.base.FunctionBase"

json举例：
```json
[
  {
    "functionName" : "com.help.AllHelp",
    "functionKey" : "^help.*"
  }, 
  {
    "functionName" : "autoReply.Add", 
    "functionKey" : "^[!！]add .*[:：].*"
  }
]
```


json和功能类都将从插件的文件夹中读取
    
    Mirai\plugins\DaceMiraiBot


