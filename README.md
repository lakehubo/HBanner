# HBanner

非常感谢在issues里给我留言的小伙伴！还有很详细的bug复现描述，我会继续努力。目前部分朋友提出的视频切换图片闪烁的问题，经过一位伙伴[DiaosiDev](https://github.com/DiaosiDev)的耐心测试发现是由于CustomVideoView类的离屏渲染导致，经过对CustomVideoView的外部包裹一层ViewGroup解决了该问题，在此非常感谢这位朋友的反馈以及意见！还有部分朋友出现的视频黑屏只有声音的问题，也已经复现找到，是由于切换动画引起的bug，原有的图片换场方式已经不再适用视频图片混播切换，故只保留了经测试可正常使用的横向切换和纵向切换动画。

首先特别感谢原作者youth5201314的开源banner

作者开源项目地址https://github.com/youth5201314/banner

我在他的项目上做了一些更改，新增了上下切换动画，以及视频图片可以混合并且可自定义设置每个子视图显示的时间

如果控件对你有帮助，欢迎star，你的一个赞是我继续优化下去的动力

##### 目前已优化项

* 1.目前新增了在线视频缓存功能，当传入的地址对象为uri类型时，将对该地址的视频/图片是否下载到本地进行判断，默认会进行缓存并优先读取本地视频/图片进行轮播，若为本地视频/图片，请用string对象。
* 2.视频加载模式从饿汉式改为了懒汉式。
* 3.目前视频播放器采用的VideoView原生控件，所以支持的视频格式有限，推荐限定视频格式为mp4
* 4.新增图片视频子布局参数设置，居中，拉伸，垂直居中（1.0.1）
* 5.支持手滑切换页面（1.0.2）
* 6.优化标题的传入方式（1.0.2）
* 7.新增控件生命周期重启和暂停的方法（1.0.3）
* 8.新增缓存地址自定义，解决关闭缓存下网络图片无法显示问题（1.0.3）
* 9.降低sdk最低版本支持为16（1.0.4）
* 10.修复设置缓存地址后，无法正确读取缓存视频和图片的bug（1.0.4）
* 11.修改图片视频控件center和full布局设置的调用位置（1.0.4）
* 12.修复标题显示不正确bug（1.0.5）
* 13.修复部分视频网站无法下载缓存的问题（本版本下载https资源时，会绕过所有证书，信任所有https网站）（1.0.6）
* 14.修复单个视频无限轮播问题（1.0.7）
* 15.修复update崩溃bug（1.0.7）
* 16.修复list为0时，调用控件生命周期引起的崩溃问题（1.0.7）
* 17.拆分视频和图片的布局方式设置为setImageGravity以及setVideoGravity（1.0.8）
* 18.修复竖方向切换时，视频相邻图片黑屏闪烁问题（1.0.8）
* 19.去掉不适用的换场方法，原有的多个transform切换类对于有视频/图片混播不适用且会有视频黑屏bug存在，故只保留了横向以及纵向的切换动画，且不会导致视频黑屏（1.0.8）
* 20.新增page背景颜色设置方法（1.0.8）

## 使用方式

##### 权限说明

>在线视频的播放以及缓存需要相应的网络权限和存储器的读写权限，请在相应应用里添加以下权限，并动态申请

```xml
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

>Gradle 依赖添加方式

```typescript
    dependencies {
        implementation 'com.lakehubo:hbanner:1.0.8'
        ...
    }
    
```

>Maven 依赖添加方式

```xml
<dependency>
  <groupId>com.lakehubo</groupId>
  <artifactId>hbanner</artifactId>
  <version>1.0.8</version>
  <type>pom</type>
</dependency>
```

>简单使用hbanner

```java
    List<ViewItemBean> list = new ArrayList<>();
		
    banner.setViews(list)
                .setBannerAnimation(Transformer.Default)//换场方式
                .setBannerStyle(BannerStyle.CIRCLE_INDICATOR_TITLE)//指示器模式
                .setCache(true)//可以不用设置，默认为true
                .setCachePath(getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath() + File.separator + "hbanner")
                .setVideoGravity(VideoGravityType.CENTER)//视频布局方式
                .setImageGravity(ImageGravityType.FIT_XY)//图片布局方式
                .setPageBackgroundColor(Color.TRANSPARENT)//设置背景
                .setShowTitle(true)//是否显示标题
                .setViewPagerIsScroll(true)//是否支持手滑
                .start();
```

>ViewItemBean说明

```java
ViewItemBean(int type, Object url, int time)
int type;//子view类型
type = BannerConfig.IMAGE//图片类型
type = BannerConfig.VIDEO//视频类型
Object url;//资源地址
url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.default1);//app内固定视频资源
url = Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.mp4";//外部存储路径下视频/图片文件
url = R.mipmap.b1;//资源内图片
int time = 5 * 1000//5s	(单位ms)

