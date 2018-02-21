package pl.wp.quiz.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import pl.wp.quiz.provider.database.QuizContract;

/**
 * Created by labio on 18.02.2018.
 */

public class QuestionModel {
    private final long mQuizId;
    private final long mId;
    private List<AnswerModel> mAnswers = new ArrayList<>();
    private int mCorrectAnswer;
    private String mQuestionText;
    private String mQuestionType;
    private String mImageURI;
    private int mQuestionOrder;

    public QuestionModel(Cursor data) {
        mId = data.getLong(data.getColumnIndex(QuizContract.QuizQuestions.ID_QUESTION));
        mQuestionText = data.getString(data.getColumnIndex(QuizContract.QuizQuestions.QUESTION_TEXT));
        mQuestionType = data.getString(data.getColumnIndex(QuizContract.QuizQuestions.QUESTION_TYPE));
        mImageURI = data.getString(data.getColumnIndex(QuizContract.QuizQuestions.QUESTION_IMAGE_URI));
        mQuizId = data.getLong(data.getColumnIndex(QuizContract.QuizQuestions.QUIZ_ID));
        mQuestionOrder = data.getInt(data.getColumnIndex(QuizContract.QuizQuestions.QUESTION_ORDER));
    }

    public String getQuestionText() {
        return mQuestionText;
    }

    public List<AnswerModel> getAnswers() {
        return mAnswers;
    }

    public long getQuizId() {
        return mQuizId;
    }

    public void addAnswer(AnswerModel answerModel) {
        mAnswers.add(answerModel);
        if (answerModel.isCorrect()) {
            mCorrectAnswer = answerModel.getOrder();
        }
    }
}
