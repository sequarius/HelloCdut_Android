package com.emptypointer.hellocdut.service;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.emptypointer.hellocdut.R;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

public class EPShareService {

    private Activity mActivity;
    private UMSocialService mController;

    public EPShareService(Activity mActivity) {
        super();
        this.mActivity = mActivity;
    }

    public void shareContent(String content, String URL, Bitmap bitmap, String title) {
        mController = UMServiceFactory
                .getUMSocialService("com.umeng.share");
        if (content != null) {
            // 设置分享内容
            mController.setShareContent(content);
        } else {
            mController.setShareContent("你好理工 http://www.hellocdut.com/");
        }
        // 设置分享图片, 参数2为图片的url地址
        if (bitmap != null) {
            mController.setShareMedia(new UMImage(mActivity,
                    bitmap));
        } else {
            mController.setShareMedia(new UMImage(mActivity,
                    R.drawable.ic_launcher));
        }
        // 设置分享图片，参数2为本地图片的资源引用
        // mController.setShareMedia(new UMImage(getActivity(),
        // R.drawable.icon));
        // 设置分享图片，参数2为本地图片的路径(绝对路径)
        // mController.setShareMedia(new UMImage(getActivity(),
        // BitmapFactory.decodeFile("/mnt/sdcard/icon.png")));

        // 设置分享音乐
        // UMusic uMusic = new
        // UMusic("http://sns.whalecloud.com/test_music.mp3");
        // uMusic.setAuthor("GuGu");
        // uMusic.setTitle("天籁之音");
        // 设置音乐缩略图
        // uMusic.setThumb("http://www.umeng.com/images/pic/banner_module_social.png");
        // mController.setShareMedia(uMusic);

        // 设置分享视频
        // UMVideo umVideo = new UMVideo(
        // "http://v.youku.com/v_show/id_XNTE5ODAwMDM2.html?f=19001023");
        // 设置视频缩略图
        // umVideo.setThumb("http://www.umeng.com/images/pic/banner_module_social.png");
        // umVideo.setTitle("友盟社会化分享!");
        // mController.setShareMedia(umVideo);

        String appID = "wxaa788cdb6c760221";
        String appSecret = "5c3ce34b281b77aeb246fa3d6a150d31";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mActivity, appID, appSecret);
        wxHandler.addToSocialSDK();
        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mActivity, appID,
                appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        // 添加到QQ
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mActivity,
                "1104270736", "Of5l3vP2qrHLfgaC");
        qqSsoHandler.addToSocialSDK();
        // 分享到QQ空間
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mActivity,
                "1104270736", "Of5l3vP2qrHLfgaC");
        qZoneSsoHandler.addToSocialSDK();
        if (URL == null) {
            qZoneSsoHandler.setTargetUrl("http://www.hellocdut.com/");
            qqSsoHandler.setTargetUrl("http://www.hellocdut.com/");
            wxCircleHandler.setTargetUrl("http://www.hellocdut.com/");
            wxHandler.setTargetUrl("http://www.hellocdut.com/");
        } else {
            qZoneSsoHandler.setTargetUrl(URL);
            wxCircleHandler.setTargetUrl(URL);
            wxHandler.setTargetUrl(URL);
            qqSsoHandler.setTargetUrl(URL);
        }
        if (title != null) {
            qqSsoHandler.setTitle(title);
            wxCircleHandler.setTitle(title);
            wxHandler.setTitle(title);
        }

        mController.openShare(mActivity, false);
    }


}
