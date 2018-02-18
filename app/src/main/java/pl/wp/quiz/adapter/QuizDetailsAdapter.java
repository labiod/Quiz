package pl.wp.quiz.adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.wp.quiz.R;
import pl.wp.quiz.listener.ImageLoadListener;
import pl.wp.quiz.listener.LoadDataListener;
import pl.wp.quiz.model.QuizModel;
import pl.wp.quiz.provider.LoadImageHelper;

public class QuizDetailsAdapter extends BaseAdapter implements LoadDataListener<QuizModel> {

    public static final String TAG = QuizDetailsAdapter.class.getSimpleName();

    private class Holder implements ImageLoadListener {
        TextView quizTitle;
        TextView quizInfo;
        ImageView quizImage;

        Holder(View itemView) {
            quizTitle = itemView.findViewById(R.id.quiz_title);
            quizInfo = itemView.findViewById(R.id.quiz_info);
            quizImage = itemView.findViewById(R.id.quiz_image);
        }

        @Override
        public void onImageLoaded(String source) {
            Log.d(TAG, "onImageLoaded: source load");
//            quizImage.setImageDrawable(sourceToDrawable(source));
        }

        private Drawable sourceToDrawable(String source) {
            return null;
        }
    }

    private List<QuizModel> mItems;

    public QuizDetailsAdapter(List<QuizModel> items) {
        mItems = items != null ? items : new ArrayList<QuizModel>();
    }

    public void setQuizzes(List<QuizModel> quizzes) {
        mItems = quizzes;
    }
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public QuizModel getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.quiz_details, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        QuizModel model = getItem(position);
        holder.quizTitle.setText(model.getQuizTitle());
        holder.quizInfo.setText(createInfoForQuiz(model));
        LoadImageHelper.downloadImage(holder, model.getQuizImageURI());
        return convertView;
    }

    @Override
    public void onLoadData(List<QuizModel> dataList) {
        mItems = dataList;
        notifyDataSetChanged();
    }

    private String createInfoForQuiz(QuizModel model) {
        return model.getLastResultInfo();
    }
}
