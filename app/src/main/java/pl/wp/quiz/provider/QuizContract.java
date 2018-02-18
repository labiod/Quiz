package pl.wp.quiz.provider;

import android.net.Uri;

public class QuizContract {

    public static final Uri CONTENT_URI = Uri.parse("content://pl.wp.quiz.provider");

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
        public static final String QUESTION_IMAGE_URI = "question_image_uri";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                ID_QUESTION + " INTEGER PRIMARY KEY," +
                QUIZ_ID + " INTEGER," +
                QUESTION_TEXT + " TEXT," +
                QUESTION_TYPE + " TEXT," +
                QUESTION_ORDER + " INTEGER," +
                QUESTION_IMAGE_URI + " TEXT)";
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
        public static final String TABLE_NAME = "users_answers";
        public static final String ID = "";
        public static final String ANSWER_ID = "answer_id";
        public static final String ANSWER_DATE = "answer_date";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY," +
                ANSWER_ID + " INTEGER," +
                ANSWER_DATE + " INTEGER)";
        public static final String DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
