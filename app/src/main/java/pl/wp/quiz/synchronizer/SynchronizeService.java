package pl.wp.quiz.synchronizer;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.wp.quiz.provider.QuizContract;

public class SynchronizeService extends Service {
    public static final String SYNCHRONIZED = "synchronized";
    public static final String TAG = SynchronizeService.class.getSimpleName();

    public SynchronizeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case SYNCHRONIZED:
                    Log.d(TAG, "onStartCommand: synchronized data");
                    putMockupData();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void putMockupData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date createdDate = new Date();
        try {
            createdDate = sdf.parse("2018-02-18T16:38:45+0000");
        } catch (ParseException e) {
            Log.e(TAG, "putMockupData: ", e);
        }
        ContentValues quiz = new ContentValues();
        quiz.put(QuizContract.Quizzes.QUIZ_TITLE,
                "Państwa, miasta i kontynenty. Rozpoznaj po jednej wskazówce");

        quiz.put(QuizContract.Quizzes.QUESTION_NUMBER, 11);
        quiz.put(QuizContract.Quizzes.QUIZ_CREATED_AT, createdDate.getTime());
        quiz.put(QuizContract.Quizzes.QUIZ_CONTENT, "Sprawdź, czy jesteś dobry z wiedzy o świecie.");
        quiz.put(QuizContract.Quizzes.QUIZ_TYPE, "KNOWLEDGE");
        quiz.put(QuizContract.Quizzes.QUIZ_CATEGORY, "w pustyni i w puszczy");
        quiz.put(QuizContract.Quizzes.QUIZ_PHOTO_URI, "https://d.wpimg.pl/532860914--1661051647/globus.jpg");
        quiz.put(QuizContract.Quizzes.QUIZ_PROGRESS, 0);
        Uri baseUri = Uri.withAppendedPath(QuizContract.CONTENT_URI, "/" + QuizContract.Quizzes.TABLE_NAME);
        if (quizExist(baseUri, 6221709005162625L)) {
            getContentResolver().update(baseUri, quiz, QuizContract.Quizzes.ID_QUIZ + " = 6221709005162625", null);
        } else {
            quiz.put(QuizContract.Quizzes.ID_QUIZ, 6221709005162625L);
            getContentResolver().insert(baseUri, quiz);
        }
    }

    private boolean quizExist(Uri uri, long id) {
        Cursor cursor = getContentResolver().query(uri,
                null,
                QuizContract.Quizzes.ID_QUIZ + " =" + id,
                null,
                null);
        return cursor != null && cursor.getCount() == 1;
    }
}
