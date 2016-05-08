package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "pt.ulisboa.tecnico.cmov.ubibike.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "UBLog MainActivity created.");

    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, SendMessageActivity.class);
        startActivity(intent);
    }

    public void sendPoints(View view) {
        Intent intent = new Intent(this, SendPointsActivity.class);
        startActivity(intent);
    }




}
