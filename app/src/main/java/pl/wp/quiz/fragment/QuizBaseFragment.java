package pl.wp.quiz.fragment;

import android.app.Fragment;
import android.database.Cursor;

import java.util.List;

import pl.wp.quiz.listener.LoadDataListener;
import pl.wp.quiz.model.QuizModel;

/**
 * @author Krzysztof Betlej <labiod@wp.pl>.
 * @date 2/20/18
 */

public abstract class QuizBaseFragment extends Fragment implements LoadDataListener<Cursor> {

    public void onBackPressed() {

    }
}
