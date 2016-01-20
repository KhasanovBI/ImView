package jat.imview.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdView;

import jat.imview.R;
import jat.imview.adapter.GalleryAdapter;
import jat.imview.contentProvider.db.table.ImageTable;
import jat.imview.model.Image;
import jat.imview.service.SendServiceHelper;
import jat.imview.ui.view.GalleryViewPager;

public abstract class ImageListActivity extends DrawerActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, ViewPager.OnPageChangeListener {
    private static final String LOG_TAG = "MyImageListActivity";
    public static final String IS_NEED_TO_LOAD_EXTRA = "IS_NEED_TO_LOAD_EXTRA";

    protected boolean isFeatured;
    protected int currentImageId;
    protected Uri contentUri;
    private GalleryViewPager mViewPager;
    private GalleryAdapter mGalleryAdapter;

    private LinearLayout mCommentsButton;
    private ImageButton mShareButton;
    private AdView mAdView;

    protected TextView mCommentsCount;
    protected TextView mImageRating;

    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        mCommentsButton = (LinearLayout) findViewById(R.id.comments_button);
        mShareButton = (ImageButton) findViewById(R.id.share_button);
        mCommentsCount = (TextView) findViewById(R.id.comments_count);

        mCommentsButton.setOnClickListener(this);
        mShareButton.setOnClickListener(this);

        if (getIntent().getBooleanExtra(IS_NEED_TO_LOAD_EXTRA, true)) {
            SendServiceHelper.getInstance(this).requestImageList(isFeatured);
        }

        getSupportLoaderManager().restartLoader(0, null, this);
        mGalleryAdapter = new GalleryAdapter(getSupportFragmentManager());
        mViewPager = (GalleryViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mGalleryAdapter);
        mViewPager.addOnPageChangeListener(this);

        if (isNeedToShowAd) {
            mAdView = (AdView) findViewById(R.id.advertising_block);
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vote_up_button:
                SendServiceHelper.getInstance(this).requestImageVote(currentImageId, true);
                break;
            case R.id.comments_button:
                Intent intent = new Intent(this, CommentsActivity.class);
                intent.putExtra(CommentsActivity.IMAGE_ID_EXTRA, currentImageId);
                startActivity(intent);
                break;
            case R.id.share_button:
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(ImageTable.CONTENT_URI, currentImageId), null, null, null, null);
                    if (cursor.moveToFirst()) {
                        Image image = Image.getByCursor(cursor);
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(getResources().getString(R.string.app_name))
                                .setContentDescription(getResources().getString(R.string.slogan))
                                .setContentUrl(Uri.parse(image.getFullNetpath()))
                                .setImageUrl(Uri.parse(image.getFullNetpath()))
                                .build();
                        ShareDialog shareDialog = new ShareDialog(this);
                        shareDialog.show(linkContent);
                    }
                    cursor.close();
                }
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                Intent intent = new Intent(this, ImageNewActivity.class);
                startActivity(intent);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
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
                if (resultCode != 200) {
                    switch (resultCode) {
                        case 406:
                            Toast.makeText(
                                    getApplicationContext(),
                                    R.string.you_already_vote_for_this_image,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        default:
                            handleResponseErrors(resultCode);
                            break;
                    }
                }
            }
        };
        registerReceiver(requestReceiver, filter);
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, contentUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mGalleryAdapter != null && mViewPager != null) {
            mGalleryAdapter.changeCursor(data);
        }
        updateActivityFromPosition(currentPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mGalleryAdapter != null)
            mGalleryAdapter.changeCursor(null);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updateActivityFromPosition(position);
        currentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    protected void updateActivityFromPosition(int position) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor.moveToPosition(position)) {
            Image image = Image.getByCursor(cursor);
            currentImageId = image.getId();
            mCommentsCount.setText(String.valueOf(image.getCommentsCount()));
            mImageRating.setText(String.valueOf(image.getRating()));
        }
        cursor.close();
    }
}
