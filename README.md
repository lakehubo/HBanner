

# HBanner（1.1.0-alpha）

全新自制改良版视频/图片混播控件。这次版本已经完全更换了实现方式。所以剔除了原来所参考的项目代码，旧版本与所参考的banner控件存在冲突的问题应该不再存在。

新版本目前所支持的功能：图片指定轮播时间，视频无需指定时间，播放结束后会自动切换，视频全屏/居中（原版本存在的视频有声音无显示问题可能是CustomVideoView全屏实现的方式所导致，具体问题可能是由于layout坐标错乱导致）



## 使用方式

##### 权限说明

>在线视频的播放以及缓存需要相应的网络权限和存储器的读写权限，请在相应应用里添加以下权限，并动态申请

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```



>Gradle 依赖添加方式

```groovy
dependencies {
  	implementation 'com.lakehubo:hbanner:1.1.0-alpha'
}
```



>Maven 依赖添加方式

```xml
<dependency>
  <groupId>com.lakehubo</groupId>
  <artifactId>hbanner</artifactId>
  <version>1.1.0-alpha</version>
  <type>pom</type>
</dependency>
```



#### HBanner主要接口方法说明

该接口提供主要的操作方法，根据传入的viewpager并对其进行轮播的实现。

| 方法名（参数）                              | 返回值     | 说明                                                         |
| ------------------------------------------- | ---------- | ------------------------------------------------------------ |
| sources(List\<SubView> subViews)            | void       | 轮播数据列表，在轮播状态下调用此方法会导致轮播暂停           |
| remove(int position);                       | void       | 移除指定位置的subview，在轮播状态下调用此方法会导致轮播暂停  |
| addSubView(SubView sub);                    | void       | 在结尾添加新的subview，在轮播状态下调用此方法会导致轮播暂停  |
| addSubView(int position, SubView sub);      | void       | 在指定位置添加subview，在轮播状态下调用此方法会导致轮播暂停  |
| pause(long timeout);                        | void       | 暂停轮播，timeout时间后自动继续，timeout为0表示永久暂停      |
| play(boolean auto);                         | void       | 开始轮播,若当前存在暂停则继续当前位置进行播放                |
| showNext(boolean auto);                     | void       | 手动切换到下一张view，auto是否后面自动播放                   |
| setPosition(int position);                  | void       | 重置当前播放位置                                             |
| getPosition();                              | void       | 获取当前播放位置                                             |
| getCurrentSubView();                        | SubView    | 获取当前显示的子view                                         |
| getSubView(int position);                   | SubView    | 获取指定位置的子view                                         |
| getBannerStatus();                          | PlayStatus | 获取banner当前的状态                                         |
| setTimeOffset(long time);                   | void       | 设置每次的轮播时间偏移，设置该值后会给所有的view加上该值，time单位ms |
| setSyncMode(SyncMode mode);                 | void       | 设置多banner的同步模式，目前仅支持一种one by one模式         |
| addSyncHBanner(HBanner hBanner);            | void       | 同步另一banner，多banner协同，根据传入的item序号进行同步，协同支持一种模式，<br>需要两个banner的item数量保持一致，否则会导致其中一个banner的item无法显示完整。 |
| addSyncHBanner(List\<HBanner> hBanners);    | void       | 参考以上，添加复数个banner                                   |
| removeSyncHBanner(HBanner hBanner);         | void       | 移除同步的banner                                             |
| removeSyncHBanner(List\<HBanner> hBanners); | void       | 参考以上，移除复数个banner                                   |
| removeAllSyncHBanner();                     | void       | 移除所有同步的banner                                         |
| create(ViewPager viewPager)                 | HBanner    | 利用viewPager创建HBanner接口。注：多次调用会创建多个HBanner实例。 |



##### SubView接口说明

该接口为传入HBanner轮播对象，目前控件自带一个图片对象和视频对象，你也可以自己继承该接口实现自己自定义的轮播对象。

| 方法名（参数）                                    | 返回值 | 说明                                                         |
| ------------------------------------------------- | ------ | ------------------------------------------------------------ |
| onShowStart(final HBanner hBanner, int position); | void   | 轮播开始显示的回调，该方法会返回HBanner对象，此时你可以在这里接管轮播控制，<br>比如暂停，并自行计时播放下一张等操作。当view为视频时，推荐自行控制视频显示时间。 |
| onShowFinish();                                   | void   | 轮播对象结束显示的回调。                                     |
| duration();                                       | long   | 指定当前轮播对象的显示的时间，单位：ms                       |
| getView();                                        | View   | HBanner获取轮播对象的接口。该返回值为控件轮播时所显示的View  |
| getPreView();                                     | View   | 当前view不为ImageView时候，比如为VideoView，则需要覆盖此方法，<br>返回一张图片替代VideoView的显示，该方法主要为了视频未加载完全时<br>候的显示以及循环播放的首尾画面的过度。 |
| getTag();                                         | String | 同步所用的tag 标记，该接口只有在多banner协同下才有用。目前版本无任何作用。 |



目前控件内实现了默认的图片轮播对象：ImageSubView

以及默认的视频轮播对象：VideoSubView

##### ImageSubView简单说明

* 通过起内部Builder类进行参数初始化和构造。

* ```java
  ImageSubView imge = new ImageSubView.Builder(getBaseContext())
                  		.resId(R.mipmap.b2)
                  		.duration(5000)
                  		.build();
  ```

* Builder构造类方法参数说明

| 方法                         | 参数类型  | 说明                                    |
| ---------------------------- | --------- | --------------------------------------- |
| gravity(ScaleType scaleType) | ScaleType | 图片布局方式（ImageView.ScaleType）     |
| url(String url)              | String    | 图片来源的网络地址                      |
| file(File file)              | File      | 图片来源的文件                          |
| resId(@DrawableRes int id)   | int       | 图片来源的资源id（R.mipmap/R.drawable） |
| duration(long show)          | long      | 图片显示时间（单位ms，默认值5000）      |



##### VideoSubView简单说明

* 通过起内部Builder类进行参数初始化和构造。

* ```java
  VideoSubView view = new VideoSubView.Builder(getBaseContext())
                  		.url("https://v-cdn.zjol.com.cn/123468.mp4")
                  		.Gravity(VideoViewType.FULL)
                  		.isSub(false)
                  		.build();
  ```

* Builder构造类方法参数说明

| 方法                        | 参数类型      | 说明                                            |
| --------------------------- | ------------- | ----------------------------------------------- |
| gravity(VideoViewType type) | VideoViewType | 视频布局方式，目前支持两种FULL和CENTER          |
| isSub(boolean sub)          | boolean       | 是否属于被同步banner中的视频控件，默认值为false |
| file(File file)             | File          | 视频来源的文件                                  |
| url(String url)             | String        | 视频来源的网络地址                              |

> 注：file和url必须有其中一个有效值，否则会抛出参数错误异常



##### 自带换场方式的对象函数

| 函数名                  | 说明     |
| ----------------------- | -------- |
| DefaultTransformer      | 横向切换 |
| VerticalPageTransformer | 纵向切换 |

ViewPager函数设置轮播换场方式

```java
//自行通过设置ViewPager的换场动画方法即可，无需通过控件。
//设置viewpager切换方式
viewPager.setPageTransformer(true, new VerticalPageTransformer());
```



对应生命周期回调中的控件推荐设置

```java
@Override
protected void onResume() {
    if (hBanner != null)
        hBanner.play(true);
    super.onResume();
}

