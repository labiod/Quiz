package pl.wp.quiz.synchronizer;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pl.wp.quiz.QuizApplication;
import pl.wp.quiz.listener.LoadDataListener;
import pl.wp.quiz.provider.database.QuizContract;

import static pl.wp.quiz.provider.database.QuizContract.CONTENT_URI;
import static pl.wp.quiz.provider.database.QuizContract.QuestionAnswers;
import static pl.wp.quiz.provider.database.QuizContract.QuizQuestions;
import static pl.wp.quiz.provider.database.QuizContract.Quizzes;

public class SynchronizeService extends Service implements LoadDataListener<JSONObject> {
    private interface OnQueueEndListener {
        void onQueueEnd();
    }

    public static final String SYNCHRONIZED = "synchronized";
    public static final String TAG = SynchronizeService.class.getSimpleName();
    public static final String MESSAGE_INFO = "message";
    public static final String MAX_PROGRESS = "max_progress";
    private static final int MAX_BACKGROUN_TASK = 5;
    private final LinkedList<String> mLoadingQueqe = new LinkedList<>();
    private final LinkedList<ImageLoadItem> mImageToLoad = new LinkedList<>();
    private int mLoadProgress = 0;
    private final List<ImageLoaderTask> mRunningTask = new ArrayList<>();

