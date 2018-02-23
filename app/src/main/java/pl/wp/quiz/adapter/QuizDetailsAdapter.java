package pl.wp.quiz.adapter;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import pl.wp.quiz.QuizActivity;
import pl.wp.quiz.R;
import pl.wp.quiz.model.QuizModel;
import pl.wp.quiz.provider.database.ImageContract;
import pl.wp.quiz.provider.database.QuizContract;
import pl.wp.quiz.synchronizer.ImageLoaderTask;

public class QuizDetailsAdapter extends RecyclerView.Adapter<QuizDetailsAdapter.Holder> {

    public static final String TAG = QuizDetailsAdapter.class.getSimpleName();
    public static final int QUIZ_IMAGE_LOAD = 5;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class Holder extends RecyclerView.ViewHolder implements LoaderManager.LoaderCallbacks<Cursor> {
        TextView quizTitle;
        TextView quizInfo;
        ImageView quizImage;
        ProgressBar loadingProgress;
        View root;
        private ImageLoaderTask mTask;

        public Holder(View itemView) {
            super(itemView);
            root = itemView;
            quizTitle = itemView.findViewById(R.id.quiz_title);
            quizInfo = itemView.findViewById(R.id.quiz_info);
            quizImage = itemView.findViewById(R.id.quiz_image);
            loadingProgress = itemView.findViewById(R.id.image_loading);
        }

        public void startUploadImageTask(long quizId) {
            Activity activity = (Activity) root.getContext();
            Bundle args = new Bundle();
            args.putLong(QuizActivity.QUIZ_ID, quizId);
            if (activity.getLoaderManager().getLoader(QUIZ_IMAGE_LOAD) != null) {
                activity.getLoaderManager().restartLoader(QUIZ_IMAGE_LOAD, args, this);
            } else {
                activity.getLoaderManager().initLoader(QUIZ_IMAGE_LOAD, args, this);
            }
//            if (mTask != null && !mTask.isCancelled()) {
//                mTask.cancel(true);
//            }
//            mTask = new ImageLoaderTask(new ImageLoaderTask.OnLoadTaskListener() {
//                @Override
//                public void onFinished(Bitmap bitmap) {
//                    quizImage.setImageBitmap(bitmap);
//                    quizImage.setVisibility(View.VISIBLE);
//                    loadingProgress.setVisibility(View.INVISIBLE);
//                }
//            });
//            mTask.execute(imageUri);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case QUIZ_IMAGE_LOAD:
                    Uri uri = Uri.withAppendedPath(ImageContract.CONTENT_URI,
                            QuizContract.Quizzes.TABLE_NAME +
                            "/" + args.getLong(QuizActivity.QUIZ_ID));
                    return new CursorLoader(root.getContext(),
                            uri,
                            null,
                            null,
                            null,
                            null);
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                if (data.getCount() > 0 && data.moveToFirst()) {
                    String uri = data.getString(data.getColumnIndex(ImageContract.ImageEntry.IMAGE_URI));
                    setBitmapFromURI(uri);
                    quizImage.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
                data.close();
            }
        }

        private void setBitmapFromURI(String uri) {
            Bitmap prevBitmap = quizImage.getDrawable() != null && quizImage.getDrawable() instanceof BitmapDrawable ?
                    ((BitmapDrawable)quizImage.getDrawable()).getBitmap() : null;
            if (prevBitmap != null) {
                prevBitmap.recycle();
            }

            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(uri, bounds);
            Display display = ((Activity)quizImage.getContext()).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int sampleRate = size.x < bounds.outWidth ? bounds.outWidth / size.x : 1;
            Log.d(TAG, "setBitmapFromURI: width:" + bounds.outWidth);
            Log.d(TAG, "setBitmapFromURI: height:" + bounds.outHeight);
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = sampleRate;
            quizImage.setImageBitmap(BitmapFactory.decodeFile(uri, option));
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.d(TAG, "onLoaderReset: restart");
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
        if (model.getQuizImageURI() != null) {
            holder.loadingProgress.setVisibility(View.INVISIBLE);
            holder.setBitmapFromURI(model.getQuizImageURI());
        } else {
            holder.quizImage.setVisibility(View.GONE);
            holder.loadingProgress.setVisibility(View.VISIBLE);
            holder.startUploadImageTask(model.getId());
        }
        holder.quizInfo.setText(createInfoForQuiz(holder.quizImage.getContext(), model));
    }

    @Override
    public void onViewRecycled(Holder holder) {
        super.onViewRecycled(holder);
        Drawable lastDrawable = holder.quizImage.getDrawable();
        holder.quizImage.setImageDrawable(null);
        if (lastDrawable != null && lastDrawable instanceof BitmapDrawable) {
            ((BitmapDrawable) lastDrawable).getBitmap().recycle();
        }
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
