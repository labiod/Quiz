package pl.wp.quiz.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.wp.quiz.QuizActivity;
import pl.wp.quiz.QuizApplication;
import pl.wp.quiz.R;
import pl.wp.quiz.synchronizer.SyncDataReceiver;

/**
 * @author Krzysztof Betlej <labiod@wp.pl>.
 * @date 2/20/18
 */

public class LoadingDataFragment extends QuizBaseFragment implements QuizApplication.OnDatabaseSynchronizedListener {

    public static final String LOADER_TYPE = "loader_type";
    public static final String FRAGMENT_TO_LOAD = "fragment_to_load";

    private String mFragmentToLoad;
    private static Handler mHandler = new Handler();

    @Override
    public void onLoadData(Cursor dataList) {
        if (dataList == null || dataList.getCount() == 0) {
            SyncDataReceiver.startDataSynchronize(getActivity(), this);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ((QuizActivity)getActivity()).loadFragmentByName(mFragmentToLoad, getArguments());
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        int loaderType = getArguments().getInt(LOADER_TYPE);
        mFragmentToLoad = getArguments().getString(FRAGMENT_TO_LOAD);
        ((QuizActivity)getActivity()).loadQuizzes(loaderType, getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.quiz_loading_data_layout, container, false);
        return root;
    }

    @Override
    public void onDatabaseSynchronized() {
        ((QuizActivity)getActivity()).loadFragmentByName(mFragmentToLoad, getArguments());
    }
}
