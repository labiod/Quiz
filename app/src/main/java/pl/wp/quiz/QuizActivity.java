package pl.wp.quiz;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import pl.wp.quiz.fragment.QuizBaseFragment;
import pl.wp.quiz.fragment.QuizDetailsFragment;
import pl.wp.quiz.fragment.QuizListFragment;
import pl.wp.quiz.fragment.QuizProgressFragment;
import pl.wp.quiz.listener.LoadDataListener;
import pl.wp.quiz.provider.QuizContract;
import pl.wp.quiz.synchronizer.SyncDataReceiver;

public class QuizActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, QuizApplication.OnDatabaseSynchronizedListener, LoadDataListener<Cursor> {

    public static final String TAG = QuizActivity.class.getSimpleName();
    public static final String QUIZ_ID = "quiz_id";
    public static final int QUIZZES_LOAD = 1;
    public static final int QUIZ_DETAILS_LOAD = 2;
    public static final int QUIZ_QUESTION_LOAD = 3;
    private static final int CHECK_DATA = 4;
    public static final String Q_PROGRESS = "quiz_progress";

    public static final String QUIZ_LIST_FRAGMENT_TAG = "quiz_list_fragment_tag";
    public static final String QUIZ_DETAILS_FRAGMENT_TAG = "quiz_details_fragment_tag";
    public static final String QUIZ_PROGRESS_FRAGMENT_TAG = "quiz_progress_fragment_tag";
    public static final String QUIZ_MODEL = "QUIZ_MODEL";

    private QuizBaseFragment mCurrentFragment;
    private final Handler mHandler = new Handler();
    private String mFragmentToLoad;
    private View mProgressView;
    private Bundle mFragmentArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_main);
        mProgressView = findViewById(R.id.sync_data_progress);
        mProgressView.setVisibility(View.VISIBLE);
        mFragmentToLoad = QUIZ_LIST_FRAGMENT_TAG;
        mFragmentArgs = new Bundle();
        loadQuizzes(QUIZZES_LOAD, null);
    }

    public void loadQuizzes(int loaderId, Bundle args) {
        if (getLoaderManager().getLoader(loaderId) == null) {
            getLoaderManager().initLoader(loaderId, args, this);
        } else {
            getLoaderManager().restartLoader(loaderId, args, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case QUIZZES_LOAD:
            case CHECK_DATA:
                Uri quizzesUri = Uri.withAppendedPath(QuizContract.CONTENT_URI, "/" + QuizContract.Quizzes.TABLE_NAME);

                return new CursorLoader(this, quizzesUri,
                        null, "", null,
                        QuizContract.Quizzes.QUIZ_CREATED_AT + " COLLATE LOCALIZED ASC");
            case QUIZ_DETAILS_LOAD:
                Uri baseUri = Uri.withAppendedPath(QuizContract.CONTENT_URI, "/"
                        + QuizContract.Quizzes.TABLE_NAME
                        + "/" + args.getLong(QUIZ_ID));

                String select = QuizContract.Quizzes.ID_QUIZ + " = " + args.getLong(QUIZ_ID);
                return new CursorLoader(this,
                        baseUri,
                        null, select,
                        null,
                        QuizContract.Quizzes.QUIZ_CREATED_AT + " COLLATE LOCALIZED ASC");
            case QUIZ_QUESTION_LOAD:
                Uri questionUri = Uri.withAppendedPath(QuizContract.CONTENT_URI,
                        QuizContract.QUESTION_WITH_ANSWER);

                String qSelect = QuizContract.QuizQuestions.QUIZ_ID + " = " + args.getLong(QUIZ_ID);
                return new CursorLoader(this,
                        questionUri,
                        null, qSelect,
                        null,
                        QuizContract.QuizQuestions.QUESTION_ORDER + " COLLATE LOCALIZED ASC");
        }
        return null;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        mCurrentFragment = (QuizBaseFragment) fragment;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mCurrentFragment != null) {
            mCurrentFragment.onLoadData(data);
        } else {
            onLoadData(data);
        }

    }

    public void loadQuizDetailsFragment(long quizId) {
        QuizDetailsFragment fragment = new QuizDetailsFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(QUIZ_ID, quizId);
        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadData(Cursor dataList) {
        if (dataList == null || dataList.getCount() == 0) {
            SyncDataReceiver.startDataSynchronize(this, this);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadFragmentByName(mFragmentToLoad, mFragmentArgs);
                    mProgressView.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onDatabaseSynchronized() {
        loadFragmentByName(mFragmentToLoad, mFragmentArgs);
        mProgressView.setVisibility(View.GONE);
    }

    public void loadQuizProgressFragment(long quizId, int quizProgress) {
        QuizProgressFragment fragment = new QuizProgressFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(QUIZ_ID, quizId);
        arguments.putInt(Q_PROGRESS, quizProgress);
        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
    }

    public void loadFragmentByName(String fragmentToLoad, Bundle arguments) {
        switch (fragmentToLoad) {
            case QUIZ_LIST_FRAGMENT_TAG:
                QuizListFragment fragment = new QuizListFragment();
                fragment.setArguments(arguments);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
                break;
            case QUIZ_DETAILS_FRAGMENT_TAG:
                loadQuizDetailsFragment(arguments.getLong(QUIZ_ID));
                break;
            case QUIZ_PROGRESS_FRAGMENT_TAG:
                loadQuizProgressFragment(arguments.getLong(QUIZ_ID), arguments.getInt(Q_PROGRESS));
                break;
        }
    }
}
