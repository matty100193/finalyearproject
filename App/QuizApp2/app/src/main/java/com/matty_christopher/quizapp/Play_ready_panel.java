package com.matty_christopher.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;


public class Play_ready_panel extends AppCompatActivity implements View.OnClickListener {

    private String selected_quiz;
    private int selected_match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_ready_panel);

        Button play = (Button) findViewById(R.id.play);
        Button cancel = (Button) findViewById(R.id.cancel);

        assert play != null;
        play.setOnClickListener(this);
        assert cancel != null;
        cancel.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        selected_quiz="";
        selected_match=-1;
        Intent intent = getIntent();

        if(intent.getExtras()!=null) {
            if (intent.getExtras().containsKey("quiz_name")) {
                selected_quiz = intent.getStringExtra("quiz_name");
            }
            else if(intent.getExtras().containsKey("match")){
                selected_match= intent.getIntExtra("match", -1);
            }
        }
        //Toast.makeText(Play_ready_panel.this, ""+selected_match, Toast.LENGTH_SHORT).show();



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.play:
                DBConnect_play connect_play=new DBConnect_play(this);

                if(selected_match!=-1){
                    loadMatch(connect_play);
                }
                else{
                    loadQuiz(connect_play);
                }
                break;

            case R.id.cancel:
                selected_quiz="";
                selected_match=-1;
                Intent intent = new Intent(getApplicationContext(), Choose_mode.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;


        }


    }

    private void loadMatch(DBConnect_play connect_play) {
        connect_play.getMatchQuestions(selected_match, new AsyncResponse<ArrayList<Question_details>>() {
            @Override
            public void processFinish(ArrayList<Question_details> output) {
                if (output.size() == 0) {
                    Toast.makeText(Play_ready_panel.this, "Error loading quiz", Toast.LENGTH_SHORT).show();
                    finish();
                } else {

                    for (int i = 0; i < output.size(); i++) {
                        Question_details curr = output.get(i);

                        Log.e("tag", "" + curr.qid);
                    }
                    Intent intent = new Intent(Play_ready_panel.this, Question_template.class);
                    intent.putExtra("Questions_list", output);
                    intent.putExtra("mode", "normal");
                    intent.putExtra("h2h_id", selected_match);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    //finish();
                }
            }
        });
    }


    private void loadQuiz(DBConnect_play connect_play) {

        connect_play.getQuestions(selected_quiz, new AsyncResponse<ArrayList<Question_details>>() {
            @Override
            public void processFinish(ArrayList<Question_details> output) {
                if (output.size()==0) {
                    Toast.makeText(Play_ready_panel.this, "Error loading quiz", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Intent intent = new Intent(Play_ready_panel.this, Question_template.class);
                    intent.putExtra("Questions_list", output);
                    if(selected_quiz.equals("")){
                        intent.putExtra("mode", "highscore");
                    }
                    else{
                        intent.putExtra("mode", "normal");
                    }
                    intent.putExtra("h2h_id", -1);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    //finish();

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

}
