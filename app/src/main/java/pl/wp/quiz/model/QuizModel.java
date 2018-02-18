package pl.wp.quiz.model;

public class QuizModel {
    private String mQuizTest;
    private String mQuizInfo;
    private String mQuizImageURI;
    private boolean mFinished;

    public QuizModel(String quizTest, String quizInfo, String quizImageURI, boolean finished) {
        mQuizTest = quizTest;
        mQuizInfo = quizInfo;
        mQuizImageURI = quizImageURI;
        mFinished = finished;
    }

    public boolean isFinished() {
        return mFinished;
    }

    public String getQuizImageURI() {
        return mQuizImageURI;
    }

    public String getQuizInfo() {
        return mQuizInfo;
    }

    public String getQuizTitle() {
        return mQuizTest;
    }
}
