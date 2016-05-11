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

/**
 * Created by ivanf on 08/05/2016.
 */
public class RegisterActivity extends Activity implements ConnectionService.Callbacks{

    private ConnectionService mService;
    private boolean mBound = false;
    private String username;
    private String pw;
    private TextView mTextOutput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        guiSetButtonListeners();
        mTextOutput = (TextView) findViewById(R.id.registerOutputText);
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

    private View.OnClickListener listenerRegisterButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "clicked register", Toast.LENGTH_SHORT).show();
            mTextOutput.setText("");
            if (mBound) {
                String sentMessage = getRegisterMessage();

                if(username == null || pw == null ){
                    mTextOutput.setText("Username and password are mandatory!");
                }
                else {

                    String responseMessage = mService.sendMessageToCentralServer(sentMessage);

                    String[] splitMessage = responseMessage.split("\\|");

                    Log.d("MainActivity", "DEBUG Message Sent " + sentMessage);
                    Log.d("MainActivity", "DEBUG Message Received " + responseMessage);

                    if (splitMessage[0].equals("OK"))
                        loadLoginActivity();
                    else
                        mTextOutput.setText(splitMessage[1]);
                }
            } else {
                Toast.makeText(v.getContext(), "Service not bound", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public String getRegisterMessage(){

        EditText name_box = (EditText) findViewById(R.id.reg_name_box);
        EditText pw_box = (EditText) findViewById(R.id.reg_pw_box);

        username = name_box.getText().toString();
        pw = pw_box.getText().toString();

        return "REGISTER|" + username + "|" + pw;
    }

    public void loadLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
    }

    public void guiSetButtonListeners() {
        findViewById(R.id.registerButton).setOnClickListener(listenerRegisterButton);
    }

    public void eraseInput(){}
    public void GuiUpdateConnectedState(){}
    public void guiUpdateDisconnectedState(){}
    public void appendOutput(String s){}
    public void displayDevicesInRange(CharSequence[] devices){}



    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mService = binder.getService();
            mService.registerClient(RegisterActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
