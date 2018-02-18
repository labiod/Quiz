package pl.wp.quiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Arrays;

import pl.wp.quiz.adapter.QuizDetailsAdapter;
import pl.wp.quiz.model.QuizModel;

public class QuizActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_main);
        ListView quizzesList = findViewById(R.id.quizzes_list);
        quizzesList.setAdapter(createQuizDataAdapter());
    }

    private ListAdapter createQuizDataAdapter() {
        return new QuizDetailsAdapter(QuizModel.generateModels(10));
    }
}
