package pl.wp.quiz.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pl.wp.quiz.QuizActivity;
import pl.wp.quiz.R;
import pl.wp.quiz.adapter.QuizDetailsAdapter;
import pl.wp.quiz.model.QuizModel;

/**
 * @author Krzysztof Betlej <labiod@wp.pl>.
 * @date 2/20/18
 */

public class QuizListFragment extends QuizBaseFragment implements QuizDetailsAdapter.OnItemClickListener {
    public static final String TAG = QuizListFragment.class.getSimpleName();

    private QuizDetailsAdapter mQuizDetailsAdapter;

    public QuizListFragment() {
        mQuizDetailsAdapter = new QuizDetailsAdapter(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mQuizDetailsAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mQuizDetailsAdapter.removeOnItemClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.quiz_list, container, false);
        RecyclerView quizzesList = root.findViewById(R.id.quizzes_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(container.getContext());
        quizzesList.setLayoutManager(layoutManager);
        quizzesList.setAdapter(mQuizDetailsAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            ((QuizActivity) getActivity()).loadQuizzes(QuizActivity.QUIZZES_LOAD, null);
        }
    }

    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStackImmediate(QuizActivity.QUIZ_LIST_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        return super.onBackPressed();
    }

    @Override
    public void onLoadData(Cursor cursor) {
        List<QuizModel> quizModels = reteiveQuizzesForCursor(cursor);
        mQuizDetailsAdapter.setQuizzes(quizModels);
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: click on item:" + position);
        QuizModel model = mQuizDetailsAdapter.getItem(position);
        if (model.isFinished()) {
            ((QuizActivity) getActivity()).loadQuizDetailsFragment(model.getId());
        } else {
            ((QuizActivity) getActivity()).loadQuizProgressFragment(model.getId(), model.getProgress());
        }
    }

    private List<QuizModel> reteiveQuizzesForCursor(Cursor data) {
        List<QuizModel> result = new ArrayList<>();
        if (data != null) {
            Log.d(TAG, "retreiveQuizzesForCursor: cursor count = " + data.getCount());
            if (data.moveToFirst()) {
                do {
                    QuizModel model = new QuizModel(data);
                    result.add(model);
                } while(data.moveToNext());
            }
            data.close();
        }
        return result;
    }
}
