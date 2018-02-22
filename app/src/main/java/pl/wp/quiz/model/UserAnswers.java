package pl.wp.quiz.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import pl.wp.quiz.provider.database.QuizContract;

/**
 * @author Krzysztof Betlej <k.betlej@samsung.com>.
 * @date 2/20/18
 * @copyright Copyright (c) 2016 by Samsung Electronics Polska Sp. z o. o.
 */

public class UserAnswers implements Parcelable {
    public static final Creator<UserAnswers> CREATOR = new Creator<UserAnswers>() {
        @Override
        public UserAnswers createFromParcel(Parcel source) {
            return new UserAnswers(source);
        }

        @Override
        public UserAnswers[] newArray(int size) {
            return new UserAnswers[0];
        }
    };
    private long mId = 0;
    private long mQuizId;
    private final int[] mList;
    private int mQuestionNumber;
    private int mAnwserIndex;

    public UserAnswers(long quizId, int questionNumber) {
        mQuizId = quizId;
        mList = new int[questionNumber];
        mAnwserIndex = 0;
        mQuestionNumber = questionNumber;
    }

    public UserAnswers(Parcel source) {
        mId = source.readLong();
        mQuizId = source.readLong();
        mAnwserIndex = source.readInt();
        mQuestionNumber = source.readInt();
        mList = new int[mQuestionNumber];
        source.readIntArray(mList);
    }

    public UserAnswers(Cursor cursor) {
        mId = cursor.getLong(cursor.getColumnIndex(QuizContract.UsersAnswers.ID));
        mQuizId = cursor.getLong(cursor.getColumnIndex(QuizContract.UsersAnswers.QUIZ_ID));
        mAnwserIndex = cursor.getInt(cursor.getColumnIndex(QuizContract.UsersAnswers.ANSWER_PROGRESS));
        String[] answers = cursor.getString(cursor.getColumnIndex(QuizContract.UsersAnswers.ANSWERS_LIST)).split(QuizContract.UsersAnswers.ANSWER_SEPARATOR);
        mQuestionNumber = answers.length;
        mList = new int[mQuestionNumber];
        for(int i = 0; i < mQuestionNumber; ++i) {
            mList[i] = Integer.parseInt(answers[i]);
        }
    }

    public long getQuizId() {
        return mQuizId;
    }

    public int getQuestionNumber() {
        return mQuestionNumber;
    }

    public int getCorrectAnswers() {
        int result = 0;
        for (int i = 0; i < mQuestionNumber; ++i) {
            result += mList[i];
        }
        return result;
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

    public void putAnswer(int isCorrect) {
        mList[mAnwserIndex++] = isCorrect;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mQuizId);
        dest.writeInt(mAnwserIndex);
        dest.writeInt(mQuestionNumber);
        dest.writeIntArray(mList);
    }

    private ContentValues toContentValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizContract.UsersAnswers.QUIZ_ID, mQuizId);
        contentValues.put(QuizContract.UsersAnswers.ANSWERS_LIST, answersToList());
        contentValues.put(QuizContract.UsersAnswers.ANSWER_PROGRESS, mAnwserIndex);
        contentValues.put(QuizContract.UsersAnswers.ANSWER_DATE, System.currentTimeMillis());
        return contentValues;
    }

    private String answersToList() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mList.length; ++i) {
            if (i != 0) {
                builder.append(QuizContract.UsersAnswers.ANSWER_SEPARATOR);
            }
            builder.append(mList[i]);
        }
        return builder.toString();
    }


}
