package pl.wp.quiz.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.wp.quiz.R;
import pl.wp.quiz.model.QuizModel;

public class QuizDetailsAdapter extends RecyclerView.Adapter<QuizDetailsAdapter.Holder> {

    public static final String TAG = QuizDetailsAdapter.class.getSimpleName();

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView quizTitle;
        TextView quizInfo;
        ImageView quizImage;
        View root;

        public Holder(View itemView) {
            super(itemView);
            root = itemView;
            quizTitle = itemView.findViewById(R.id.quiz_title);
            quizInfo = itemView.findViewById(R.id.quiz_info);
            quizImage = itemView.findViewById(R.id.quiz_image);
        }
    }

    private List<QuizModel> mItems;
    private OnItemClickListener mListener;

    public QuizDetailsAdapter(List<QuizModel> items) {
        mItems = items != null ? items : new ArrayList<QuizModel>();
    }

    public void setQuizzes(List<QuizModel> quizzes) {
        mItems = quizzes;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.quiz_list_item, parent, false);
        return new Holder(root);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        QuizModel model = mItems.get(position);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
        holder.quizTitle.setText(model.getQuizTitle());
        holder.quizInfo.setText(createInfoForQuiz(holder.quizImage.getContext(), model));
        byte[] blob = model.getQuizImage();
        holder.quizImage.setImageBitmap(BitmapFactory.decodeByteArray(blob, 0, blob.length));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void removeOnItemClickListener(OnItemClickListener listener) {
        mListener = null;
    }

    public QuizModel getItem(int position) {
        return mItems.get(position);
    }

    private String createInfoForQuiz(Context context, QuizModel model) {
        String pattern;
        int lastResult = model.getLastResultInfo();
        if (lastResult == -1) {
            return context.getString(R.string.quiz_empty_result);
        }
        if (model.isFinished()) {
            pattern = context.getString(R.string.quiz_last_result);


            return String.format(pattern, lastResult, model.getQuestionNumber(),
                    ((lastResult * 100) / model.getQuestionNumber()) + "%");
        } else {
            pattern = context.getString(R.string.quiz_resolved_percent);
            return String.format(pattern, ((model.getProgress() * 100) / model.getQuestionNumber()) + "%");
        }
    }
}
