package pl.wp.quiz.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import pl.wp.quiz.provider.database.QuizeesDBHelper;

public class QuizProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String TAG = QuizProvider.class.getSimpleName();

    static {

        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.Quizzes.TABLE_NAME, 1);
        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.QuizQuestions.TABLE_NAME, 2);
        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.QuestionAnswers.TABLE_NAME, 3);
        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.UsersAnswers.TABLE_NAME, 4);

        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.Quizzes.TABLE_NAME + "/#", 5);
        sUriMatcher.addURI("pl.wp.quiz.provider", QuizContract.QUESTION_WITH_ANSWER, 6);
    }

    private QuizeesDBHelper mDbHelper;


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
        String table = uri.getLastPathSegment();
        long id = mDbHelper.getWritableDatabase().insert(table, null, values);
        return Uri.withAppendedPath(QuizContract.CONTENT_URI, "/" + id);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new QuizeesDBHelper(getContext());
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
                String table = uri.getLastPathSegment();
                return mDbHelper.getReadableDatabase().query(
                        table,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case 5:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "query: id: " + id);
                String query = "SELECT COUNT(" + QuizContract.UsersAnswers.ID + ") correct_answer, " + QuizContract.Quizzes.QUESTION_NUMBER + " " +
                        "FROM " + QuizContract.UsersAnswers.TABLE_NAME + " uq " +
                        "INNER JOIN " + QuizContract.QuestionAnswers.TABLE_NAME + " qa " +
                        "INNER JOIN " + QuizContract.QuizQuestions.TABLE_NAME + " qq " +
                        "INNER JOIN " + QuizContract.Quizzes.TABLE_NAME + " q " +
                        "WHERE " + QuizContract.QuizQuestions.QUIZ_ID + " = " + id + " AND " +
                        QuizContract.QuestionAnswers.IS_CORRECT + " = 1";
                return mDbHelper.getReadableDatabase().rawQuery(query, null);
            case 6:
                String qQuery = "SELECT * FROM " + QuizContract.QuizQuestions.TABLE_NAME + " qq " +
                        "INNER JOIN " + QuizContract.QuestionAnswers.TABLE_NAME + " qa " +
                        "WHERE qq.id_question = qa.question_id AND (" + selection + " )" +
                        " ORDER BY " + sortOrder;
                return mDbHelper.getReadableDatabase().rawQuery(qQuery, null);
            default:
                return null;
        }


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String table = uri.getLastPathSegment();
        return mDbHelper.getWritableDatabase().update(table, values, selection, selectionArgs);
    }
}
