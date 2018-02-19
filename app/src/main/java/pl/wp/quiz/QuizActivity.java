package pl.wp.quiz;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pl.wp.quiz.adapter.QuizDetailsAdapter;
import pl.wp.quiz.model.QuizModel;
import pl.wp.quiz.provider.QuizContract;
import pl.wp.quiz.synchronizer.SyncDataReceiver;

public class QuizActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int QUIZZES_LOAD = 1;
    public static final String TAG = QuizActivity.class.getSimpleName();
    private QuizDetailsAdapter mQuizDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_main);
        ListView quizzesList = findViewById(R.id.quizzes_list);
        mQuizDetailsAdapter = new QuizDetailsAdapter(null);
        quizzesList.setAdapter(mQuizDetailsAdapter);
        loadQuizzes();
        SyncDataReceiver.startDataSynchronize(this);
    }

    private void loadQuizzes() {
        getLoaderManager().initLoader(QUIZZES_LOAD, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.withAppendedPath(QuizContract.CONTENT_URI, "/" + QuizContract.Quizzes.TABLE_NAME);

        String select = "";
        return new CursorLoader(this, baseUri,
                null, select, null,
                QuizContract.Quizzes.QUIZ_CREATED_AT + " COLLATE LOCALIZED ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<QuizModel> quizModels = reteiveQuizzesForCursor(data);
        mQuizDetailsAdapter.setQuizzes(quizModels);
        mQuizDetailsAdapter.notifyDataSetChanged();
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

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
