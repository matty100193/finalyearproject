package com.matty_christopher.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Create_homepage extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_homepage);

        Button new_quiz = (Button) findViewById(R.id.new_quiz_btn);
        Button view_group = (Button) findViewById(R.id.view_groups_btn);
        Button question=(Button)findViewById(R.id.new_question_btn);
        ImageButton help=(ImageButton)findViewById(R.id.imageButton);

        assert new_quiz != null;
        new_quiz.setOnClickListener(this);
        assert view_group != null;
        view_group.setOnClickListener(this);
        assert question != null;
        question.setOnClickListener(this);
        assert help != null;
        help.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.view_groups_btn:
                Intent intent1=new Intent(Create_homepage.this, View_Groups.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent1);
                break;

            case R.id.new_quiz_btn:
                Intent intent2=new Intent(Create_homepage.this, Create_quiz.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent2);
                break;

            case R.id.new_question_btn:
                Intent intent3=new Intent(Create_homepage.this, Create_question_start.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent3);
                break;

            case R.id.imageButton:
                Intent intent4=new Intent(Create_homepage.this, Help_create.class);
                intent4.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent4);
                break;

        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), homepage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        //finish();
    }
}