//若要显示标题需要设置该view的标题
ViewItemBean(int type, String title, Object url, int time)
```

> 换场方式

```java
Transformer.Default//默认横向切换
Transformer.Vertical//纵向切换
setBannerAnimation(Transformer.Vertical);//纵向切换
```

> 缓存功能说明

若添加了网络地址的视频，必须开启缓存才能正常缓存视频文件到本地

```java
setCache(true)//可以不用设置，默认为true
setCachePath(String path)//设置缓存的文件地址  Android10以上的target需要使用应用内dir，Environment.getExternalStorageDirectory()在Android10已经无法正常获取权限和读写
```

> 指示器

若需要显示标题，需要设置带title的指示器模式

```java
BannerStyle.NOT_INDICATOR
BannerStyle.CIRCLE_INDICATOR
BannerStyle.NUM_INDICATOR
BannerStyle.NUM_INDICATOR_TITLE
BannerStyle.CIRCLE_INDICATOR_TITLE
BannerStyle.CIRCLE_INDICATOR_TITLE_INSIDE
```

> 子view的图像视频显示布局

最新的1.0.8版本已经分开了视频和图片的布局设置

```java
VideoGravityType.CENTER//居中
VideoGravityType.CENTER_HORIZONTAL//垂直居中
VideoGravityType.FULL_SCREEN//全屏
setVideoGravity(VideoGravityType.CENTER)//视频布局方式
  
ImageGravityType.MATRIX//以下均对应ImageView.ScaleType
ImageGravityType.FIT_XY
ImageGravityType.FIT_START
ImageGravityType.FIT_CENTER
ImageGravityType.FIT_END
ImageGravityType.CENTER
ImageGravityType.CENTER_CROP
ImageGravityType.CENTER_INSIDE
setImageGravity(ImageGravityType.FIT_XY)//图片布局方式
```

> 设置page背景

```java
setPageBackgroundColor(int Color)//颜色设置背景
setPageBackground(Drawable background)//drawable设置背景
setPageBackgroundResource(int resource)//使用资源设置背景
```

> hbanner对应生命周期调用对应方法，可优化hbanner的使用体验

```java
@Override
protected void onResume() {
    super.onResume();
    banner.onResume();
}

@Override
protected void onPause() {
    banner.onPause();
    super.onPause();
}

@Override
protected void onStop() {
    banner.onStop();
    super.onStop();
}
```



##### 版本1.0.1

>更新内容：

* 1.新增图片/视频居中或者拉伸布局设置

##### 版本1.0.2

>更新内容：

* 1.新增手势滑动切换 默认开始手势滑动模式，若不需要请手动设置为false
* 2.优化标题的传入方式

##### 版本1.0.3

>更新内容：

* 1.新增控件生命周期重启和暂停的方法
* 2.新增缓存地址自定义，解决关闭缓存下网络图片无法显示问题



##### 自定义图片加载器

* 关于使用自定义视频控件以及图片控件可以自行实现VideoViewLoaderInterface以及ViewLoaderInterface接口后，
  通过setVideoLoader(VideoViewLoaderInterface videoLoader)以及setImageLoader(ViewLoaderInterface imageLoader)
  方法实现替换

```java
/**
 * 例如自定义使用glide加载图片代理实现
 */
public class MyImageLoader implements ViewLoaderInterface<ImageView> {
    public ImageLoader() {
    }

    @Override
    public ImageView createView(Context context, int gravity) {
        ImageView imageView = new ImageView(context);
        switch (gravity) {
            case BannerGravity.CENTER:
                 imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                 break;
            case BannerGravity.FULL_SCREEN:
                 imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                 break;
        }
        return imageView;
    }

    @Override//view预加载时候调用，并非正在显示
    public void onPrepared(Context context, Object path, ImageView imageView, String cachePath) {
        //Glide 加载图片简单用法
        Glide.with(context).load(path).into(imageView);
    }

    @Override
    public void onDestroy(ImageView imageView) {
        System.gc();
    }
}

banner.setImageLoader(new MyImageLoader());

//替换视频控件同理  
```

##### 版本1.0.4

>更新内容：

* 1.降低sdk最低版本支持为16
* 2.修复设置缓存地址后，无法正确读取缓存视频和图片的bug
* 3.修改图片视频控件center和full布局设置的调用位置

##### 版本1.0.5

>更新内容：

* 1.修复标题显示顺序不对应bug

##### 版本1.0.6

>更新内容：

* 1.修复部分视频网站无法下载缓存的问题（本版本下载https资源时，会绕过所有证书，信任所有https网站，且在视频未完全缓存时，视频可能无法正常播放，当视频缓存完成，轮播到当前视频时，会正常播放！）

##### 版本1.0.7

>更新内容：

* 1.修复单个视频无限轮播问题
* 2.修复update崩溃bug（该版本尚遗留问题待解决，当update列表中第一个item为video类型时，操作update方法后，新一轮的轮播可能会出现首视频暂停不自动播放的问题，轮播一轮后会正常）
* 3.修复list为0时，调用控件生命周期引起的崩溃问题

```java
List<ViewItemBean> list = new ArrayList<>();
list.add(...)//add your new item
....
banner.update(list);//update it!
```

##### 版本1.0.8

>更新内容：

* 1.拆分视频和图片的布局方式设置为setImageGravity以及setVideoGravity
* 2.修复竖方向切换时，视频相邻图片黑屏闪烁问题
* 3.去掉不适用的换场方法，原有的多个transform切换类对于有视频/图片混播不适用且会有视频黑屏bug存在，故只保留了横向以及纵向的切换动画，且不会导致视频黑屏
* 4.新增page背景颜色设置方法
