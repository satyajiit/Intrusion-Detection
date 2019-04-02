package com.satyajit.intrusiondetection.Fragments;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.satyajit.intrusiondetection.R;
import com.satyajit.thespotsdialog.SpotsDialog;
import com.suke.widget.SwitchButton;


import java.io.IOException;
import java.util.UUID;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class SettingsFragment extends Fragment {



    Typeface Cav;
    LayoutInflater inflater2;
    View layout;
    AlertDialog dialog;
    Toast toast;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address;
    TextView text;

    SwitchButton Alarm,Ldr1,Ldr2;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_fragment, container, false);



        initUI(view);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new ConnectBT().execute();
            }
        }, 1000);

        setListeners();



        return view;
    }



    void setCustomTitlebar(){
        //Set Title bar with Custom Typeface
        TextView tv = new TextView(getActivity());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText("Settings");
        tv.setTextSize(24);
       tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTypeface(Cav);
         ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(tv);

    }

    void initUI(View view){

        Cav =  Typeface.createFromAsset(getActivity().getAssets(), "font/cav.ttf");

        address = "00:21:13:04:BE:A0"; //Bluetooth Device MAC Address

        setCustomTitlebar();



        //Toast Design
        inflater2 = getLayoutInflater();



        layout = inflater2.inflate(R.layout.toast, (ViewGroup) view.findViewById(R.id.custom_toast_container));

        text = layout.findViewById(R.id.TMsg);


        toast = new Toast(getActivity());
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        Alarm = view.findViewById(R.id.alarm);
        Ldr1 = view.findViewById(R.id.ldr1);
        Ldr2 = view.findViewById(R.id.ldr2);



    }

    // fast way to call Toast
    void msg(String message) {

        text.setText(message);


        toast.show();
    }





    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {


            dialog = new SpotsDialog.Builder()
                    .setContext(getActivity())
                    .setTheme(R.style.upd)
                    .build();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();



        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Error Code 03");
                fgh();

            }
            else
            {
                msg("Connected...!!");
                isBtConnected = true;
            }

        }
    }

    public void fgh(){

        //Start the Register Activity
        PopFrag alertDialogFragment = new PopFrag();

        FragmentManager manager = getActivity().getSupportFragmentManager();

        alertDialogFragment.show(manager,"fragment_pop");



    }


    public void sendCommand(String cmd){



        if (btSocket!=null)
        {
            try
            {

                btSocket.getOutputStream().write(cmd.getBytes()); //on the device

                msg("Command Sent!");

            }
            catch (IOException e)
            {
                msg("Error Code 123");

            }
        }
        else {
            msg("Module Not Connected!");

        }
    }


    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            if (btSocket!=null)
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    void setListeners(){



        Alarm.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {



                if (isChecked)
                        sendCommand("2");
                else
                    sendCommand("3");


            }
        });

        Ldr1.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {



                if (isChecked)
                    sendCommand("4");
                else
                    sendCommand("5");


            }
        });

        Ldr2.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {



                if (isChecked)
                    sendCommand("6");
                else
                    sendCommand("7");


            }

        });


    }


}




