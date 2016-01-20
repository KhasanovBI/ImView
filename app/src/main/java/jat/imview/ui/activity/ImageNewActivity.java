package jat.imview.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import jat.imview.R;
import jat.imview.service.SendServiceHelper;

public class ImageNewActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private static final String LOG_TAG = "MyImageNewActivity";
    private ImageView mContentImage;
    private EditText mCaption;
    private Uri mSelectedImageUri;
    private Bitmap mOldBitmap;
    private Bitmap mNewBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_new);

        mContentImage = (ImageView) findViewById(R.id.content_image);
        mCaption = (EditText) findViewById(R.id.caption);

        mContentImage.setOnClickListener(this);
        findViewById(R.id.select_image_button).setOnClickListener(this);
        findViewById(R.id.publish_button).setOnClickListener(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case REQUEST_CODE_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    mSelectedImageUri = imageReturnedIntent.getData();
                    try {
                        mOldBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mContentImage.setImageBitmap(mOldBitmap);
                }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_image_button:
            case R.id.content_image:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_image)), REQUEST_CODE_SELECT_IMAGE);
                break;
            case R.id.publish_button:
                if (createNewBitmap()) {
                    SendServiceHelper.getInstance(this).requestImageNew(mNewBitmap);
                }
                break;
        }
    }

    private boolean createNewBitmap() {
        String captionString = mCaption.getText().toString();
        if (mSelectedImageUri != null && !captionString.equals("")) {
            mNewBitmap = processingBitmap(mSelectedImageUri, captionString);
            return true;
        } else {
            Toast.makeText(getApplicationContext(), R.string.load_image_and_put_caption, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private Bitmap processingBitmap(Uri source, String captionString) {
        Bitmap oldBitmap;
        Bitmap newBitmap = null;
        try {
            oldBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(source));

            Bitmap.Config config = oldBitmap.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            int captionHeight = mCaption.getHeight();
            int oldBitmapWidth = oldBitmap.getWidth();
            float zoom = ((float) mContentImage.getWidth()) / oldBitmapWidth;
            int oldBitmapHeight = oldBitmap.getHeight();
            newBitmap = Bitmap.createBitmap(oldBitmapWidth, oldBitmapHeight + captionHeight, config);
            Canvas newCanvas = new Canvas(newBitmap);

            newCanvas.drawBitmap(oldBitmap, 0, 0, null);

            Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setColor(Color.WHITE);
            paintText.setTextSize(mCaption.getTextSize() / zoom);
            paintText.setStyle(Paint.Style.FILL);

            Rect rectText = new Rect();
            String[] captionStringLines = captionString.split("\n");
            int captionsLineHeight = captionHeight / captionStringLines.length;
            int offset = 5;
            for (int i = 0; i < captionStringLines.length; ++i) {
                String captionStringLine = captionStringLines[i];
                paintText.getTextBounds(captionStringLine, 0, captionStringLine.length(), rectText);
                newCanvas.drawText(
                        captionStringLine,
                        (oldBitmapWidth - rectText.width()) / 2,
                        captionsLineHeight * i + offset + oldBitmapHeight + rectText.height(),
                        paintText
                );
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return newBitmap;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_new_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (createNewBitmap()) {
                    addImageToGallery(mNewBitmap);
                }
                break;
            default:
                return false;
        }
        return true;
    }

    public void addImageToGallery(Bitmap bitmap) {
        File album = getAlbumStorageDir("ImView");
        File file = new File (album, "ImView" + String.valueOf(new Date().getTime())+ ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), R.string.image_saved, Toast.LENGTH_SHORT).show();
    }

    public File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
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
                        case 400:
                            Toast.makeText(
                                    getApplicationContext(),
                                    R.string.bad_file,
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
    }
}
