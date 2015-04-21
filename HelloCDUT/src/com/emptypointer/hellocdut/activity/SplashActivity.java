package com.emptypointer.hellocdut.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * 开屏页
 *
 * @author Sequarius
 */
public class SplashActivity extends Activity {
    private RelativeLayout mLayout;
    private LinearLayout mLayoutText;

    // 开屏等待时长
    private static final int SLEEP_TIME = 2000;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mLayout = (RelativeLayout) findViewById(R.id.layout_root);
        new loadTextThread().start();
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        mLayout.startAnimation(animation);

    }

    private class loadTextThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String[] strs = getResources().getStringArray(
                    R.array.str_array_hello_cdut);
            final List<String> list = new ArrayList<String>();
            list.add("> std::cout <<\"Hello CDUT\"");
            for (String str : strs) {
                list.add(str);
            }
            List<String> showList = new ArrayList<String>();
            final Random random = new Random();
            while (showList.size() < 4) {
                String tempString = list.get(random.nextInt(list.size()));
                if (!showList.contains(tempString)) {
                    showList.add(tempString);
                }
            }

            for (final String str : showList) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        addText(str);
                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * 添加一个文字弹幕
     *
     * @param text
     */
    private void addText(String text) {
        mLayoutText = (LinearLayout) findViewById(R.id.layout_text);
        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(param);
        mLayoutText.addView(textView);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        new Thread(new Runnable() {
            public void run() {
                if (EPApplication.getInstance().isLogined()) {
                    long start = System.currentTimeMillis();
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;
                    // 等待sleeptime时长
                    if (SLEEP_TIME - costTime > 0) {
                        try {
                            Thread.sleep(SLEEP_TIME - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // 进入主页面
                    startActivity(new Intent(SplashActivity.this,
                            MainActivity.class));
                    finish();
                } else {
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(SplashActivity.this,
                            LoginActivity.class));
                    finish();
                }
            }
        }).start();
    }

}
