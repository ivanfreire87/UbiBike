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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity implements ConnectionService.Callbacks {

    private ConnectionService mService;
    private boolean mBound = false;
    private TextView mTextInput;
    private TextView mMessagesOutput;
    private TextView mValidationOutput;
    private TextView mTextOutput;
    private String connectionAddress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            mService.disconnect();
            unbindService(mConnection);
            mBound = false;
        }
    }

    // Methods to communicate with service
    public void eraseInput(){
        mTextInput.setText("");
    }
    public void sendTrack(List<LatLng> list){}

    public void appendValuesOutput(String text){
        mMessagesOutput.append(text + "\n");
    }
    public void setValidationOutput(String text){
        mValidationOutput.setText(text);
    }

    public void displayDevicesInRange(final CharSequence[] devices) {
        new AlertDialog.Builder(this)
                .setTitle("Connect to")
                .setItems(devices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String option = devices[which].toString();
                        connectionAddress = option.split("\\(")[1].split("\\)")[0];

                        if (mBound) {
                            mService.connect(connectionAddress);

                            setValidationOutput("Connected to device with ip " + connectionAddress);
                        } else {
                            Toast.makeText(MessagesActivity.this, "Service not bound", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void GuiUpdateConnectedState(){

        findViewById(R.id.sendMessageButton).setEnabled(true);
        findViewById(R.id.idInRangeButton).setEnabled(false);
        mTextInput.setHint("");
        mTextInput.setText("");

    }

    public void guiSetButtonListeners() {
        findViewById(R.id.sendMessageButton).setOnClickListener(listenerSendButton);
        findViewById(R.id.idInRangeButton).setOnClickListener(listenerInRangeButton);

    }

    public void guiUpdateInitState() {

        mTextInput = (TextView) findViewById(R.id.messagesInputText);
        mTextInput.setEnabled(true);

        mValidationOutput = (TextView) findViewById(R.id.validationOutputText);
        mValidationOutput.setEnabled(false);

        mMessagesOutput = (TextView) findViewById(R.id.messagesOutputText);
        mMessagesOutput.setEnabled(false);

        findViewById(R.id.sendMessageButton).setEnabled(false);

        findViewById(R.id.idInRangeButton).setEnabled(true);

    }

    public void guiUpdateDisconnectedState() {

        mTextInput.setEnabled(true);

        findViewById(R.id.idInRangeButton).setEnabled(true);

    }

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

    private OnClickListener listenerSendButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
            findViewById(R.id.sendMessageButton).setEnabled(false);
            if (mBound) {
                mService.sendMessage(mTextInput.getText().toString());
            } else {
                Toast.makeText(v.getContext(), "Service not bound", Toast.LENGTH_SHORT).show();
            }


        }
    };


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

}
