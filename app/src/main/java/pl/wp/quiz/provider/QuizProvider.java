package pl.wp.quiz.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import pl.wp.quiz.provider.database.QuizContract;
import pl.wp.quiz.provider.database.QuizzesDBHelper;

public class QuizProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String TAG = QuizProvider.class.getSimpleName();

    static {

        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.Quizzes.TABLE_NAME, 1);
        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.QuizQuestions.TABLE_NAME, 2);
        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.QuestionAnswers.TABLE_NAME, 3);
        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.UsersAnswers.TABLE_NAME, 4);
        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.QuizRates.TABLE_NAME, 5);

        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.Quizzes.TABLE_NAME + "/#", 6);
        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.QuizQuestions.TABLE_NAME + "/#", 7);
        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.QUESTION_WITH_ANSWER, 8);
    }

    private QuizzesDBHelper mDbHelper;


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        String table = uri.getLastPathSegment();
        return mDbHelper.getWritableDatabase().delete(table, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case 4:
                String[] answers = values.getAsString(QuizContract.UsersAnswers.ANSWERS_LIST)
                        .split(QuizContract.UsersAnswers.ANSWER_SEPARATOR);
                int answerCount = answers.length;
                int answerRate = 0;
                for (int i = 0; i < answerCount; ++i) {
                    answerRate += Integer.parseInt(answers[i]);
                }
                answerRate = (answerRate * 100) / answerCount;
                Cursor cursor = mDbHelper.getReadableDatabase().query(QuizContract.QuizRates.TABLE_NAME,
                        null,
                        QuizContract.QuizRates.QUIZ_ID + " = " + values.getAsLong(QuizContract.UsersAnswers.QUIZ_ID),
                        null,
                        null,
                        null,
                        null
                );
                if (cursor != null) {
                    if (cursor.getColumnCount() > 0 && cursor.moveToFirst()) {
                        do {
                            int from = cursor.getInt(cursor.getColumnIndex(QuizContract.QuizRates.RATE_FROM));
                            int to = cursor.getInt(cursor.getColumnIndex(QuizContract.QuizRates.RATE_TO));
                            if (answerRate > from && answerRate <= to) {
                                values.put(QuizContract.UsersAnswers.RATE_ID,
                                        cursor.getLong(cursor.getColumnIndex(QuizContract.QuizRates.ID_RATE)));
                                break;
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
                break;
        }
        String table = uri.getLastPathSegment();
        long id = mDbHelper.getWritableDatabase().insert(table, null, values);
        return Uri.withAppendedPath(QuizContract.CONTENT_URI, "/" + id);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new QuizzesDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                String table = uri.getLastPathSegment();
                return mDbHelper.getReadableDatabase().query(
                        table,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case 6:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "query: id: " + id);
                String query = "SELECT " + QuizContract.UsersAnswers.ANSWERS_LIST + ", " +
                        QuizContract.Quizzes.QUESTION_NUMBER + ", " +
                        QuizContract.QuizRates.RATE_CONTENT + " " +
                        "FROM " + QuizContract.UsersAnswers.TABLE_NAME + " uq " +
                        "INNER JOIN " + QuizContract.Quizzes.TABLE_NAME + " q " +
                        "ON uq." + QuizContract.UsersAnswers.QUIZ_ID + " = q." + QuizContract.Quizzes.ID_QUIZ +  " " +
                        "INNER JOIN " + QuizContract.QuizRates.TABLE_NAME + " qr " +
                        "ON uq." + QuizContract.UsersAnswers.RATE_ID + " = qr." + QuizContract.QuizRates.ID_RATE +  " " +
                        "WHERE uq." + QuizContract.QuizQuestions.QUIZ_ID + " = " + id;
                if (sortOrder != null && !sortOrder.trim().isEmpty()) {
                    query += " ORDER BY " + sortOrder;
                }
                Log.d(TAG, "query: " + query);
                return mDbHelper.getReadableDatabase().rawQuery(query, null);
            case 8:
                String qQuery = "SELECT * FROM " + QuizContract.QuizQuestions.TABLE_NAME + " qq " +
                        "INNER JOIN " + QuizContract.QuestionAnswers.TABLE_NAME + " qa " +
                        "WHERE qq.id_question = qa.question_id";
                if (selection != null && !selection.trim().isEmpty()) {
                    qQuery += " AND (" + selection + " )";
                }
                if (sortOrder != null && !sortOrder.trim().isEmpty()) {
                    qQuery += " ORDER BY " + sortOrder;
                }
                Log.d(TAG, "query: " + qQuery);
                return mDbHelper.getReadableDatabase().rawQuery(qQuery, null);
            default:
                return null;
        }


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case 4:
                String[] answers = values.getAsString(QuizContract.UsersAnswers.ANSWERS_LIST)
                        .split(QuizContract.UsersAnswers.ANSWER_SEPARATOR);
                int answerCount = answers.length;
                int answerRate = 0;
                for (int i = 0; i < answerCount; ++i) {
                    answerRate += Integer.parseInt(answers[i]);
                }
                answerRate = (answerRate * 100) / answerCount;
                Cursor cursor = mDbHelper.getReadableDatabase().query(QuizContract.QuizRates.TABLE_NAME,
                        null,
                        QuizContract.QuizRates.QUIZ_ID + " = " + values.getAsLong(QuizContract.UsersAnswers.QUIZ_ID),
                        null,
                        null,
                        null,
                        null
                        );
                if (cursor != null) {
                    if (cursor.getColumnCount() > 0 && cursor.moveToFirst()) {
                        do {
                            int from = cursor.getInt(cursor.getColumnIndex(QuizContract.QuizRates.RATE_FROM));
                            int to = cursor.getInt(cursor.getColumnIndex(QuizContract.QuizRates.RATE_FROM));
                            if (answerRate > from && answerRate <= to) {
                                values.put(QuizContract.UsersAnswers.RATE_ID,
                                        cursor.getLong(cursor.getColumnIndex(QuizContract.QuizRates.ID_RATE)));
                                break;
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
                break;
        }
        String table = uri.getLastPathSegment();
        return mDbHelper.getWritableDatabase().update(table, values, selection, selectionArgs);
    }
}
