package pl.wp.quiz.synchronizer;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.widget.BaseAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.wp.quiz.listener.LoadDataListener;

/**
 * @author Krzysztof Betlej <labiod@wp.pl>.
 * @date 2/19/18
 */

public class QuizzesDataLoader {

    private static class BackgroundTask extends AsyncTask<String, Void, JSONObject> {

        private LoadDataListener<JSONObject> mListener;

        public BackgroundTask(LoadDataListener<JSONObject> listener) {
            mListener = listener;
        }
        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    stringBuffer.append(line);
                }
                return new JSONObject(stringBuffer.toString());
            } catch (IOException | JSONException e) {
                Log.e(TAG, "loadData: ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (mListener != null) {
                mListener.onLoadData(jsonObject, SYNC_DATA);
            }
        }
    }

    public static final String TAG = QuizzesDataLoader.class.getSimpleName();
    public static final int SYNC_DATA = 1;

    public static void loadData(final String entryPointUrl, LoadDataListener<JSONObject> listener) {

        BackgroundTask backgroundTask = new BackgroundTask(listener);
        backgroundTask.execute(entryPointUrl);
    }
}
