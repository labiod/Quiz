package pl.wp.quiz;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import pl.wp.quiz.provider.database.QuizContract;

/**
 * @author Krzysztof Betlej <labiod@wp.pl>.
 * @date 2/21/18
 */

public class DataBaseTestActivity extends Activity {
    private static final String[] QUERY_TYPES = {
            QuizContract.Quizzes.TABLE_NAME,
            QuizContract.QuizQuestions.TABLE_NAME,
            QuizContract.QuestionAnswers.TABLE_NAME,
            QuizContract.QuizRates.TABLE_NAME,
            QuizContract.UsersAnswers.TABLE_NAME,
            QuizContract.QUESTION_WITH_ANSWER
    };

    private ArrayAdapter<String> mAdapter;
    private ArrayAdapter<String> mSpinnerAdapter;
    private TextView mMSelectionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_test_layout);
        mMSelectionText = findViewById(R.id.selection_text);
        ListView listView = findViewById(R.id.table_content);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.test_list_item);
        listView.setAdapter(mAdapter);

        Spinner spinner = findViewById(R.id.table_chooser);
        mSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, QUERY_TYPES);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadData(QUERY_TYPES[position], mMSelectionText.getText().toString());            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadData(String tableType, String selection) {
        Uri uri = Uri.withAppendedPath(QuizContract.CONTENT_URI, tableType);
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        List<String> content = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < cursor.getColumnCount(); ++i) {
                        if (i != 0) {
                            builder.append(";");
                        }
                        builder.append(cursor.getColumnName(i) + ": " + cursor.getString(i));
                    }
                    content.add(builder.toString());
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        mAdapter.clear();
        mAdapter.addAll(content);
    }
}
