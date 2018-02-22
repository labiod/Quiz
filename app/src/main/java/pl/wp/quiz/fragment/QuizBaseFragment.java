package pl.wp.quiz.fragment;

import android.database.Cursor;
import android.support.v4.app.Fragment;

import pl.wp.quiz.listener.LoadDataListener;

/**
 * @author Krzysztof Betlej <labiod@wp.pl>.
 * @date 2/20/18
 */

public abstract class QuizBaseFragment extends Fragment implements LoadDataListener<Cursor> {

    public boolean onBackPressed() {
        return false;
    }
}
