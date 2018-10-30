<img src="https://github.com/ExplosiveBattery/zhimingdi-android/blob/master/README/%E6%A4%8D%E5%90%8D%E5%9C%B0.jpg?raw=true" width="200">  ![language](https://img.shields.io/badge/language-java-blue.svg?longCache=true&language=java) ![buildIDE](https://img.shields.io/badge/build-android--studio3-green.svg?longCache=true&build=android-studio3) ![license](https://img.shields.io/badge/license-apache2.0-green.svg?longCache=true&license=apache2.0)


### 起源
一个大学生创新创业建环学院省级项目，5人团队，4美工（建环那边要画各种图）+我一个代码，同时负责IOS与Android端开发。  
抱着了解学习一下移动端开发的想法，我应了这份差事。
### 功能
1. 有着日历App的功能，类似于每日故宫App的方式来展示一些手绘植物图片，借此达到科普植物知识的目的。每一天的日历页都以一幅植物图片或者植物图片的一部分为背景，点击日历页，会进入完整的植物大图，通过拖动和缩放来查看当天展示的植物，支持收藏图片，显示图片对应详细信息（我按钮已经放好了，但是她们还没有给我内容）。
2. 最后一页为附加功能页，提供植物地图，事情记录（一般事情记录，生日记录），一般样子日历的功能
植物地图功能，可以显示当期位置周边已记录的植物情况，开着地图移动时，地图上会记录你的“脚印”（移动路径）
提供一般的日历是为了能够满足用户想要查看指定日期所记录的事情，或者指定日期的农历等需求  

下面的视频里面还缺了几个按钮图片，比如说收藏图片用的爱心按钮等.....我就随便用两张图片来表示收藏与取消收藏的变化了
[![Watch the video](https://raw.github.com/GabLeRoux/WebMole/master/ressources/WebMole_Youtube_Video.png)](https://youtu.be/NgTxy20sC5I)

### 收获
- 本来运行之后在logcat那里会有运行时间xxxms的提示，运行起来也比较慢、卡.....专门去改了改就没了....找问题用的方法比较原始，猜测可能的问题代码段，注释掉再运行看效果...也可以使用性能分析工具
- 手势处理与事件分发传递：ImageActivity
- PaperView（缓存三页）
- 自定义遮罩：ShadowImageView(继承自ImageView，重写绘制函数从而在上面镂空绘字)
- 一开始直接将大图放到imageview中导致OOM
使用熟练后，却也在获取其缓存图片位置和结合下载进度条时卡了很久
```Java
GlideApp.with(xxxx).downloadOnly().load(xxxxxx).submit().get().getAbsolutePath();
//                已经失效了的办法,我上传项目中保留了DataCacheKey....当时为了实现这个功能还去看了源码，不过现在忘得差不多了
//                DiskCache disk  = DiskLruCacheWrapper.get(GlideApp.getPhotoCacheDir(MultiImageActivity.this), 250 * 1024 * 1024);
//                DataCacheKey dataCacheKey = new DataCacheKey(new GlideUrl(url), EmptySignature.obtain());
//                File file =disk.get(dataCacheKey);
```
下载进度条见ProgressAppGlideModule文件与GlideImageLoader
- GridSpacingItemDecoration文件为RecyclerView的GridLayoutManager提供了合适的间隔
- 按钮移动动画后可能会遇到无法点击的情况，才明白按钮可以认为是个两层控件，一层用于显示，一层用于响应事件。所以在移动动画结束时，使用了offsetTopAndBottom，确保可以响应点击。详见MainActivity的setShareAnimation()
- 数据库存储与SharedPreferences（xml存储）都试着用了一遍
- retrofit网络部分见/model/network/...
- 时间借助joda库
- 用到了QQ、新浪微博的登录以及分享。微信因为不提供个人开发者登录功能，便只有分享。图文分享其可以直接将文字绘制到图片上然后去分享图片。
### 开源协议
Apache 2.0
