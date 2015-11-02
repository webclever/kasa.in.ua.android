package my_services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Женя on 30.10.2015.
 */
public class TimerService extends Service {
    final String LOG_TAG = "myLogs";
    private Long time = null;
    private IBinder mBinder = new MyBinder();

    public void onCreate() {
        super.onCreate();
        CountDownTimer countDownTimer = new CountDownTimer(90000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time = millisUntilFinished;
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
        Log.d(LOG_TAG, "MyService onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "MyService onBind");
        return mBinder;
    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(LOG_TAG, "MyService onRebind");
    }

    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "MyService onUnbind");
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "MyService onDestroy");
    }

    public Long getTime()
    {
        return time;
    }
    public class MyBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }
}
