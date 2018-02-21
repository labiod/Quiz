package pl.wp.quiz.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pl.wp.quiz.QuizActivity;
import pl.wp.quiz.R;
import pl.wp.quiz.provider.database.QuizContract;

/**
 * @author Krzysztof Betlej <labiod@wp.pl>.
 * @date 2/20/18
 */

public class QuizDetailsFragment extends QuizBaseFragment {

    public static final String TAG = QuizDetailsFragment.class.getSimpleName();
    private TextView mQuizResultView;
    private TextView mQuizRate;

    @Override
    public void onLoadData(Cursor cursor) {
        ((QuizActivity)getActivity()).hideLoadingScreen();
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                int correctAnswer = getCorrectAnswer(cursor);
                int questionNumber = cursor.getInt(cursor.getColumnIndex(QuizContract.Quizzes.QUESTION_NUMBER));
                if (questionNumber != 0) {
                    String quizResult = String.valueOf(correctAnswer * 100 / questionNumber) + " %";
                    mQuizResultView.setText(quizResult);
                }
                //float rate = cursor.getFloat(cursor.getColumnIndex("user_rates"));
            }
            cursor.close();
        }
    }

    private int getCorrectAnswer(Cursor cursor) {
        String[] answerList = cursor.getString(cursor.getColumnIndex(QuizContract.UsersAnswers.ANSWERS_LIST))
                .split(QuizContract.UsersAnswers.ANSWER_SEPARATOR);
        int count = 0;
        for (String answer: answerList) {
            if (answer.equals("1")) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((QuizActivity)getActivity()).loadQuizzes(QuizActivity.QUIZ_DETAILS_LOAD, getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.quiz_details, container, false);
        mQuizResultView = root.findViewById(R.id.quiz_result);
        mQuizRate = root.findViewById(R.id.rate_text);
        Button back = root.findViewById(R.id.back_to_list);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ((QuizActivity)getActivity()).loadQuizzesList();
            }
        });
        Button repeatQuiz = root.findViewById(R.id.repeat_quiz);
        repeatQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ((QuizActivity)getActivity()).loadQuizProgressFragment(getArguments().getLong(QuizActivity.QUIZ_ID), 0);
            }
        });
        Log.d(TAG, "onCreateView: log arguments :" + getArguments().getLong(QuizActivity.QUIZ_ID));
        return root;
    }
}
