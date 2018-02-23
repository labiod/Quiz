package pl.wp.quiz;

import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import pl.wp.quiz.fragment.QuizBaseFragment;
import pl.wp.quiz.fragment.QuizDetailsFragment;
import pl.wp.quiz.fragment.QuizListFragment;
import pl.wp.quiz.fragment.QuizProgressFragment;
import pl.wp.quiz.listener.LoadDataListener;
import pl.wp.quiz.model.UserAnswers;
import pl.wp.quiz.provider.database.ImageContract;
import pl.wp.quiz.provider.database.QuizContract;
import pl.wp.quiz.synchronizer.SyncDataReceiver;

public class QuizActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        QuizApplication.OnDatabaseSynchronizedListener, LoadDataListener<Cursor> {

    public static final String QUIZ_ID = "quiz_id";
    public static final int QUIZZES_LOAD = 1;
    public static final int QUIZ_DETAILS_LOAD = 2;
    public static final int QUIZ_QUESTION_LOAD = 3;
    public static final int CHECK_DATA = 4;
    public static final int QUIZ_IMAGE_LOAD = 5;
    public static final String Q_PROGRESS = "quiz_progress";

    public static final String QUIZ_LIST_FRAGMENT_TAG = "quiz_list_fragment_tag";
    public static final String QUIZ_DETAILS_FRAGMENT_TAG = "quiz_details_fragment_tag";
    public static final String QUIZ_PROGRESS_FRAGMENT_TAG = "quiz_progress_fragment_tag";
    public static final String USER_ANSWERS = "user_answers";
    public static final String QUERY_SELECTION = "query_selection";

