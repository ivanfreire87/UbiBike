package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by ivanf on 08/05/2016.
 */
public class LoggedActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String email = intent.getStringExtra(LoginActivity.NAME);

        setTitle("UbiBike - " + email);

        setContentView(R.layout.activity_logged);
    }

    public void messages(View view) {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
    }

    public void points(View view) {
        Intent intent = new Intent(this, PointsActivity.class);
        startActivity(intent);
    }

}
