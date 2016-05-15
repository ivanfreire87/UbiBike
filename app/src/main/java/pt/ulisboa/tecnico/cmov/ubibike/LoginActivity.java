package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by ivanf on 08/05/2016.
 */
public class LoginActivity extends AppCompatActivity implements ConnectionService.Callbacks {
    public final static String NAME = "pt.ulisboa.tecnico.cmov.ubibike.MESSAGE";

    private ConnectionService mService;
    private boolean mBound = false;
    private String username;
    private String pw;
    private TextView mTextOutput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        guiSetButtonListeners();
        mTextOutput = (TextView) findViewById(R.id.loginOutputText);
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

    private View.OnClickListener listenerLoginButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mTextOutput.setText("");
            if (mBound) {
                String sentMessage = getLoginMessage();

                if(username == null || pw == null ){
                    mTextOutput.setText("Username and password are mandatory!");
                }
                else {

                    String responseMessage = mService.sendMessageToCentralServer(sentMessage);

                    String[] splitMessage = responseMessage.split("\\|");

                    Log.d("MainActivity", "DEBUG Message Sent " + sentMessage);
                    Log.d("MainActivity", "DEBUG Message Received " + responseMessage);

                    if (splitMessage[0].equals("OK"))
                        loadLoggedActivity();
                    else
                        mTextOutput.setText(splitMessage[1]);
                }
            } else {
                Toast.makeText(v.getContext(), "Service not bound", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //TODO install termite no mac
    public String getLoginMessage(){

        EditText name_box = (EditText) findViewById(R.id.log_name_box);
        EditText pw_box = (EditText) findViewById(R.id.log_pw_box);

        username = name_box.getText().toString();
        pw = pw_box.getText().toString();

        return "LOGIN|" + username + "|" + pw;
    }

    public void loadLoggedActivity(){
        Intent intent = new Intent(this, LoggedActivity.class);
        intent.putExtra(NAME, username);

        startActivity(intent);
    }

    public void guiSetButtonListeners() {
        findViewById(R.id.loginButton).setOnClickListener(listenerLoginButton);
    }

    public void eraseInput(){}
    public void GuiUpdateConnectedState(){}
    public void guiUpdateDisconnectedState(){}
    public void appendValuesOutput(String s){}
    public void setValidationOutput(String s){}
    public void displayDevicesInRange(CharSequence[] devices){}
    public void sendTrack(List<LatLng> list){}

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mService = binder.getService();
            mService.registerClient(LoginActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}
