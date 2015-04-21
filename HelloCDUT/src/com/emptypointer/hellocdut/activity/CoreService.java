package com.emptypointer.hellocdut.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.easemob.chat.EMChatManager;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.service.EPHttpQueueService;

/**
 * EP核心server 包含耗時的異步操作處理
 *
 * @author Sequarius
 */
public class CoreService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private EPBinder mBinder;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            long endTime = System.currentTimeMillis() + 5 * 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
//			stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mBinder = new EPBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the
        // job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        EPHttpQueueService service = EPHttpQueueService.getInstance(getApplicationContext());
        service.startService();

        // 让该service前台运行，避免手机休眠时系统自动杀掉该服务
        // 如果 id 为 0 ，那么状态栏的 notification 将不会显示。

        // Notification
        // notification=Notification.Builder(getApplicationContext());
        // startForeground(startId, notification);
        // startForeground(startId, null);
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return mBinder;
    }

    @Override
    public void onDestroy() {
        startService(new Intent(this, CoreService.class));
    }

    private class EPBinder extends Binder {

    }

}