    public SynchronizeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {

            switch (intent.getAction()) {
                case SYNCHRONIZED:
                    mLoadProgress = 0;
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
                quiz.put(Quizzes.QUIZ_PROGRESS, 0);
                final long id = item.getLong("id");
                Uri quizUri = Uri.withAppendedPath(CONTENT_URI, Quizzes.TABLE_NAME);
                mImageToLoad.add(new ImageLoadItem(
                        item.getJSONObject("mainPhoto").getString("url"),
                        Quizzes.TABLE_NAME,
                        Quizzes.QUIZ_PHOTO_BLOB,
                        id));
                if (!quizExist(quizUri, id)) {
                    quiz.put(Quizzes.ID_QUIZ, id);
                    getContentResolver().insert(quizUri, quiz);
                    mLoadingQueqe.add("http://quiz.o2.pl/api/v1/quiz/" + id + "/0");
                }
            }

            loadFromQuequ(new OnQueueEndListener() {
                @Override
                public void onQueueEnd() {
                    Bundle args = new Bundle();
                    args.putString(MESSAGE_INFO, "Load images");
                    args.putInt(MAX_PROGRESS, mImageToLoad.size());
                    mLoadProgress = 0;
                    sendSyncInProgress(mLoadProgress++, args);
                    loadAllImages(new OnQueueEndListener() {
                        @Override
                        public void onQueueEnd() {
                            if (mRunningTask.size() == 0) {
                                finishSync();
                            }
                        }
                    });
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "onLoadData: ", e);
        }
    }

    private void loadAllImages(final OnQueueEndListener onQueueEndListener) {
        if (mImageToLoad.size() == 0) {
            onQueueEndListener.onQueueEnd();
        } else {
            int availableTask = MAX_BACKGROUN_TASK - mRunningTask.size();
            if (availableTask < 0) {
                final ImageLoadItem item = mImageToLoad.removeFirst();
                final ImageLoaderTask task = new ImageLoaderTask();
                mRunningTask.add(task);
                task.setOnLoadTaskListener(new ImageLoaderTask.OnLoadTaskListener() {
                    @Override
                    public void onFinished(Bitmap bitmap) {
                        mRunningTask.remove(task);
                        putBitmapToDataBase(bitmap, item);
                        loadAllImages(onQueueEndListener);
                        sendSyncInProgress(mLoadProgress++);
                    }
                });
                task.execute(item);
            } else {
                for (int i = 0; i < availableTask; ++i) {
                    final ImageLoadItem item = mImageToLoad.removeFirst();
                    final ImageLoaderTask task = new ImageLoaderTask();
                    mRunningTask.add(task);
                    task.setOnLoadTaskListener(new ImageLoaderTask.OnLoadTaskListener() {
                        @Override
                        public void onFinished(Bitmap bitmap) {
                            mRunningTask.remove(task);
                            putBitmapToDataBase(bitmap, item);
                            loadAllImages(onQueueEndListener);
                            sendSyncInProgress(mLoadProgress++);
                        }
                    });
                    task.execute(item);
                }
            }

        }
    }

    private void putBitmapToDataBase(Bitmap bitmap, ImageLoadItem item) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bytes);
            bitmap.recycle();
        } catch (Exception e) {
            Log.e("Error", "message", e);
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put(item.getImageColumn(), bytes.toByteArray());
        Uri uri = Uri.withAppendedPath(QuizContract.CONTENT_URI, item.getTableName());
        getContentResolver().update(uri,
                values,
                QuizContract.getIdForTable(item.getTableName()) + " = " + item.getId(),
                null);
    }

    private void loadFromQuequ(final OnQueueEndListener onQueueEndListener) {
        if (mLoadingQueqe.size() == 0) {
            onQueueEndListener.onQueueEnd();
        } else {
            QuizzesDataLoader.loadData(mLoadingQueqe.removeFirst(),
                    new LoadDataListener<JSONObject>() {
                        @Override
                        public void onLoadData(JSONObject dataList) {
                            loadQuestionData(dataList);
                            loadFromQuequ(onQueueEndListener);
                            sendSyncInProgress(mLoadProgress++);
                        }
            });
        }

    }

    private void loadQuestionData(JSONObject dataList) {
        try {
            JSONArray questions = dataList.getJSONArray("questions");
            JSONArray rates = dataList.getJSONArray("rates");
            long id = dataList.getLong("id");
            for (int i = 0; i < questions.length(); ++i) {
                JSONObject q = questions.getJSONObject(i);
                ContentValues question = new ContentValues();

                question.put(QuizQuestions.QUIZ_ID, id);

                question.put(QuizQuestions.QUESTION_TEXT, q.getString("text"));
                question.put(QuizQuestions.QUESTION_TYPE, q.getString("type"));
                question.put(QuizQuestions.QUESTION_ORDER, q.getInt("order"));
                Uri questionUri = Uri.withAppendedPath(CONTENT_URI, "/" + QuizQuestions.TABLE_NAME);
                long qId = Long.parseLong(getContentResolver().insert(questionUri, question).getLastPathSegment());
                String imageUri = q.getJSONObject("image").getString("url");
                if (!imageUri.isEmpty()) {
                    mImageToLoad.add(new ImageLoadItem(
                            imageUri,
                            QuizQuestions.TABLE_NAME,
                            QuizQuestions.QUESTION_PHOTO_BLOB,
                            qId)
                    );
                }
                JSONArray answers = q.getJSONArray("answers");
                for (int j = 0; j < answers.length(); ++j) {
                    JSONObject answer = answers.getJSONObject(j);
                    ContentValues answerValue = new ContentValues();
                    answerValue.put(QuestionAnswers.QUESTION_ID, qId);
                    answerValue.put(QuestionAnswers.ANSWER_TEXT, answer.getString("text"));
                    answerValue.put(QuestionAnswers.IS_CORRECT, answer.has("isCorrect") ? 1 : 0);
                    answerValue.put(QuestionAnswers.ANSWER_ORDER, answer.getInt("order"));
                    Uri answerUri = Uri.withAppendedPath(CONTENT_URI, "/" + QuestionAnswers.TABLE_NAME);
                    long aId = Long.parseLong(getContentResolver().insert(answerUri, answerValue).getLastPathSegment());
                    String answerImageUri = answer.getJSONObject("image").getString("url");
                    if (!answerImageUri.isEmpty()) {
                        mImageToLoad.add(new ImageLoadItem(
                                answerImageUri,
                                QuestionAnswers.TABLE_NAME,
                                QuestionAnswers.ANSWER_IMAGE_BLOB,
                                aId)
                        );
                    }
                }
            }
            for (int r = 0; r < rates.length(); ++r) {
                JSONObject rate = rates.getJSONObject(r);
                ContentValues rateValue = new ContentValues();
                rateValue.put(QuizContract.QuizRates.QUIZ_ID, id);
                rateValue.put(QuizContract.QuizRates.RATE_FROM, rate.getInt("from"));
                rateValue.put(QuizContract.QuizRates.RATE_TO, rate.getInt("to"));
                rateValue.put(QuizContract.QuizRates.RATE_CONTENT, rate.getString("content"));
                Uri rateUri = Uri.withAppendedPath(CONTENT_URI, "/" + QuizContract.QuizRates.TABLE_NAME);
                getContentResolver().insert(rateUri, rateValue);
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

    public void finishSync() {
        ((QuizApplication)getApplication()).databaseSyncFinish();
    }

    public void sendSyncInProgress(int progress) {
        sendSyncInProgress(progress, null);
    }
    public void sendSyncInProgress(int progress, Bundle args) {
        ((QuizApplication)getApplication()).databaseSyncProgress(progress, args);
    }
}
