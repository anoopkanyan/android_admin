package recode360.spreeadminapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import recode360.spreeadminapp.R;

public class PrimaryFragment extends Fragment {

    private static final String TAG = PrimaryFragment.class.getSimpleName();

    String result;

    public PrimaryFragment() {
        //nothing here yet
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.primary_layout, container, false);


        return rootView;
    }


}