package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by ivanf on 08/05/2016.
 */
public class RegisterActivity extends AppCompatActivity {

    public final static String EMAIL = "pt.ulisboa.tecnico.cmov.ubibike.MESSAGE";

    private String name;
    private String email;
    private String pw;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_register);
    }

    public void register(View view) {
        Intent intent = new Intent(this, LoggedActivity.class);

        EditText name_box = (EditText) findViewById(R.id.reg_name_box);
        EditText email_box = (EditText) findViewById(R.id.reg_email_box);
        EditText pw_box = (EditText) findViewById(R.id.reg_pw_box);

        email = name_box.getText().toString();
        email = email_box.getText().toString();
        pw = pw_box.getText().toString();

        intent.putExtra(EMAIL, email);
        startActivity(intent);
    }

}
