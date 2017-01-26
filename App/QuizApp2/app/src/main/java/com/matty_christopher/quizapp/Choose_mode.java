package com.matty_christopher.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Choose_mode extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

        Button high = (Button) findViewById(R.id.points_btn);
        Button normal = (Button) findViewById(R.id.normal_btn);
        Button h2h = (Button) findViewById(R.id.head2head_btn);

        assert high != null;
        high.setOnClickListener(this);
        assert h2h != null;
        h2h.setOnClickListener(this);
        assert normal != null;
        normal.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.points_btn:
                Intent edit_selected = new Intent(Choose_mode.this, Play_ready_panel.class);
                edit_selected.putExtra("quiz_name", "");
                edit_selected.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(edit_selected);
                break;

            case R.id.normal_btn:
                Intent intent1=new Intent(Choose_mode.this, Play_choose_quiz.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent1);
                break;

            case R.id.head2head_btn:
                Intent intent2=new Intent(Choose_mode.this, Head2Head_home.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent2);
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
