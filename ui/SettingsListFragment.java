package sushchak.bohdan.curabitur.ui;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import sushchak.bohdan.curabitur.R;

public class SettingsListFragment extends PreferenceFragment {

    private final String TAG = "SettingsListFragment";


    public SettingsListFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_notification);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = (ListView) view.findViewById(android.R.id.list);

        if (listView != null)
            ViewCompat.setNestedScrollingEnabled(listView, true);
    }
}
