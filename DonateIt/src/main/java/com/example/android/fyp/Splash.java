package com.example.android.fyp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;

public class Splash extends AppCompatActivity {
    private Button signUp;
    private Button signIn;
    RadioButton D;
    RadioButton O;
    Button popup;
    Dialog dialog;
    PopupWindow popupWindow;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("HiddenUrl", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear(); //http://localhost/DonateIt/donorLogin.php?email=ssss&password=44
        editor.putString("URL", "http://192.168.1.6:8081/"); //wifi
        //editor.putString("URL", "http://192.168.43.152:8081/"); //mobile net
        //editor.putString("URL", "http://10.0.2.2:8081/"); //emulator
        editor.apply();

        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(2 * 1000);
                    // After 5 seconds redirect to another intent
                    Intent i = new Intent(getBaseContext(), ActivityValidationReg.class);
                    startActivity(i);
                    //Remove activity
                    finish();
                } catch (Exception ignored) {}
            }
        };
        // start thread
        background.start();
    }
}
