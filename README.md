ZPanoramaView
---

*单个全景图展示*  
![单个全景图](./1.gif)

*全景图列表展示*  
![全景图列表](./2.gif)

##### 使用说明

```
dependencies {
  ...
  //当前版本 1.0.1，可持续关注 github 上的 release 版本
  implementation "com.github.androidZzT:VRPanoramaView:1.0.1" 
}
```

##### 支持功能

1.工程中提供 demo，展示了单个全景图和全景图列表的简单用法
   * 单个全景图像使用普通的控件一样即可
   * 全景图列表需要根据需求决定一页最多显示几个，然后手动复用控件，否则控件对象过多会造成OOM


2.控件提供的方法

> 1.提供图片url，加载渲染全景图 #setBitmapUrl(String url)  
> 2.恢复初始角度 #reCenter()  
> 3.开关陀螺仪 #setGyroTrackingEnabled(boolean enable)

后续还会增加手势拖拽全景图功能

