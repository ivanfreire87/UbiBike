package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MessagesActivity extends Activity implements ConnectionService.Callbacks {

    ConnectionService mService;
    boolean mBound = false;

    protected TextView mTextInput;
    protected TextView mTextOutput;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the UI
        setContentView(R.layout.activity_messages);
        guiSetButtonListeners();
        guiUpdateInitState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    // Methods to comunicate with service
    public void eraseInput(){
        mTextInput.setText("");
    }

    public void displayDevicesInRange(StringBuilder sb) {
        new AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Range")
                .setMessage(sb.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void GuiUpdateConnectedState(){
        findViewById(R.id.idDisconnectButton).setEnabled(true);
        findViewById(R.id.connectButton).setEnabled(false);
        findViewById(R.id.sendButton).setEnabled(true);
        mTextInput.setHint("");
        mTextInput.setText("");
        mTextOutput.setText("");
    }

    public void setOutput(String s){
        mTextOutput.setText(s);
    }



    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mService = binder.getService();
            mService.registerClient(MessagesActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

	//Listeners associated to buttons

    private OnClickListener listenerInRangeButton = new OnClickListener() {
        public void onClick(View v){
            if (mBound) {
                mService.inRange();
            } else {
                Toast.makeText(v.getContext(), "Service not bound", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private OnClickListener listenerConnectButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
            findViewById(R.id.connectButton).setEnabled(false);
            if (mBound) {
                mService.connect(mTextInput.getText().toString());
            } else {
                Toast.makeText(v.getContext(), "Service not bound", Toast.LENGTH_SHORT).show();
            }

        }
    };


    private void guiSetButtonListeners() {

        findViewById(R.id.connectButton).setOnClickListener(listenerConnectButton);
        findViewById(R.id.idDisconnectButton).setOnClickListener(listenerDisconnectButton);
        findViewById(R.id.sendButton).setOnClickListener(listenerSendButton);
        findViewById(R.id.idInRangeButton).setOnClickListener(listenerInRangeButton);

    }

    protected void guiUpdateInitState() {

        mTextInput = (TextView) findViewById(R.id.editText1);
        mTextInput.setHint("type remote virtual IP (192.168.0.0/16)");
        mTextInput.setEnabled(false);

        mTextOutput = (TextView) findViewById(R.id.editText2);
        mTextOutput.setEnabled(false);
        mTextOutput.setText("");

        findViewById(R.id.connectButton).setEnabled(false);
        findViewById(R.id.idDisconnectButton).setEnabled(false);
        findViewById(R.id.sendButton).setEnabled(false);
        findViewById(R.id.idInRangeButton).setEnabled(true);

    }

    public void guiUpdateDisconnectedState() {

        mTextInput.setEnabled(true);
        mTextInput.setHint("type remote virtual IP (192.168.0.0/16)");
        mTextOutput.setEnabled(true);
        mTextOutput.setText("");

        findViewById(R.id.sendButton).setEnabled(false);
        findViewById(R.id.connectButton).setEnabled(true);
        findViewById(R.id.idDisconnectButton).setEnabled(false);
        findViewById(R.id.idInRangeButton).setEnabled(true);

    }

    private OnClickListener listenerSendButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
            findViewById(R.id.sendButton).setEnabled(false);

            if (mBound) {
                mService.send(mTextInput.getText().toString());
            } else {
                Toast.makeText(v.getContext(), "Service not bound", Toast.LENGTH_SHORT).show();
            }


        }
    };

    private OnClickListener listenerDisconnectButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
            findViewById(R.id.idDisconnectButton).setEnabled(false);

            if (mBound) {
                mService.disconnect();
            } else {
                Toast.makeText(v.getContext(), "Service not bound", Toast.LENGTH_SHORT).show();
            }

            guiUpdateDisconnectedState();
        }
    };

    public class ConnectCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            //////////////////////mTextOutput.append(values[0] + "\n");
        }
    }

}
