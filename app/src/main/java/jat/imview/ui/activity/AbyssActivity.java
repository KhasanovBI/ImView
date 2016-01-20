package jat.imview.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jat.imview.R;
import jat.imview.contentProvider.db.table.AbyssTable;
import jat.imview.service.SendServiceHelper;

/**
 * Created by bulat on 16.01.16.
 */
public class AbyssActivity extends ImageListActivity {
    private ImageView mVoteUpButton;
    private ImageView mVoteDownButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFeatured = false;
        contentUri = AbyssTable.CONTENT_URI;
        super.onCreate(savedInstanceState);
        ViewStub viewStub = (ViewStub) findViewById(R.id.votes_placeholder);
        viewStub.setLayoutResource(R.layout.abyss_votes);
        viewStub.inflate();
        mVoteUpButton = (ImageView) findViewById(R.id.vote_up_button);
        mVoteUpButton.setOnClickListener(this);
        mVoteDownButton = (ImageView) findViewById(R.id.vote_down_button);
        mVoteDownButton.setOnClickListener(this);
        mImageRating = (TextView) findViewById(R.id.image_rating);
    }

    @Override
    public NavigationDrawerItem getCurrentNavDrawerItem() {
        return NavigationDrawerItem.ABYSS;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vote_up_button:
                SendServiceHelper.getInstance(this).requestImageVote(currentImageId, true);
                break;
            case R.id.vote_down_button:
                SendServiceHelper.getInstance(this).requestImageVote(currentImageId, false);
                break;
            default:
                super.onClick(v);
        }
    }
}
