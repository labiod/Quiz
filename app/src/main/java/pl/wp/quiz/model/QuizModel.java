package pl.wp.quiz.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import static pl.wp.quiz.provider.QuizContract.*;

public class QuizModel {
    private int mQuestionNumber;
    private String mQuizTitle;
    private String mLastResultInfo;
    private String mQuizImageURI;
    private String mQuizCategory;
    private long mCreatedDate;

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
        mQuizTitle = cursor.getString(cursor.getColumnIndex(Quizzes.QUIZ_TITLE));
        mQuizImageURI = cursor.getString(cursor.getColumnIndex(Quizzes.QUIZ_PHOTO_URI));
        mQuestionNumber = cursor.getInt(cursor.getColumnIndex(Quizzes.QUESTION_NUMBER));
        mQuizCategory = cursor.getString(cursor.getColumnIndex(Quizzes.QUIZ_CATEGORY));
        mCreatedDate = cursor.getLong(cursor.getColumnIndex(Quizzes.QUIZ_CATEGORY));
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
}
