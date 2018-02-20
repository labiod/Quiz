package pl.wp.quiz.provider.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pl.wp.quiz.provider.QuizContract;

/**
 * Created by labio on 18.02.2018.
 */

public class QuizeesDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "QuizDB.db";


    public QuizeesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QuizContract.Config.CREATE);
        db.execSQL(QuizContract.Quizzes.CREATE);
        db.execSQL(QuizContract.QuizQuestions.CREATE);
        db.execSQL(QuizContract.QuestionAnswers.CREATE);
        db.execSQL(QuizContract.UsersAnswers.CREATE);
        db.execSQL(QuizContract.QuizRates.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(QuizContract.Config.DELETE);
        db.execSQL(QuizContract.Quizzes.DELETE);
        db.execSQL(QuizContract.QuizQuestions.DELETE);
        db.execSQL(QuizContract.QuestionAnswers.DELETE);
        db.execSQL(QuizContract.UsersAnswers.DELETE);
        db.execSQL(QuizContract.QuizRates.DELETE);
        onCreate(db);

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
