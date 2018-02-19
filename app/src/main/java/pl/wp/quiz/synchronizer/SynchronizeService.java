package pl.wp.quiz.synchronizer;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.wp.quiz.listener.LoadDataListener;
import pl.wp.quiz.provider.QuizContract;

import static pl.wp.quiz.provider.QuizContract.CONTENT_URI;
import static pl.wp.quiz.provider.QuizContract.QuestionAnswers;
import static pl.wp.quiz.provider.QuizContract.QuizQuestions;
import static pl.wp.quiz.provider.QuizContract.Quizzes;

public class SynchronizeService extends Service implements LoadDataListener<JSONObject> {

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
                    QuizzesDataLoader.loadData("http://quiz.o2.pl/api/v1/quizzes/0/100", this);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLoadData(JSONObject quizzes) {
        Log.d(TAG, "onLoadData: data loaded");
        try {
            int count = quizzes.getInt("count");
            Log.d(TAG, "onLoadData: data count:" + quizzes.get("count"));
            JSONArray items = quizzes.getJSONArray("items");
            for (int i = 0; i < count; ++i) {
                JSONObject item = items.getJSONObject(i);
                ContentValues quiz = new ContentValues();
                quiz.put(QuizContract.Quizzes.QUIZ_TITLE, item.getString("title"));
                quiz.put(Quizzes.QUESTION_NUMBER, item.getInt("questions"));
                quiz.put(Quizzes.QUIZ_CREATED_AT, item.getString("createdAt"));
                quiz.put(Quizzes.QUIZ_CONTENT, item.getString("content"));
                quiz.put(Quizzes.QUIZ_TYPE, item.getString("type"));
                quiz.put(Quizzes.QUIZ_CATEGORY, item.getJSONObject("category").getString("name"));
                quiz.put(Quizzes.QUIZ_PHOTO_URI, item.getJSONObject("mainPhoto").getString("url"));
                quiz.put(Quizzes.QUIZ_PROGRESS, 0);
                final long id = item.getLong("id");
                Uri quizUri = Uri.withAppendedPath(CONTENT_URI, "/" + Quizzes.TABLE_NAME);
                if (!quizExist(quizUri, id)) {
                    quiz.put(Quizzes.ID_QUIZ, id);
                    getContentResolver().insert(quizUri, quiz);
                    QuizzesDataLoader.loadData(
                            "http://quiz.o2.pl/api/v1/quiz/" + id + "/0",
                            new LoadDataListener<JSONObject>() {
                                @Override
                                public void onLoadData(JSONObject dataList) {
                                    loadQuestionData(dataList, id);
                                }
                            });
                }

            }

        } catch (JSONException e) {
            Log.e(TAG, "onLoadData: ", e);
        }
    }

    private void loadQuestionData(JSONObject dataList, long id) {
        try {
            JSONArray questions = dataList.getJSONArray("questions");
            for (int i = 0; i < questions.length(); ++i) {
                JSONObject q = questions.getJSONObject(i);
                ContentValues question = new ContentValues();
                question.put(QuizQuestions.QUIZ_ID, id);

                question.put(QuizQuestions.QUESTION_TEXT, q.getString("text"));
                question.put(QuizQuestions.QUESTION_IMAGE_URI, q.getJSONObject("image").getString("url"));
                question.put(QuizQuestions.QUESTION_TYPE, q.getString("type"));
                question.put(QuizQuestions.QUESTION_ORDER, q.getInt("order"));
                Uri questionUri = Uri.withAppendedPath(CONTENT_URI, "/" + QuizQuestions.TABLE_NAME);
                long qId = Long.parseLong(getContentResolver().insert(questionUri, question).getLastPathSegment());
                JSONArray answers = q.getJSONArray("answers");
                for (int j = 0; j < answers.length(); ++j) {
                    JSONObject answer = answers.getJSONObject(j);
                    ContentValues answerValue = new ContentValues();
                    answerValue.put(QuestionAnswers.QUESTION_ID, qId);
                    answerValue.put(QuestionAnswers.ANSWER_TEXT, answer.getString("text"));
                    answerValue.put(QuestionAnswers.IS_CORRECT, answer.has("isCorrect") ? 1 : 0);
                    answerValue.put(QuestionAnswers.ANSWER_ORDER, answer.getInt("order"));
                    answerValue.put(QuestionAnswers.ANSWER_IMAGE_URI, answer.getJSONObject("image")
                            .getString("url"));

                    Uri answerUri = Uri.withAppendedPath(CONTENT_URI, "/" + QuestionAnswers.TABLE_NAME);
                    getContentResolver().insert(answerUri, question);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "onLoadData: ", e);
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
