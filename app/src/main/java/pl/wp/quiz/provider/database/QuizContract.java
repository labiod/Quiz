package pl.wp.quiz.provider.database;

import android.net.Uri;

public class QuizContract {

    public static final String AUTHORITY = "pl.wp.quiz.provider.quizzes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String QUESTION_WITH_ANSWER = "question_with_answer";

    public static class Quizzes {
        public static final String TABLE_NAME = "quizzes";
        public static final String ID_QUIZ = "id_quiz";
        public static final String QUIZ_TITLE = "quiz_title";
        public static final String QUESTION_NUMBER = "question_number";
        public static final String QUIZ_TYPE = "quiz_type";
        public static final String QUIZ_CATEGORY = "quiz_category";
        public static final String QUIZ_CONTENT = "quiz_content";
        public static final String QUIZ_PHOTO_URI = "quiz_photo_uri";
        public static final String QUIZ_PROGRESS = "quiz_progress";
        public static final String LAST_RESULT = "last_result";
        public static final String QUIZ_CREATED_AT = "quiz_created_at";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                ID_QUIZ + " INTEGER PRIMARY KEY," +
                QUIZ_TITLE + " TEXT," +
                QUESTION_NUMBER + " INTEGER," +
                QUIZ_TYPE + " TEXT," +
                QUIZ_CATEGORY + " TEXT," +
                QUIZ_CONTENT + " TEXT," +
                QUIZ_PHOTO_URI + " TEXT," +
                QUIZ_PROGRESS + " INTEGER," +
                LAST_RESULT + " INTEGER DEFAULT -1," +
                QUIZ_CREATED_AT + " INTEGER)";
        public static final String DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class QuizQuestions {
        public static final String TABLE_NAME = "quiz_questions";
        public static final String ID_QUESTION = "id_question";
        public static final String QUIZ_ID = "quiz_id";
        public static final String QUESTION_TEXT = "question_text";
        public static final String QUESTION_TYPE = "question_type";
        public static final String QUESTION_ORDER = "question_order";
        public static final String QUESTION_PHOTO_URI = "question_photo_uri";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                ID_QUESTION + " INTEGER PRIMARY KEY," +
                QUIZ_ID + " INTEGER," +
                QUESTION_TEXT + " TEXT," +
                QUESTION_TYPE + " TEXT," +
                QUESTION_ORDER + " INTEGER," +
                QUESTION_PHOTO_URI + " TEXT)";
        public static final String DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class QuestionAnswers {
        public static final String TABLE_NAME = "question_answers";
        public static final String ID_ANSWER = "id_answer";
        public static final String QUESTION_ID = "question_id";
        public static final String ANSWER_TEXT = "answer_text";
        public static final String ANSWER_IMAGE_URI = "answer_image_uri";
        public static final String ANSWER_ORDER = "answer_order";
        public static final String IS_CORRECT = "is_correct";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                ID_ANSWER + " INTEGER PRIMARY KEY," +
                QUESTION_ID + " INTEGER," +
                ANSWER_TEXT + " TEXT," +
                ANSWER_IMAGE_URI + " TEXT," +
                ANSWER_ORDER + " INTEGER," +
                IS_CORRECT + " INTEGER)";
        public static final String DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class UsersAnswers {
        public static final String ANSWER_SEPARATOR = ";";
        public static final String TABLE_NAME = "users_answers";
        public static final String ID = "_id";
        public static final String QUIZ_ID = "quiz_id";
        public static final String RATE_ID = "rate_id";
        public static final String ANSWERS_LIST = "answers_list";
        public static final String ANSWER_DATE = "answer_date";
        public static final String ANSWER_PROGRESS = "answer_progress";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY," +
                QUIZ_ID + " INTEGER," +
                ANSWERS_LIST + " INTEGER," +
                ANSWER_PROGRESS + " INTEGER," +
                RATE_ID + " INTEGER," +
                ANSWER_DATE + " INTEGER)";
        public static final String DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class QuizRates {
        public static final String TABLE_NAME = "quiz_rates";
        public static final String ID_RATE = "id_rate";
        public static final String QUIZ_ID = "quiz_id";
        public static final String RATE_FROM = "rate_from";
        public static final String RATE_TO = "rate_to";
        public static final String RATE_CONTENT = "rate_content";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                ID_RATE + " INTEGER PRIMARY KEY," +
                QUIZ_ID + " INTEGER ," +
                RATE_FROM + " INTEGER ," +
                RATE_TO + " INTEGER ," +
                RATE_CONTENT + " TEXT)";
        public static final String DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class Config {
        public static final String TABLE_NAME = "sync_config";
        public static final String ID_CONFIG = "id_config";
        public static final String SYNC_DATE = "sync_date";
        public static final String SYNC_STATUS = "sync_status";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                ID_CONFIG + " INTEGER PRIMARY KEY," +
                SYNC_DATE + " INTEGER ," +
                SYNC_STATUS + " INTEGER)";
        public static final String DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static String getIdForTable(String tableName) {
        switch (tableName) {
            case Quizzes.TABLE_NAME:
                return Quizzes.ID_QUIZ;
            case QuizQuestions.TABLE_NAME:
                return QuizQuestions.ID_QUESTION;
            case QuestionAnswers.TABLE_NAME:
                return QuestionAnswers.ID_ANSWER;
            case QuizRates.ID_RATE:
                return QuizRates.ID_RATE;
            default:
                return "_id";
        }
    }
}
