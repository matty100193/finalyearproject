package com.matty_christopher.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Head2Head_home extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head2_head_home);

        Button challenge=(Button)findViewById(R.id.challenge_btn);
        Button matches=(Button)findViewById(R.id.matches_btn);

        assert challenge != null;
        challenge.setOnClickListener(this);
        assert matches != null;
        matches.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.challenge_btn:
                Intent intent = new Intent(Head2Head_home.this,Head2Head_create.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;

            case R.id.matches_btn:
                Intent intent1 = new Intent(Head2Head_home.this, Head2Head_matches.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent1);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Head2Head_home.this, Choose_mode.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