    private QuizBaseFragment mCurrentFragment;
    private final Handler mHandler = new Handler();
    private String mFragmentToLoad;
    private TextView mProgressViewInfo;
    private ProgressBar mProgressView;
    private Bundle mFragmentArgs;
    private View mContainer;
    private View mLoadingView;
    private View mLoadingDataContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_main);
        mContainer = findViewById(R.id.container);
        mLoadingView = findViewById(R.id.loading_progress);
        mLoadingDataContainer = findViewById(R.id.loading_data_container);
        mProgressViewInfo = findViewById(R.id.progress_info);
        mProgressView = findViewById(R.id.sync_data_progress);
        mLoadingDataContainer.setVisibility(View.VISIBLE);
        mFragmentToLoad = QUIZ_LIST_FRAGMENT_TAG;
        mFragmentArgs = new Bundle();
        loadQuizzes(QUIZZES_LOAD, null);
    }

    public void loadQuizzes(int loaderId, Bundle args) {
        loadQuizzes(loaderId, args, this);
    }

    public void loadQuizzes(int loaderId, Bundle args, LoaderManager.LoaderCallbacks<Cursor> callbacks) {
        if (getLoaderManager().getLoader(loaderId) == null) {
            getLoaderManager().initLoader(loaderId, args, callbacks);
        } else {
            getLoaderManager().restartLoader(loaderId, args, callbacks);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragment != null && mCurrentFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
        mCurrentFragment = null;
        mFragmentToLoad = QUIZ_LIST_FRAGMENT_TAG;
    }

    public void finishQuiz(final UserAnswers userAnswers, final int progress) {
                getSupportFragmentManager().popBackStack(QUIZ_LIST_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mCurrentFragment = null;
        mFragmentToLoad = QUIZ_DETAILS_FRAGMENT_TAG;
        final Bundle args = new Bundle();
        args.putParcelable(USER_ANSWERS, userAnswers);
        args.putInt(Q_PROGRESS, progress);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                userAnswers.publishChanges(QuizActivity.this);
                updateQuizProgress(progress, userAnswers.getQuizId(), userAnswers.getCorrectAnswers());
                if (progress < userAnswers.getQuestionNumber()) {
                    loadQuizzesList();
                } else {
                    mContainer.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.VISIBLE);
                    loadQuizDetailsFragment(userAnswers.getQuizId());
                }
            }
        });
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
                        QuizContract.UsersAnswers.ANSWER_DATE + " DESC");
            case QUIZ_QUESTION_LOAD:
                Uri questionUri = Uri.withAppendedPath(QuizContract.CONTENT_URI,
                        QuizContract.QUESTION_WITH_ANSWER);

                String qSelect = QuizContract.QuizQuestions.QUIZ_ID + " = " + args.getLong(QUIZ_ID);
                return new CursorLoader(this,
                        questionUri,
                        null, qSelect,
                        null,
                        QuizContract.QuizQuestions.QUESTION_ORDER + " COLLATE LOCALIZED ASC");
            case QUIZ_IMAGE_LOAD:
                String selection = args.getString(QUERY_SELECTION);
                long quiz_id = args.getLong(QUIZ_ID);
                Uri imageUri = Uri.withAppendedPath(ImageContract.CONTENT_URI, "quizzes" + "/" + quiz_id);
                return new CursorLoader(this,
                        imageUri,
                        null,
                        null,
                        null,
                        null);
        }
        return null;
    }

    @Override
    public void onAttachFragment(android.support.v4.app.Fragment fragment) {
        mCurrentFragment = (QuizBaseFragment) fragment;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mCurrentFragment != null) {
            mCurrentFragment.onLoadData(data, loader.getId());
        } else {
            onLoadData(data, loader.getId());
        }
    }

    public void hideLoadingScreen() {
        mContainer.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
    }

    public QuizBaseFragment loadQuizDetailsFragment(long quizId) {
        QuizDetailsFragment fragment = new QuizDetailsFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(QUIZ_ID, quizId);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(QUIZ_DETAILS_FRAGMENT_TAG)
                .commit();
        return fragment;
    }

    public QuizBaseFragment loadQuizzesList() {
        QuizListFragment fragment = new QuizListFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(QUIZ_LIST_FRAGMENT_TAG)
                .commit();
        return fragment;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadData(Cursor dataList, int type) {
        if (dataList == null || dataList.getCount() == 0) {
            SyncDataReceiver.startDataSynchronize(this, this);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadFragmentByName(mFragmentToLoad, mFragmentArgs);
                    mLoadingDataContainer.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.db_test:
                startActivity(new Intent(this, DataBaseTestActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDatabaseSynchronized() {
        loadFragmentByName(mFragmentToLoad, mFragmentArgs);
        mLoadingDataContainer.setVisibility(View.GONE);
    }

    @Override
    public void onDatabaseSyncProgress(int progress) {
        mProgressView.setProgress(progress);
    }

    public void loadQuizProgressFragment(long quizId, int quizProgress) {
        QuizProgressFragment fragment = new QuizProgressFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(QUIZ_ID, quizId);
        arguments.putInt(Q_PROGRESS, quizProgress);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(QUIZ_PROGRESS_FRAGMENT_TAG)
                .commit();
    }

    public void loadFragmentByName(String fragmentToLoad, Bundle arguments) {
        switch (fragmentToLoad) {
            case QUIZ_LIST_FRAGMENT_TAG:
                QuizListFragment fragment = new QuizListFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(QUIZ_LIST_FRAGMENT_TAG)
                        .commit();
                break;
            case QUIZ_DETAILS_FRAGMENT_TAG:
                loadQuizDetailsFragment(arguments.getLong(QUIZ_ID));
                break;
            case QUIZ_PROGRESS_FRAGMENT_TAG:
                loadQuizProgressFragment(arguments.getLong(QUIZ_ID), arguments.getInt(Q_PROGRESS));
                break;
        }
    }

    private void updateQuizProgress(int progress, long quizId, int correctAnswer) {
        Uri uri = Uri.withAppendedPath(QuizContract.CONTENT_URI, QuizContract.Quizzes.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(QuizContract.Quizzes.QUIZ_PROGRESS, progress);
        values.put(QuizContract.Quizzes.LAST_RESULT, correctAnswer);
        getContentResolver().update(uri, values, QuizContract.Quizzes.ID_QUIZ + " = " + quizId, null);
    }
}
