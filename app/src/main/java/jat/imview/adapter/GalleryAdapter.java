package jat.imview.adapter;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import jat.imview.model.Image;
import jat.imview.ui.fragment.ImageFragment;

/**
 * Created by bulat on 07.12.15.
 */
public class GalleryAdapter extends FragmentStatePagerAdapter {
    private Cursor cursor;

    public GalleryAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        if (cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public Fragment getItem(int position) {
        Image image = null;
        if (cursor.moveToPosition(position)) {
            image = Image.getByCursor(cursor);
        }
        return ImageFragment.newInstance(image);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
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

    public Cursor getCursor() {
        return cursor;
    }
}
