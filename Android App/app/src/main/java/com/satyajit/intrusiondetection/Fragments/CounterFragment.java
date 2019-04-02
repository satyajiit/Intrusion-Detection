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
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.satyajit.intrusiondetection.R;
import com.satyajit.thespotsdialog.SpotsDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class CounterFragment extends Fragment {

    static Handler bluetoothIn;
    public static StringBuilder sb = new StringBuilder();
    private Typeface Cav;
    private LayoutInflater inflater2;
    private View layout;
    private TextView Counter;
    private AlertDialog dialog;
    private Toast toast;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String address;
    private TextView text;
    private Button reset;
    int people = 0;


    private ConnectedThread mConnectedThread;

    public static CounterFragment newInstance() {
        return new CounterFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.counter_fragment, container, false);


        initUI(view);


            setUIText();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new ConnectBT().execute();
            }
        }, 1000);




        return view;
    }
    public interface OnFragmentInteractionListener {
    }

    void setCustomTitlebar(){
        //Set Title bar with Custom Typeface
        TextView tv = new TextView(getActivity());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText("Counter");
        tv.setTextSize(24);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTypeface(Cav);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(tv);

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
                    .setTypeface(Cav)
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
                mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();

                isBtConnected = true;

            }

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

    public void fgh(){

        //Start the Register Activity
        PopFrag alertDialogFragment = new PopFrag();

        FragmentManager manager = getActivity().getSupportFragmentManager();

        alertDialogFragment.show(manager,"fragment_pop");



    }

    // fast way to call Toast
    void msg(String message) {

        text.setText(message);


        toast.show();
    }


    void initUI(View view){

        Cav =  Typeface.createFromAsset(getActivity().getAssets(), "font/cav.ttf");

        address = "00:21:13:04:BE:A0"; //Bluetooth Device MAC Address

        setCustomTitlebar();

        reset = view.findViewById(R.id.reset);

        //Toast Design
        inflater2 = getLayoutInflater();

        Counter = view.findViewById(R.id.counter);

        layout = inflater2.inflate(R.layout.toast, (ViewGroup) view.findViewById(R.id.custom_toast_container));

        text = layout.findViewById(R.id.TMsg);


        toast = new Toast(getActivity());
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendCommand("r");
                Counter.setText("0");
            }
        });


    }



    public void sendCommand(String cmd){



        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(cmd.getBytes()); //on the device

                msg("Command Sent!!");

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

    void setUIText(){


        bluetoothIn = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:                                  //if message is what we want




                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);

                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("!");

                        if (endOfLineIndex > 0) {
                            String sbprint = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());
                            Counter.setText(sbprint);
                            Log.i("asdas",sbprint);
                        }





                }
            }
        };



    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;


        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;

            try {
                //Create I/O streams for connection

                tmpIn = socket.getInputStream();

            } catch (IOException e) { }

            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);      //read bytes from input buffer
                    bluetoothIn.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                    bluetoothIn.handleMessage(new Message());
                } catch (IOException e) {
                    break;
                }
            }
        }

    }
}







