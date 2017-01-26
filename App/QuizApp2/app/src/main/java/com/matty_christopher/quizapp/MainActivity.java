package com.matty_christopher.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        TextView myTextView=(TextView)findViewById(R.id.welcome_header);
//        Typeface tf = FontCache.get("fonts/" + "delarge.ttf", this);
//        myTextView.setTypeface(tf);

        Button btn_login = (Button) findViewById(R.id.welcome_loginbtn);
        assert btn_login != null;
        btn_login.setOnClickListener(this);

        Button btn_createAcc = (Button) findViewById(R.id.createAcc_btn);
        assert btn_createAcc != null;
        btn_createAcc.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.welcome_loginbtn:
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
                finish();
                break;

            case R.id.createAcc_btn:
                Intent intent1 = new Intent(MainActivity.this, register.class);
                startActivity(intent1);
                finish();
                break;

        }

    }

    @Override
    public void onBackPressed() {
    }
}
