package pl.wp.quiz;

import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * @author Krzysztof Betlej <k.betlej@samsung.com>.
 * @date 2/20/18
 * @copyright Copyright (c) 2016 by Samsung Electronics Polska Sp. z o. o.
 */

public class QuizApplication extends Application {
    private WeakReference<OnDatabaseSynchronizedListener> mDatabaseSynchronizedListener;
    public interface OnDatabaseSynchronizedListener {
        void onDatabaseSynchronized();
        void onDatabaseSyncProgress(int progress, Bundle args);
    }

    public void registerOnDatabaseUpdateListener(OnDatabaseSynchronizedListener listener) {
        mDatabaseSynchronizedListener = new WeakReference<OnDatabaseSynchronizedListener>(listener);
    }

    public void databaseSyncFinish() {

        if (mDatabaseSynchronizedListener != null) {
            OnDatabaseSynchronizedListener listener = mDatabaseSynchronizedListener.get();
            if (listener != null) {
                listener.onDatabaseSynchronized();
            }
        }
    }

    public void databaseSyncProgress(int progress, Bundle args) {

        if (mDatabaseSynchronizedListener != null) {
            OnDatabaseSynchronizedListener listener = mDatabaseSynchronizedListener.get();
            if (listener != null) {
                listener.onDatabaseSyncProgress(progress, args);
            }
        }
    }
}
