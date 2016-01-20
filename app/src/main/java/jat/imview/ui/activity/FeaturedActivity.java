package jat.imview.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import jat.imview.R;
import jat.imview.contentProvider.db.table.FeaturedTable;
import jat.imview.service.SendServiceHelper;

/**
 * Created by bulat on 16.01.16.
 */
public class FeaturedActivity extends ImageListActivity {
    private LinearLayout mVoteUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFeatured = true;
        contentUri = FeaturedTable.CONTENT_URI;
        super.onCreate(savedInstanceState);
        ViewStub viewStub = (ViewStub) findViewById(R.id.votes_placeholder);
        viewStub.setLayoutResource(R.layout.featured_votes);
        viewStub.inflate();
        mVoteUpButton = (LinearLayout) findViewById(R.id.vote_up_button);
        mVoteUpButton.setOnClickListener(this);
        mImageRating = (TextView) findViewById(R.id.image_rating);
    }

    @Override
    public NavigationDrawerItem getCurrentNavDrawerItem() {
        return NavigationDrawerItem.FEATURED;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vote_up_button:
                SendServiceHelper.getInstance(this).requestImageVote(currentImageId, true);
                break;
            default:
                super.onClick(v);
        }
    }
}
