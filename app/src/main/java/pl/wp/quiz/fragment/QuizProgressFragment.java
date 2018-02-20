package pl.wp.quiz.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.wp.quiz.QuizActivity;
import pl.wp.quiz.R;
import pl.wp.quiz.model.AnswerModel;
import pl.wp.quiz.model.QuestionModel;
import pl.wp.quiz.provider.QuizContract;

/**
 * @author Krzysztof Betlej <labiod@wp.pl>.
 * @date 2/20/18
 */

public class QuizProgressFragment extends QuizBaseFragment {
    public static final String TAG = QuizDetailsFragment.class.getSimpleName();
    private static final int[] ANSERW_IDS = {R.id.answer_1, R.id.answer_2, R.id.answer_3, R.id.answer_4};
    private int mProgress  = 0;
    private List<QuestionModel> mQuestionList;

    private TextView mQuestionText;
    private TextView mQuestionProgress;
    private RadioGroup mQuestionAnswers;

    @Override
    public void onLoadData(Cursor cursor) {
        mQuestionList = retreiveQuestionsFromCursor(cursor);
        initView(mQuestionList.get(mProgress));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((QuizActivity)getActivity()).loadQuizzes(QuizActivity.QUIZ_QUESTION_LOAD, getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.quiz_progress, container, false);
        mQuestionText = root.findViewById(R.id.question_text_view);
        mQuestionProgress = root.findViewById(R.id.question_progress);
        mQuestionAnswers = root.findViewById(R.id.question_answers);
        mQuestionAnswers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                saveAnswer(checkedId);
                nextQuestion();
            }
        });
        return root;
    }

    private void nextQuestion() {
        mProgress += 1;
        if (mProgress >= mQuestionList.size()) {
            finishQuiz();
        } else {
            initView(mQuestionList.get(mProgress));
        }
    }

    private void initView(QuestionModel questionModel) {
        mQuestionText.setText(questionModel.getQuestionText());
        mQuestionAnswers.removeAllViews();
        String progressText = (mProgress + 1) + "/" + mQuestionList.size();
        mQuestionProgress.setText(progressText);
        List<AnswerModel> answers = questionModel.getAnswers();
        for (int i = 0; i < answers.size(); ++i) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setId(ANSERW_IDS[i]);
            radioButton.setText(answers.get(i).getAnswerText());
            radioButton.setTag(answers.get(i));
            mQuestionAnswers.addView(radioButton);
        }
        getView().invalidate();
    }

    private void finishQuiz() {
        ((QuizActivity)getActivity()).loadQuizDetailsFragment(mQuestionList.get(0).getQuizId());
    }

    private void saveAnswer(int checkedId) {
        //TODO: save to database
        AnswerModel questionAnswers = (AnswerModel) mQuestionAnswers.findViewById(checkedId).getTag();


    }

    private List<QuestionModel> retreiveQuestionsFromCursor(Cursor data) {
        List<QuestionModel> result = new ArrayList<>();
        int actualOrder = 0;
        QuestionModel actualModel = null;
        if (data != null) {
            Log.d(TAG, "retreiveQuizzesForCursor: cursor count = " + data.getCount());
            if (data.moveToFirst()) {
                do {
                    int order = data.getInt(data.getColumnIndex(QuizContract.QuizQuestions.QUESTION_ORDER));
                    Log.d(TAG, "retreiveQuestionsFromCursor: order: " + order);
                    if (order != actualOrder || actualModel == null) {
                        if (actualModel != null) {
                            result.add(actualModel);
                        }
                        actualModel = new QuestionModel(data);
                        actualOrder = order;
                    }
                    AnswerModel answerModel = new AnswerModel(data);
                    actualModel.addAnswer(answerModel);

                } while(data.moveToNext());
            }
            data.close();
        }
        return result;
    }
}
