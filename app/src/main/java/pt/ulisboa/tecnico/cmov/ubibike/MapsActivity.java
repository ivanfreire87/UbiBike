package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback, ConnectionService.Callbacks {

    private GoogleMap mMap;
    private ConnectionService mService;
    private boolean mBound = false;
    private String username;
    private String stations;
    List<LatLng> track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        username = intent.getStringExtra(LoggedActivity.NAME);
        stations = intent.getStringExtra(LoggedActivity.STATIONS);

    }

    @Override
    protected void onStart() {
        super.onStart();

        track = new ArrayList<LatLng>();
        // Bind to LocalService
        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    public void sendTrack(List<LatLng> list){
        track = list;
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng centerMarkerLocation = null;



            String[] splitMessage = stations.split("\\|");
            Log.d("MainActivity", "DEBUG Stations. Result: " + stations);

            if (splitMessage[0].equals("OK")) {
                // OK|123|&stationName-xxxx+yyyy&stationName-xxxx+yyyy
                String[] stationSplit = splitMessage[2].split("&");
                for(int i=1; i < stationSplit.length; i++) {
                    String[] stationLocationsSplit = stationSplit[i].split(";");
                    String stationName = stationLocationsSplit[0];
                    Log.d("MainActivity", "DEBUG stationName: " + stationName);
                    String[] locationSplit = stationLocationsSplit[1].split("\\+");
                    LatLng location = new LatLng(Double.parseDouble(locationSplit[0]), Double.parseDouble(locationSplit[1]));
                    Log.d("MainActivity", "DEBUG location: " + location);

                    centerMarkerLocation = location;


                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(stationName))
                            .showInfoWindow();

                }
            }


        mMap.moveCamera(CameraUpdateFactory.newLatLng(centerMarkerLocation));
        mMap.setOnInfoWindowClickListener(this);




    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        if (mBound) {
            Log.d("MainActivity", "DEBUG marker.getTitle(): " + marker.getTitle());

            String sentMessageOnClick = "RESERVE|" + username +"|" + marker.getTitle();

            Log.d("MainActivity", "DEBUG Booking bike!!!!!." + sentMessageOnClick);


            String responseMessage = mService.sendMessageToCentralServer(sentMessageOnClick);

            String[] splitMessage = responseMessage.split("\\|");
            Log.d("MainActivity", "DEBUG Booking bike. Result: " + responseMessage);

            if (splitMessage[0].equals("OK")) {

                Toast.makeText(this, splitMessage[1], Toast.LENGTH_SHORT).show();
            }
            else {
                marker.remove();
                Toast.makeText(this, splitMessage[1], Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(this, "Service not bound", Toast.LENGTH_SHORT).show();
        }

    }

    public void eraseInput(){}
    public void GuiUpdateConnectedState(){}
    public void guiUpdateDisconnectedState(){}
    public void appendValuesOutput(String s){}
    public void setValidationOutput(String s){}
    public void displayDevicesInRange(CharSequence[] devices){}

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mService = binder.getService();
            mService.registerClient(MapsActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}
