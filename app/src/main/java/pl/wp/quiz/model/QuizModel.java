package pl.wp.quiz.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class QuizModel {
    private int mQuestionNumber;
    private String mQuizTest;
    private String mLastResultInfo;
    private String mQuizImageURI;
    private boolean mFinished;

    /**
     * Constructor used only for mockup
     * @param quizTest - name of current quiz
     * @param lastResultInfo - info about
     * @param quizImageURI - quiz image url
     * @param finished - true if last result is finished, false otherwise
     */
    public QuizModel(String quizTest, String lastResultInfo, String quizImageURI, boolean finished) {
        mQuizTest = quizTest;
        mLastResultInfo = lastResultInfo;
        mQuizImageURI = quizImageURI;
        mFinished = finished;
    }

    public QuizModel(Cursor cursor) {

    }

    public boolean isFinished() {
        return mFinished;
    }

    public String getQuizImageURI() {
        return mQuizImageURI;
    }

    public String getLastResultInfo() {
        return mLastResultInfo;
    }

    public String getQuizTitle() {
        return mQuizTest;
    }

    public static List<QuizModel> generateModels(int size) {
        List<QuizModel> result = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            result.add(new QuizModel("Test" + i, "Ostatni wynik 8/10 80%", "", true));
        }
        return result;
    }
}
