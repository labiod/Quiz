package pl.wp.quiz.synchronizer;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import pl.wp.quiz.QuizApplication;

/**
 * @author Krzysztof Betlej <labiod@wp.pl>.
 * @date 2/19/18
 */

public class SyncDataReceiver extends BroadcastReceiver {

    public static final int SYNCHRONIZED_DATA_WITH_SERVICE = 1;
    public static final String TAG = SyncDataReceiver.class.getSimpleName();

    public static void startDataSynchronize(Context context, QuizApplication.OnDatabaseSynchronizedListener listener) {
        Intent i = new Intent(context, SyncDataReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, SYNCHRONIZED_DATA_WITH_SERVICE, i, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 60 * 1000;// start after 1 minutes.

        // Schedule the alarm!
        ((QuizApplication)context.getApplicationContext()).registerOnDatabaseUpdateListener(listener);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, firstTime, sender);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "new alarm Intent");
        Intent startIntent = new Intent(context, SynchronizeService.class);
        startIntent.setAction(SynchronizeService.SYNCHRONIZED);
        context.startService(startIntent);
    }
}
