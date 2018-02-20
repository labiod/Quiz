package pl.wp.quiz.model;

import android.database.Cursor;

import pl.wp.quiz.provider.QuizContract;

/**
 * @author Krzysztof Betlej <k.betlej@wp.pl>.
 * @date 2/20/18
 */

public class AnswerModel {
    private long mQuestionId;
    private long mId;
    private boolean mCorrected;
    private String mAnswerText;
    private int mOrder;

    public AnswerModel(Cursor cursor) {
        mId = cursor.getLong(cursor.getColumnIndex(QuizContract.QuestionAnswers.ID_ANSWER));
        mQuestionId = cursor.getLong(cursor.getColumnIndex(QuizContract.QuestionAnswers.QUESTION_ID));
        mCorrected = cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionAnswers.IS_CORRECT)) == 1;
        mOrder = cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionAnswers.ANSWER_ORDER));
        mAnswerText = cursor.getString(cursor.getColumnIndex(QuizContract.QuestionAnswers.ANSWER_TEXT));
    }

    public boolean isCorrect() {
        return mCorrected;
    }

    public int getOrder() {
        return mOrder;
    }

    public String getAnswerText() {
        return mAnswerText;
    }
}
