package pl.wp.quiz.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import static pl.wp.quiz.provider.QuizContract.*;

public class QuizModel implements Parcelable {
    public static final Creator<QuizModel> CREATOR = new Creator<QuizModel>() {
        @Override
        public QuizModel createFromParcel(Parcel source) {
            return new QuizModel(source);
        }

        @Override
        public QuizModel[] newArray(int size) {
            return new QuizModel[0];
        }
    };
    private int mQuestionNumber;
    private String mQuizTitle;
    private String mLastResultInfo;
    private String mQuizImageURI;
    private String mQuizCategory;
    private long mCreatedDate;
    private long mId;
    private int mProgress;

    /**
     * Constructor used only for mockup
     * @param quizTitle - name of current quiz
     * @param lastResultInfo - info about
     * @param quizImageURI - quiz image url
     * @param finished - true if last result is finished, false otherwise
     */
    public QuizModel(String quizTitle, String lastResultInfo, String quizImageURI, boolean finished) {
        mQuizTitle = quizTitle;
        mLastResultInfo = lastResultInfo;
        mQuizImageURI = quizImageURI;
    }

    public QuizModel(Cursor cursor) {
        mId = cursor.getLong(cursor.getColumnIndex(Quizzes.ID_QUIZ));
        mQuizTitle = cursor.getString(cursor.getColumnIndex(Quizzes.QUIZ_TITLE));
        mQuizImageURI = cursor.getString(cursor.getColumnIndex(Quizzes.QUIZ_PHOTO_URI));
        mQuestionNumber = cursor.getInt(cursor.getColumnIndex(Quizzes.QUESTION_NUMBER));
        mQuizCategory = cursor.getString(cursor.getColumnIndex(Quizzes.QUIZ_CATEGORY));
        mCreatedDate = cursor.getLong(cursor.getColumnIndex(Quizzes.QUIZ_CATEGORY));
        mProgress = cursor.getInt(cursor.getColumnIndex(Quizzes.QUIZ_PROGRESS));
    }

    public QuizModel(Parcel parcel) {
        mId = parcel.readLong();
        mQuizTitle = parcel.readString();
        mQuizImageURI = parcel.readString();
        mQuestionNumber = parcel.readInt();
        mQuizCategory = parcel.readString();
        mCreatedDate = parcel.readLong();
        mProgress = parcel.readInt();
    }

    public String getQuizImageURI() {
        return mQuizImageURI;
    }

    public String getLastResultInfo() {
        return mLastResultInfo == null ? "Brak rozwiązania" : mLastResultInfo;
    }

    public String getQuizTitle() {
        return mQuizTitle;
    }

    public String getQuizCategory() {
        return mQuizCategory;
    }

    public long getCreatedDate() {
        return mCreatedDate;
    }

    public int getQuestionNumber() {
        return mQuestionNumber;
    }

    public long getId() {
        return mId;
    }

    public boolean isFinished() {
        return mProgress == mQuestionNumber;
    }

    public int getProgress() {
        return mProgress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mQuizTitle);
        dest.writeString(mQuizImageURI);
        dest.writeInt(mQuestionNumber);
        dest.writeString(mQuizCategory);
        dest.writeLong(mCreatedDate);
        dest.writeInt(mProgress);
    }
}
