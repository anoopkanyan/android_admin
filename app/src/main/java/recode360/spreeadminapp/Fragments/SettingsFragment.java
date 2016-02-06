package recode360.spreeadminapp.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.utils.AppCompatPreferenceActivity;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTheme(R.style.AppFormTheme);

        if (getArguments() != null) {
            String page = getArguments().getString("page");
            if (page != null)
                switch (page) {
                    case "page1":
                        addPreferencesFromResource(R.xml.settings_page1);
                        break;
                    case "page2":
                        addPreferencesFromResource(R.xml.settings_page2);
                        break;
                    case "page3":
                        addPreferencesFromResource(R.xml.settings_page3);
                        break;
                    case "page4":
                        addPreferencesFromResource(R.xml.settings_page4);
                        break;
                }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.activity_settings, container, false);
        if (layout != null) {
            AppCompatPreferenceActivity activity = (AppCompatPreferenceActivity) getActivity();
            Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
            activity.setSupportActionBar(toolbar);

            ActionBar bar = activity.getSupportActionBar();
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            bar.setTitle(getPreferenceScreen().getTitle());
        }
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() != null) {
            View frame = (View) getView().getParent();
            if (frame != null)
                frame.setPadding(0, 0, 0, 0);
        }
    }
}