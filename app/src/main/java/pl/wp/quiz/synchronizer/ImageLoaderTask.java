package pl.wp.quiz.synchronizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

public class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
    public interface OnLoadTaskListener {
        void onFinished(Bitmap bitmap);
    }

    private OnLoadTaskListener mListener;

    public ImageLoaderTask(OnLoadTaskListener listener) {
        mListener = listener;
    }


    @Override
    protected Bitmap doInBackground(String... items) {
        String imageUrl = items[0];
        Bitmap result = null;
        try {
            InputStream in = new java.net.URL(imageUrl).openStream();
            result = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onCancelled(Bitmap bitmap) {
        super.onCancelled(bitmap);
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mListener != null) {
            mListener.onFinished(bitmap);
        }
    }
}
