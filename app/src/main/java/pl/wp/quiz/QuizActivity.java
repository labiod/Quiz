package pl.wp.quiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Arrays;

import pl.wp.quiz.adapter.QuizDetailsAdapter;

public class QuizActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_main);
        ListView quizzesList = findViewById(R.id.quizzes_list);
        quizzesList.setAdapter(createQuizDataAdapter());
    }

    private ListAdapter createQuizDataAdapter() {
        String[] quizzesTmpList = new String[] {"Quiz1", "Quiz2", "Quiz3", "Quiz4", "Quiz5", "Quiz6", "Quiz7", "Quiz8", "Quiz9", "Quiz10" };
        return new QuizDetailsAdapter(Arrays.asList(quizzesTmpList));
    }
}
