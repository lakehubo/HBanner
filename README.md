# HBanner

首先特别感谢原作者youth5201314的开源banner

作者开源项目地址https://github.com/youth5201314/banner

我在他的项目上做了一些更改，新增了上下切换动画，以及视频图片可以混合并且可自定义设置每个子视图显示的时间

##### 目前已优化项
>1.目前新增了在线视频缓存功能，当传入的地址对象为uri类型时，将对该地址的视频是否下载到本地进行判断，默认会进行缓存并优先读取本地视频进行轮播，若为本地视频，请用string对象。
2.视频加载模式从饿汉式改为了懒汉式。
3.目前视频播放器采用的VideoView原生控件，所以支持的视频格式有限，推荐限定视频格式为mp4

## 使用方式

##### 权限说明
>在线视频的播放以及缓存需要相应的网络权限和存储器的读写权限，请在相应应用里添加以下权限，并动态申请

```xml
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

```android
   List<ViewItemBean> list = new ArrayList<>();
   Uri path1 = Uri.parse("https://v-cdn.zjol.com.cn/280443.mp4");
   Uri path2 = Uri.parse("https://v-cdn.zjol.com.cn/276982.mp4");
   list.add(new ViewItemBean(VIDEO, path1, 15 * 1000));
   list.add(new ViewItemBean(IMAGE, R.mipmap.b3, 2 * 1000));
   list.add(new ViewItemBean(VIDEO, path2, 15 * 1000));
   list.add(new ViewItemBean(IMAGE, R.mipmap.b1, 2 * 1000));
   list.add(new ViewItemBean(IMAGE, R.mipmap.b2, 2 * 1000));

   banner.setViews(list)
         .setBannerAnimation(DefaultTransformer.class)
         .setBannerStyle(BannerStyle.NUM_INDICATOR)
         .setCache(true)//可以不用设置，默认为true 目前只做了视频缓存
         .start();
```
