package jat.imview.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import jat.imview.R;
import jat.imview.model.Comment;

/**
 * Created by bulat on 16.12.15.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private Cursor cursor;
    private OnItemClickListener onItemClickListener;

    public CommentsAdapter() {
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            Comment comment = Comment.getByCursor(cursor);
            holder.mCommentText.setText(comment.getMessage());
            Locale locale = new Locale("ru");
            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd MMMM", locale);
            holder.mPublishDate.setText(dateFormat.format(comment.getPublishDate()));
            holder.mRating.setText(String.valueOf(comment.getRating()));
            holder.mUsername.setText(comment.getUserName());
        }
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void changeCursor(Cursor newCursor) {
        if (cursor == newCursor)
            return;
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        this.cursor = newCursor;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, int itemViewId);
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mUsername;
        private final TextView mPublishDate;
        private final TextView mCommentText;
        private final TextView mRating;
        private final ImageButton mVoteUpButton;
        private final ImageButton mVoteDownButton;
        private final ImageView mUserAvatar;

        public CommentViewHolder(View itemView) {
            super(itemView);
            mUsername = (TextView) itemView.findViewById(R.id.username);
            mPublishDate = (TextView) itemView.findViewById(R.id.publish_date);
            mCommentText = (TextView) itemView.findViewById(R.id.comment_text);
            mUserAvatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            mRating = (TextView) itemView.findViewById(R.id.rating);
            mVoteUpButton = (ImageButton) itemView.findViewById(R.id.vote_up_button);
            mVoteDownButton = (ImageButton) itemView.findViewById(R.id.vote_down_button);

            itemView.setOnClickListener(this);
            mUsername.setOnClickListener(this);
            mUserAvatar.setOnClickListener(this);
            mVoteUpButton.setOnClickListener(this);
            mVoteDownButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = getOnItemClickListener();
            listener.onItemClick(getAdapterPosition(), v.getId());
        }
    }
}
