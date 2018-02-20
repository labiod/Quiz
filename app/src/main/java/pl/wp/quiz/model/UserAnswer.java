package pl.wp.quiz.model;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import pl.wp.quiz.provider.QuizContract;

/**
 * @author Krzysztof Betlej <k.betlej@samsung.com>.
 * @date 2/20/18
 * @copyright Copyright (c) 2016 by Samsung Electronics Polska Sp. z o. o.
 */

public class UserAnswer {
    private long mId = 0;
    private long mAnswerId;
    public UserAnswer(long answerId) {
        mAnswerId = answerId;
    }
    public void publishChanges(Context context) {
        Uri uri = Uri.withAppendedPath(QuizContract.CONTENT_URI, QuizContract.UsersAnswers.TABLE_NAME);
        if (mId == 0) {
            Uri newId = context.getContentResolver().insert(uri, toContentValue());
            mId = Long.parseLong(newId.getLastPathSegment());
        } else {
            context.getContentResolver().update(uri, toContentValue(), QuizContract.UsersAnswers.ID + " = " + mId, null);
        }

    }

    private ContentValues toContentValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizContract.UsersAnswers.ANSWER_ID, mAnswerId);
        contentValues.put(QuizContract.UsersAnswers.ANSWER_DATE, System.currentTimeMillis());
        return contentValues;
    }


}
