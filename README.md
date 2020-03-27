# HBanner

首先特别感谢原作者youth5201314的开源banner

作者开源项目地址https://github.com/youth5201314/banner

我在他的项目上做了一些更改，新增了上下切换动画，以及视频图片可以混合并且可自定义设置每个子视图显示的时间

##### 目前已优化项
* 1.目前新增了在线视频缓存功能，当传入的地址对象为uri类型时，将对该地址的视频/图片是否下载到本地进行判断，默认会进行缓存并优先读取本地视频/图片进行轮播，若为本地视频/图片，请用string对象。
* 2.视频加载模式从饿汉式改为了懒汉式。
* 3.目前视频播放器采用的VideoView原生控件，所以支持的视频格式有限，推荐限定视频格式为mp4
* 4.新增图片视频子布局参数设置，居中，拉伸，垂直居中（1.0.1）
* 5.支持手滑切换页面（1.0.2）
* 6.优化标题的传入方式（1.0.2）

## 使用方式

##### 权限说明
>在线视频的播放以及缓存需要相应的网络权限和存储器的读写权限，请在相应应用里添加以下权限，并动态申请

```xml
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
>Gradle 依赖添加方式
```xml
    dependencies {
        implementation 'com.lakehubo:hbanner:1.0.2'
        ...
    }
    
```
>Maven 依赖添加方式
```
<dependency>
  <groupId>com.lakehubo</groupId>
  <artifactId>hbanner</artifactId>
  <version>1.0.2</version>
  <type>pom</type>
</dependency>
```

>简单使用hbanner
```android
    List<ViewItemBean> list = new ArrayList<>();
    Uri path1 = Uri.parse("https://v-cdn.zjol.com.cn/123468.mp4");
    Uri path2 = Uri.parse("https://v-cdn.zjol.com.cn/276982.mp4");
    Uri imageUrl = Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1579170629919&di=fc03a214795a764b4094aba86775fb8f&imgtype=jpg&src=http%3A%2F%2Fimg4.imgtn.bdimg.com%2Fit%2Fu%3D4061015229%2C3374626956%26fm%3D214%26gp%3D0.jpg");
    list.add(new ViewItemBean(VIDEO, path1, 15 * 1000));
    list.add(new ViewItemBean(IMAGE, imageUrl, 2 * 1000));
    list.add(new ViewItemBean(VIDEO, path2, 15 * 1000));
    list.add(new ViewItemBean(IMAGE, R.mipmap.b1, 2 * 1000));
    list.add(new ViewItemBean(IMAGE, R.mipmap.b2, 2 * 1000));

    banner.setViews(list)
          .setBannerAnimation(Transformer.Default)
          .setBannerStyle(BannerStyle.NUM_INDICATOR)
          .setCache(true)//可以不用设置，默认为true
          .setViewGravity(BannerGravity.CENTER)
          .start();
```
##### 版本1.0.1
>更新内容：
* 1.新增图片/视频居中或者拉伸布局设置
```
BannerGravity.CENTER 居中
BannerGravity.CENTER_HORIZONTAL 垂直居中
BannerGravity.FULL_SCREEN 全屏
```
##### 版本1.0.2
>更新内容：
* 1.新增手势滑动切换 默认开始手势滑动模式，若不需要请手动设置为false
* 2.优化标题的传入方式
```
/**
 *新版本使用方式 当不需要显示标题时候，
 *可以按照旧的方式new ViewItemBean对象无需传入标题参数
 */
List<ViewItemBean> list = new ArrayList<>();
        Uri path1 = Uri.parse("https://v-cdn.zjol.com.cn/123468.mp4");
        Uri path2 = Uri.parse("https://v-cdn.zjol.com.cn/276982.mp4");
        Uri imageUrl = Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1579170629919&di=fc03a214795a764b4094aba86775fb8f&imgtype=jpg&src=http%3A%2F%2Fimg4.imgtn.bdimg.com%2Fit%2Fu%3D4061015229%2C3374626956%26fm%3D214%26gp%3D0.jpg");
        list.add(new ViewItemBean(VIDEO, "标题1",path1, 15 * 1000));
        list.add(new ViewItemBean(IMAGE, "标题2",imageUrl, 2 * 1000));
        list.add(new ViewItemBean(VIDEO, "标题3",path2, 15 * 1000));
        list.add(new ViewItemBean(IMAGE, "标题4",R.mipmap.b1, 2 * 1000));
        list.add(new ViewItemBean(IMAGE, "标题5",R.mipmap.b2, 2 * 1000));

        banner.setViews(list)
                .setBannerAnimation(Transformer.Default)
                .setBannerStyle(BannerStyle.CIRCLE_INDICATOR_TITLE)//带标题的指示器模式 若不显示标题请设置无标题的指示器模式
                .setCache(true)//可以不用设置，默认为true
                .setShowTitle(true)//显示标题 默认false
                .setViewGravity(BannerGravity.CENTER)
                .setViewPagerIsScroll(true)//支持手滑模式 默认为true
                .start();
```

* 关于使用自定义视频控件以及图片控件可以自行实现VideoViewLoaderInterface以及ViewLoaderInterface接口后，
通过setVideoLoader(VideoViewLoaderInterface videoLoader)以及setImageLoader(ViewLoaderInterface imageLoader)
方法实现替换
```
/**
 * 例如自定义使用glide加载图片代理实现
 */
public class MyImageLoader implements ViewLoaderInterface<ImageView> {
    private int gravity = BannerGravity.CENTER;

    public ImageLoader() {
    }

    public ImageLoader(int gravity) {
        this.gravity = gravity;
    }

    @Override
    public ImageView createView(Context context) {
        return new ImageView(context);
    }

    @Override//view预加载时候调用，并非正在显示
    public void onPrepared(Context context, Object path, ImageView imageView) {
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
