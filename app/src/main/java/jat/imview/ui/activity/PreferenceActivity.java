package jat.imview.ui.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferenceActivity extends DrawerActivity {
    @Override
    public NavigationDrawerItem getCurrentNavDrawerItem() {
        return NavigationDrawerItem.SETTINGS;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(jat.imview.R.layout.activity_preference);
        getFragmentManager().beginTransaction().replace(jat.imview.R.id.pref_fragment_container, new PrefFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static class PrefFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(jat.imview.R.xml.pref_general);
        }
    }
}
