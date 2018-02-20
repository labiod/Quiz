package pl.wp.quiz.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pl.wp.quiz.QuizActivity;
import pl.wp.quiz.R;
import pl.wp.quiz.provider.QuizContract;

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
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            int correctAnswer = cursor.getInt(cursor.getColumnIndex("correct_answer"));
            int questionNumber = cursor.getInt(cursor.getColumnIndex(QuizContract.Quizzes.QUESTION_NUMBER));
            if (questionNumber != 0) {
                mQuizResultView.setText((correctAnswer / questionNumber));
            }
            //float rate = cursor.getFloat(cursor.getColumnIndex("user_rates"));
        }
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
        Log.d(TAG, "onCreateView: log arguments :" + getArguments().getLong(QuizActivity.QUIZ_ID));
        return root;
    }
}
