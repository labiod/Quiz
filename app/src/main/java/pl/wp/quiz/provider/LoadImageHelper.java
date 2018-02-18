package pl.wp.quiz.provider;

import android.os.AsyncTask;
import android.widget.ImageView;

import pl.wp.quiz.listener.DataReceiver;
import pl.wp.quiz.model.QuizModel;

public class LoadImageHelper {
    public static void downloadImage(DataReceiver receiver, String sourceURI) {
        receiver.onDataDownload(sourceURI);
    }
}
