package jat.imview.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import jat.imview.R;
import jat.imview.adapter.CommentsAdapter;
import jat.imview.contentProvider.db.table.CommentTable;
import jat.imview.model.Comment;
import jat.imview.service.RequestType;
import jat.imview.service.SendServiceHelper;

public class CommentsActivity extends DrawerActivity implements CommentsAdapter.OnItemClickListener, OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = "MyCommentsActivity";
    public static final String IMAGE_ID_EXTRA = "IMAGE_ID_EXTRA";
    private static final int MIN_INPUT_TEXT_LENGTH = 3;
    private RecyclerView mCommentsRecyclerView;
    private CommentsAdapter mCommentsAdapter;
    private EditText mMessageTextInput;
    private ImageView mSendButton;
    private int imageId;
    private AdView mAdView;
    private BroadcastReceiver requestReceiver;

    @Override
    public NavigationDrawerItem getCurrentNavDrawerItem() {
        return NavigationDrawerItem.FEATURED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        Intent intent = getIntent();
        imageId = intent.getIntExtra(IMAGE_ID_EXTRA, -1);
        Log.d(LOG_TAG, "Image ID: " + String.valueOf(imageId));

        getSupportLoaderManager().restartLoader(0, null, this);

        setContentView(R.layout.activity_comments);
        mCommentsRecyclerView = (RecyclerView) findViewById(R.id.comments_recycler_view);
        mCommentsAdapter = new CommentsAdapter();
        mCommentsAdapter.setOnItemClickListener(this);

        mCommentsRecyclerView.setAdapter(mCommentsAdapter);
        mCommentsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication().getApplicationContext()));
        mSendButton = (ImageView) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(this);

        mMessageTextInput = (EditText) findViewById(R.id.message_text_input);
        mMessageTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSendButton.setImageDrawable(getResources().getDrawable(
                        s.length() >= MIN_INPUT_TEXT_LENGTH ? R.drawable.ic_arrow_forward_white_48dp : R.drawable.ic_arrow_forward_black_48dp
                ));
            }
        });

        if (isNeedToShowAd) {
            mAdView = (AdView) findViewById(R.id.advertising_block);
            mAdView.loadAd(adRequest);
        }

        requestsIdMap.put(
                SendServiceHelper.getInstance(this).requestCommentList(imageId),
                RequestType.COMMENT_LIST
        );
    }

    @Override
    public void onItemClick(int position, int itemViewId) {
        boolean isUpVote;
        switch (itemViewId) {
            case R.id.vote_up_button:
                isUpVote = true;
                break;
            case R.id.vote_down_button:
                isUpVote = false;
                break;
            // на случай дальнейших улучшений
            case R.id.user_avatar:
            case R.id.username:
            default:
                return;
        }
        Cursor cursor = getContentResolver().query(CommentTable.CONTENT_URI, null, CommentTable.IMAGE_ID + " = ?", new String[]{String.valueOf(imageId)}, null);
        if (cursor.moveToPosition(position)) {
            Comment comment = Comment.getByCursor(cursor);
            int commentId = comment.getId();
            requestsIdMap.put(
                    SendServiceHelper.getInstance(this).requestCommentVote(commentId, isUpVote),
                    RequestType.COMMENT_VOTE
            );
        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                String commentText = mMessageTextInput.getText().toString();
                if (commentText.length() >= MIN_INPUT_TEXT_LENGTH) {
                    requestsIdMap.put(
                            SendServiceHelper.getInstance(this).requestCommentNew(imageId, commentText),
                            RequestType.COMMENT_NEW
                    );
                }
                break;
            case R.id.profile:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(SendServiceHelper.ACTION_REQUEST_RESULT);
        requestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int resultRequestId = intent.getIntExtra(SendServiceHelper.EXTRA_REQUEST_ID, 0);
                Log.d(LOG_TAG, "Received intent " + intent.getAction() + ", request ID " + resultRequestId);
                int resultCode = intent.getIntExtra(SendServiceHelper.EXTRA_RESULT_CODE, 0);
                Log.d(LOG_TAG, String.valueOf(resultCode));
                RequestType requestType = requestsIdMap.remove(resultRequestId);
                switch (requestType) {
                    case COMMENT_VOTE:
                        if (resultCode != 200) {
                            switch (resultCode) {
                                case 406:
                                    Toast.makeText(
                                            getApplicationContext(),
                                            R.string.you_already_vote_for_this_comment,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    break;
                                default:
                                    handleResponseErrors(resultCode);
                                    break;
                            }
                        }
                        break;
                    case COMMENT_NEW:
                        if (resultCode != 200) {
                            handleResponseErrors(resultCode);
                        } else {
                            mMessageTextInput.setText("");
                            mCommentsRecyclerView.smoothScrollToPosition(0);
                        }
                        break;
                    case COMMENT_LIST:
                        if (resultCode != 200) {
                            handleResponseErrors(resultCode);
                        }
                        break;
                }
            }
        };
        if (mAdView != null) {
            mAdView.resume();
        }
        registerReceiver(requestReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
        if (requestReceiver != null) {
            try {
                this.unregisterReceiver(requestReceiver);
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage(), e);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CommentTable.CONTENT_URI, null, CommentTable.IMAGE_ID + " = ?", new String[]{String.valueOf(imageId)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mCommentsAdapter != null) {
            mCommentsAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mCommentsAdapter != null)
            mCommentsAdapter.changeCursor(null);
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
