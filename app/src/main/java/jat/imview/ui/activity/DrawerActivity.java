package jat.imview.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;

import jat.imview.R;


public abstract class DrawerActivity extends BaseActivity {
    private static final int NAVDRAWER_LAUNCH_DELAY = 258;

    private Drawer mDrawer;
    private Handler mHandler;

    public enum NavigationDrawerItem {
        FEATURED(R.drawable.ic_all_inclusive_black_18dp, R.string.featured_activity_string, FeaturedActivity.class, true),
        ABYSS(R.drawable.ic_whatshot_black_18dp, R.string.abyss_activity_string, AbyssActivity.class, true),
        AUTHENTICATION(R.drawable.ic_person_black_18dp, R.string.authentication_activity_string, AuthenticationActivity.class, true),
        SETTINGS(R.drawable.ic_settings_black_18dp, R.string.settings_activity_string, PreferenceActivity.class, true);

        private int name;
        private int icon;
        private Class<? extends Activity> activity;
        private boolean isDivider = false;
        private boolean isDrawerActivity = true;

        NavigationDrawerItem(int icon, int name, Class<? extends Activity> activity, boolean isDrawerActivity) {
            this.icon = icon;
            this.name = name;
            this.activity = activity;
            this.isDrawerActivity = isDrawerActivity;
        }

        NavigationDrawerItem(boolean isDivider) {
            this.isDivider = isDivider;
        }

        public int getName() {
            return name;
        }

        public int getIcon() {
            return icon;
        }

        public Class<? extends Activity> getActivity() {
            return activity;
        }

        public boolean isDivider() {
            return isDivider;
        }
    }

    protected Toolbar toolbar;

    /**
     * @return current navigation drawer item,
     * which would be displayed as selected
     */
    public abstract NavigationDrawerItem getCurrentNavDrawerItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
    }

    protected int getHomeAsUpIndicator() {
        return 0;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(this.toolbar);
        }

        // If current navigation drawer presents,
        // init navigation drawer
        if (getCurrentNavDrawerItem() != null) {
            createDrawer();
            mDrawer.setSelection(
                    getCurrentNavDrawerItem().ordinal(),
                    false
            );
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                if (getHomeAsUpIndicator() != 0) {
                    getSupportActionBar().setHomeAsUpIndicator(getHomeAsUpIndicator());
                }
            }
        }
    }

    /**
     * Build and set navigation drawer
     */
    private void createDrawer() {
        ArrayList<IDrawerItem> items = new ArrayList<>();
        for (NavigationDrawerItem item : NavigationDrawerItem.values()) {
            if (item.isDivider()) {
                items.add(new DividerDrawerItem());
            } else {
                items.add(
                        new PrimaryDrawerItem()
                                .withName(getString(item.getName()))
                                .withIcon(item.getIcon())
                                .withIdentifier(item.ordinal())
                                .withIconTintingEnabled(true)
                );
            }
        }

        this.mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withDrawerItems(items)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        onNavDrawerItemClicked(NavigationDrawerItem.values()[drawerItem.getIdentifier()]);
                        return true;
                    }
                })
                .withCloseOnClick(false)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        closeKeyboard();
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }
                })
                .withHeaderDivider(false)
                .build();
    }

    /**
     * Set badge to navigation drawer item
     *
     * @param item navigation drawer item
     * @param text badge text
     */
    protected void setBadge(NavigationDrawerItem item, String text) {
        try {
            PrimaryDrawerItem drawerItem = (PrimaryDrawerItem) mDrawer.getDrawerItem(item.ordinal());
            drawerItem.withBadge(text);
            mDrawer.updateItem(drawerItem);
        } catch (ClassCastException e) {
            e.printStackTrace();
            Log.e("DrawerBaseActivity", "Navigation drawer item must be primary drawer item");
        }
    }

    /**
     * Called when user taps on navigation drawer item
     *
     * @param item current navigation drawer item
     */
    private void onNavDrawerItemClicked(final NavigationDrawerItem item) {
        mDrawer.closeDrawer();

        if (item == getCurrentNavDrawerItem()) {
            return;
        }

        // Launch the target Activity after a short delay, to allow the close animation to play
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateTo(item);
            }
        }, NAVDRAWER_LAUNCH_DELAY);
    }

    /**
     * Opens appropriate activity
     *
     * @param item navigation drawer item
     */
    private void navigateTo(NavigationDrawerItem item) {
        Intent intent = new Intent();
        intent.setClass(this, item.getActivity());

        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Disable activity animation
        overridePendingTransition(0, 0);
        if (item.isDrawerActivity) {
            finish();
        }
    }

    /**
     * Closes keyboard.
     * Used to close keyboard, when drawer opens
     */
    private void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();

        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }
}
