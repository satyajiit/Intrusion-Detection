package com.satyajit.intrusiondetection;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.satyajit.intrusiondetection.Fragments.AboutFragment;
import com.satyajit.intrusiondetection.Fragments.CounterFragment;
import com.satyajit.intrusiondetection.Fragments.SettingsFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity  implements CounterFragment.OnFragmentInteractionListener{

    Fragment fragment = null;

    //Change fragments by click nav buttons
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_about:
                    fragment = AboutFragment.newInstance();
                    break;
                case R.id.navigation_settings:
                    fragment = SettingsFragment.newInstance();

                    break;
                case R.id.navigation_counter:
                    fragment = CounterFragment.newInstance();
                    break;


            }



            if (fragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);

        navigation.setSelectedItemId(R.id.navigation_counter);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //Set the main fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, CounterFragment.newInstance()); //Set Dashboard fragment as default
        fragmentTransaction.commit();

    }

}
