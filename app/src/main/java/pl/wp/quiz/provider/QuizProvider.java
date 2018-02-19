package pl.wp.quiz.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import pl.wp.quiz.provider.database.QuizeesDBHelper;

public class QuizProvider extends ContentProvider {
    private SQLiteDatabase db;
    private QuizeesDBHelper mDbHelper;


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;
        switch (uri.getLastPathSegment()) {
            case QuizContract.QuizQuestions.TABLE_NAME:
                table = QuizContract.QuizQuestions.TABLE_NAME;
                break;
            default:
                table = QuizContract.Quizzes.TABLE_NAME;
        }
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
        // TODO: Implement basically for now. Retreive data only from quiz.

        String table;
        switch (uri.getLastPathSegment()) {
            case QuizContract.QuizQuestions.TABLE_NAME:
                table = QuizContract.QuizQuestions.TABLE_NAME;
                break;
            default:
                table = QuizContract.Quizzes.TABLE_NAME;
        }
        return mDbHelper.getReadableDatabase().query(table, projection, selection, selectionArgs, null, null, sortOrder);

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String table;
        switch (uri.getLastPathSegment()) {
            case QuizContract.QuizQuestions.TABLE_NAME:
                table = QuizContract.QuizQuestions.TABLE_NAME;
                break;
            default:
                table = QuizContract.Quizzes.TABLE_NAME;
        }
        return mDbHelper.getWritableDatabase().update(table, values, selection, selectionArgs);
    }
}
