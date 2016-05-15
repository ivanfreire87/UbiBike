package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivanf on 08/05/2016.
 */
public class LoggedActivity extends AppCompatActivity implements ConnectionService.Callbacks {
    public final static String NAME = "pt.ulisboa.tecnico.cmov.ubibike.NAME";
    public final static String STATIONS = "pt.ulisboa.tecnico.cmov.ubibike.STATIONS";
    public final static String POINTS = "pt.ulisboa.tecnico.cmov.ubibike.POINTS";

    private String username;
    private ConnectionService mService;
    private boolean mBound = false;
    private int points;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        username = intent.getStringExtra(LoginActivity.NAME);

        setTitle("UbiBike - " + username);

        setContentView(R.layout.activity_logged);
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

    public void messages(View view) {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
    }

    public void trajectory(View view) {
        if (mBound) {
            String trajectory = "&trajectory1name-11.11+22.22-44.44+55.55-77.77+88.88";
            String sentMessage = "SENDTRAJECTORY|" + username +"|" + trajectory;

            Log.d("MainActivity", "DEBUG Sending trajectory." + sentMessage);


            String responseMessage = mService.sendMessageToCentralServer(sentMessage);

            String[] splitMessage = responseMessage.split("\\|");
            Log.d("MainActivity", "DEBUG Sending trajectory. Result: " + responseMessage);

            if (splitMessage[0].equals("OK")) {
                Toast.makeText(LoggedActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(LoggedActivity.this, "Error sending trajectory.", Toast.LENGTH_SHORT).show();



        } else {
            Toast.makeText(LoggedActivity.this, "Service not bound", Toast.LENGTH_SHORT).show();
        }
    }

    public void dropBikeOnStation(View view) {

        final CharSequence[] stations = getStations();

        new AlertDialog.Builder(this)
                .setTitle("Select a station to drop a bike")
                .setItems(stations, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String option = stations[which].toString();

                        if (mBound) {
                            dropBike(option);
                        } else {
                            Toast.makeText(LoggedActivity.this, "Service not bound", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public CharSequence[]  getStations(){

        CharSequence[] stations;
        List<CharSequence> charSequences = new ArrayList<>();
        if (mBound) {

            String sentMessage = "STATIONS|" + username;

            Log.d("MainActivity", "DEBUG Sending REQUEST FORSTATIONS." + sentMessage);

            String responseMessage = mService.sendMessageToCentralServer(sentMessage);

            String[] splitMessage = responseMessage.split("\\|");
            Log.d("MainActivity", "DEBUG Sending trajectory. Result: " + responseMessage);

            if (splitMessage[0].equals("OK")) {
                String[] stationSplit = splitMessage[2].split("&");
                for(int i=1; i < stationSplit.length; i++) {
                    //OK|123|&stationName-xxxx+yyyy&stationName-xxxx+yyyy&stationName-xxxx+yyyy&stationName-xxxx+yyyy
                    String[] stationLocationsSplit = stationSplit[i].split(";");
                    String station = stationLocationsSplit[0];

                    Log.d("MainActivity", "Station: " + station);
                    charSequences.add(station);
                }
            }
            else
                Toast.makeText(LoggedActivity.this, "Error getting available stations.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoggedActivity.this, "Service not bound", Toast.LENGTH_SHORT).show();
        }
        stations = charSequences.toArray(new
                CharSequence[charSequences.size()]);
        return stations;
    }

    public void dropBike(String station){
        if (mBound) {

            String sentMessage = "DROP|" + username +"|" + station;

            Log.d("MainActivity", "DEBUG Droping bike." + sentMessage);

            String responseMessage = mService.sendMessageToCentralServer(sentMessage);

            String[] splitMessage = responseMessage.split("\\|");
            Log.d("MainActivity", "DEBUG Droping bike. Result: " + responseMessage);

            if (splitMessage[0].equals("OK")) {
                Toast.makeText(LoggedActivity.this, splitMessage[1], Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(LoggedActivity.this, splitMessage[1], Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(LoggedActivity.this, "Service not bound", Toast.LENGTH_SHORT).show();
        }
    }

    public void stationsMap(View view){
        String responseMessage = null;

        if (mBound) {

            String sentMessage = "STATIONSBIKES|" + username;

            Log.d("MainActivity", "DEBUG Requesting stations." + sentMessage);

            responseMessage = mService.sendMessageToCentralServer(sentMessage);

        } else {
            Toast.makeText(this, "Service not bound!!", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(NAME, username);
        intent.putExtra(STATIONS, responseMessage);
        startActivity(intent);
    }

    public void points(View view) {
        Intent intent = new Intent(this, PointsActivity.class);

        if (mBound) {
            String sentMessage = "USERINFO|" + username;

            Log.d("MainActivity", "DEBUG Asking user info." + sentMessage);


            String responseMessage = mService.sendMessageToCentralServer(sentMessage);

            String[] splitMessage = responseMessage.split("\\|");
            Log.d("MainActivity", "DEBUG Asking user info. Result: " + responseMessage);

            if (splitMessage[0].equals("OK")) {
                Log.d("MainActivity", "DEBUG SPLIT: " + splitMessage[1]);
                points = Integer.parseInt(splitMessage[1]);
            }
            else
                Toast.makeText(LoggedActivity.this, "Error retrieving user info.", Toast.LENGTH_SHORT).show();



        } else {
            Toast.makeText(LoggedActivity.this, "Service not bound", Toast.LENGTH_SHORT).show();
        }
        Log.d("MainActivity", "DEBUG  username " + username);
        intent.putExtra(NAME, username);
        Log.d("MainActivity", "DEBUG  points " + points);
        intent.putExtra(POINTS, points);
        startActivity(intent);
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
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mService = binder.getService();
            mService.registerClient(LoggedActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
