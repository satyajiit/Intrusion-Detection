package com.satyajit.intrusiondetection.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.satyajit.intrusiondetection.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

    Typeface Cav;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.about_fragment, container, false);

        Cav =  Typeface.createFromAsset(getActivity().getAssets(), "font/cav.ttf");

        setCustomTitlebar();

        return view;
    }


    void setCustomTitlebar(){
        //Set Title bar with Custom Typeface
        TextView tv = new TextView(getActivity());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText("About");
        tv.setTextSize(24);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTypeface(Cav);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(tv);

    }
}
