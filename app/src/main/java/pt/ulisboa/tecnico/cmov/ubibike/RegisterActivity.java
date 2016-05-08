package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by ivanf on 08/05/2016.
 */
public class RegisterActivity extends AppCompatActivity {

    private String name;
    private String email;
    private String password;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_register);
    }

    public void register(View view) {
        Intent intent = new Intent(this, LoggedActivity.class);
        startActivity(intent);
    }

}
