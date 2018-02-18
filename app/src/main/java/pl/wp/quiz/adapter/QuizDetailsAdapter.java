package pl.wp.quiz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.wp.quiz.R;
import pl.wp.quiz.model.QuizModel;

public class QuizDetailsAdapter extends BaseAdapter {
    private class Holder {
        TextView quizTitle;
        TextView quizInfo;
        ImageView quizImage;

        Holder(View itemView) {
            quizTitle = itemView.findViewById(R.id.quiz_title);
            quizInfo = itemView.findViewById(R.id.quiz_info);
            quizImage = itemView.findViewById(R.id.quiz_image);
        }
    }

    private List<QuizModel> mItems;

    public QuizDetailsAdapter(List<QuizModel> items) {
        mItems = items;
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
        return convertView;
    }

    private String createInfoForQuiz(QuizModel model) {
        return model.getQuizInfo();
    }
}
