package com.matty_christopher.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Create_question_mode_option extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question_mode_option);

        Button single = (Button) findViewById(R.id.create_on_own_btn);
        Button group = (Button) findViewById(R.id.create_group_btn);

        assert single != null;
        single.setOnClickListener(this);
        assert group != null;
        group.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.create_group_btn:
                startActivity(new Intent(Create_question_mode_option.this,AddGroup.class));
                break;

            case R.id.create_on_own_btn:
                startActivity(new Intent(Create_question_mode_option.this,Add_Question.class));
                break;

        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Create_question_start.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        //finish();
    }

}