@Override
protected void onPause() {
    if (hBanner != null)
        hBanner.pause(0);
    super.onPause();
}

@Override
protected void onStop() {
    if (hBanner != null)
        hBanner = null;
    super.onStop();
}
```



新版banner，目前去掉了指示器标题栏，推荐自行实现需要的指示器和标题等。

##### 使用示例代码(项目demo)：

 ```java
/**
 * 在create banner前需要确保viewpager控件已经被创建
 * 这里是双viewpager，为了方便所以直接对根布局进行视图创建
 * 进行回调
 */
HBanner hBanner = HBanner.create(viewPager);

List<SubView> data = new ArrayList<>();
data.add(new ImageSubView.Builder(getBaseContext())
         .url("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4148675854,1608370142&fm=26&gp=0.jpg")
         .duration(6000)
         .build());
data.add(new ImageSubView.Builder(getBaseContext())
         .resId(R.mipmap.b2)
         .duration(5000)
         .build());
data.add(new ImageSubView.Builder(getBaseContext())
         .resId(R.mipmap.b3)
         .duration(5000)
         .build());
data.add(new VideoSubView.Builder(getBaseContext())
         .url("https://v-cdn.zjol.com.cn/123468.mp4")
         .gravity(VideoViewType.FULL)
         .isSub(false)
         .build());

hBanner.sources(data);
//设置viewpager切换方式
viewPager.setPageTransformer(true, new VerticalPageTransformer());
      
//开始显示或者自动播放
hBanner.play(true);
 ```



##### 如果需要**多banner同步**轮播

需要保证所有banner都使用包内所提供的**BannerViewPager**（如果是单一banner，使用ViewPager即可）。

```java
HBanner hBanner = HBanner.create(viewPager);

List<SubView> data = new ArrayList<>();
data.add(new ImageSubView.Builder(getBaseContext())
         .url("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4148675854,1608370142&fm=26&gp=0.jpg")
         .duration(6000)
         .build());
data.add(new ImageSubView.Builder(getBaseContext())
         .resId(R.mipmap.b2)
         .duration(5000)
         .build());
data.add(new ImageSubView.Builder(getBaseContext())
         .resId(R.mipmap.b3)
         .duration(5000)
         .build());
data.add(new VideoSubView.Builder(getBaseContext())
         .url("https://v-cdn.zjol.com.cn/123468.mp4")
         .gravity(VideoViewType.FULL)
         .isSub(false)
         .build());

hBanner.sources(data);

HBanner hBanner2 = HBanner.create(viewPager2);

List<SubView> data2 = new ArrayList<>();
//被同步banner无需设置时间
data2.add(new ImageSubView.Builder(getBaseContext())
          .url("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1607078748657&di=32e2fa257aa53426f8ab1fbcb43e1325&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20180629%2Fd444958986894a6994533eda59edb460.jpeg")
          .build());
data2.add(new ImageSubView.Builder(getBaseContext())
          .resId(R.mipmap.b2)
          .build());
data2.add(new ImageSubView.Builder(getBaseContext())
          .resId(R.mipmap.b3)
          .build());
data2.add(new VideoSubView.Builder(getBaseContext())
          .file(new File(Environment.getExternalStorageDirectory() + "/default1.mp4"))
          .isSub(true)
          .build());

hBanner2.sources(data2);

hBanner.addSyncHBanner(hBanner2);
//多协同，只需主banner播放即可，其他banner会跟随切换。
hBanner.play(true);
```

