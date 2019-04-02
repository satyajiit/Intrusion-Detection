package com.satyajit.intrusiondetection.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.satyajit.intrusiondetection.R;

import java.net.CookieHandler;
import java.net.CookieManager;

import androidx.fragment.app.DialogFragment;


public class PopFrag extends DialogFragment {

    Button ok;


    public PopFrag() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        setCancelable(false); //Disallow cancellation/dismiss of popUp

        View view = inflater.inflate(R.layout.error_pop, container);  //Inflate the Layout

        initUI(view);


        setListeners();






        return view;
    }

    public void initUI(View v){


        ok = v.findViewById(R.id.ok);

    }


    void setListeners(){



        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });





    }




}




