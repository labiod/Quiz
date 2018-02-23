package pl.wp.quiz.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pl.wp.quiz.provider.database.QuizContract;
import pl.wp.quiz.provider.database.QuizzesDBHelper;

import static pl.wp.quiz.provider.database.ImageContract.AUTHORITY;
import static pl.wp.quiz.provider.database.ImageContract.ImageEntry;

/**
 * @author Krzysztof Betlej <labiod@wp.pl>.
 * @date 2/22/18
 */

public class ImageDataProvider extends ContentProvider {

    public static final String TAG = QuizProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int QUIZZES_QUERY = 1;
    private static final int QUIZ_QUERY = 2;
    private static final int QUESTION_QUERY = 3;

    static {
        sUriMatcher.addURI(AUTHORITY, "quizzes", QUIZZES_QUERY);
        sUriMatcher.addURI(AUTHORITY, "quizzes/#", QUIZ_QUERY);
        sUriMatcher.addURI(AUTHORITY, "questions/#", QUESTION_QUERY);
    }

    private QuizzesDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new QuizzesDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case QUIZZES_QUERY:
                return mDbHelper.getReadableDatabase().query(
                        ImageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case QUIZ_QUERY:
                long quizId = Long.parseLong(uri.getLastPathSegment());
                String whereCl = ImageEntry.IMAGE_TYPE + " = '" + QuizContract.Quizzes.TABLE_NAME +
                        "' AND " + ImageEntry.TYPE_ID + " = " + quizId;
                Cursor result = mDbHelper.getReadableDatabase().query(
                        ImageEntry.TABLE_NAME,
                        projection,
                        whereCl,
                        null,
                        null,
                        null,
                        sortOrder);
                if (result != null && result.getCount() > 0) {
                    return result;
                } else {
                    if (result != null) {
                        result.close();
                    }
                    Cursor quizData = mDbHelper.getReadableDatabase().query(
                            QuizContract.Quizzes.TABLE_NAME,
                            new String[] {QuizContract.Quizzes.QUIZ_PHOTO_URI},
                            QuizContract.Quizzes.ID_QUIZ + " = " + quizId,
                            null,
                            null,
                            null,
                            null
                    );
                    ContentValues values = new ContentValues();
                    values.put(ImageEntry.IMAGE_TYPE, QuizContract.Quizzes.TABLE_NAME);
                    values.put(ImageEntry.TYPE_ID, quizId);

                    if (quizData != null) {
                        if (quizData.getCount() == 1 && quizData.moveToFirst()) {
                            String imageURL = quizData.getString(quizData.getColumnIndex(QuizContract.Quizzes.QUIZ_PHOTO_URI));
                            String imageUri = loadImageFromUrl(imageURL, quizId, QuizContract.Quizzes.TABLE_NAME);
                            values.put(ImageEntry.IMAGE_URI, imageUri);
                            values.put(ImageEntry.IMAGE_URL, imageURL);
                        }
                        quizData.close();
                    }
                    mDbHelper.getWritableDatabase().insert(
                            ImageEntry.TABLE_NAME,
                            null,
                            values);
                    return mDbHelper.getReadableDatabase().query(
                            ImageEntry.TABLE_NAME,
                            projection,
                            whereCl,
                            null,
                            null,
                            null,
                            sortOrder
                    );
                }
        }
        return null;
    }

    private String loadImageFromUrl(String imageUrl, long id, String type) {
        FileOutputStream fileOutputStream = null;
        File imageDir = getContext().getDir("images", Context.MODE_PRIVATE);
        File image = new File (imageDir, type + "_" + id + ".png");
        try {
            InputStream in = new java.net.URL(imageUrl).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);


            fileOutputStream = new FileOutputStream(image);

            bitmap.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
            bitmap.recycle();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "loadImageFromUrl: ", e);
                }
            }
        }
        return image.getPath();
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
