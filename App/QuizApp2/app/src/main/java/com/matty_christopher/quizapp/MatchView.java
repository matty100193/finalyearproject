package com.matty_christopher.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MatchView extends AppCompatActivity implements View.OnClickListener {


    private TextView score_you;
    private TextView score_opponent;
    private TextView status;
    private TextView opponent_header;
    private String current_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);

        score_you=(TextView)findViewById(R.id.score_you);
        score_opponent=(TextView)findViewById(R.id.score_oppo);
        status=(TextView)findViewById(R.id.status);
        opponent_header=(TextView)findViewById(R.id.opponent_header);

        Button submit=(Button)findViewById(R.id.submit);
        assert submit != null;
        submit.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        int id=-1;
        Intent intent=getIntent();
        if(intent.getExtras()!=null) {
            if (intent.getExtras().containsKey("match_id")) {
                id = intent.getIntExtra("match_id",-1);
            }
            if (intent.getExtras().containsKey("status")) {
                current_status = intent.getStringExtra("status");
            }
        }

        DB_connect_head2head connect=new DB_connect_head2head(this);
        connect.getMatchDetails(id, new AsyncResponse<Match_Details>() {
            @Override
            public void processFinish(Match_Details output) {
                if (output != null) {
                    fillView(output);
                }
            }
        });

    }

    private void fillView(Match_Details output) {

        int user_score=output.challenger_score;
        int opponent_score=output.creator_score;
        String oppo=output.creator_name;
        String oppo_status=output.status_creator;

        User_details_store user_details_store=new User_details_store(this);
        User_details current_user=user_details_store.loggedInUser();

        if(current_user.username.equals(output.creator_name)){
            user_score=output.creator_score;
            opponent_score=output.challenger_score;
            oppo=output.challenger_name;
            oppo_status=output.status_challenger;
        }


        if(current_status.equals("check") && oppo_status.equals("incomplete")){
            oppo_status="Waiting for user";
        }
        else{
            if(user_score>opponent_score){
                oppo_status="You won";
            }
            else  if(user_score<opponent_score){
                oppo_status="You Lost";
            }
            else{
                oppo_status="You Drew";
            }
        }


        status.setText(""+oppo_status);
        opponent_header.setText(""+oppo);
        score_you.setText(""+user_score);
        score_opponent.setText("" + opponent_score);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.submit:
                Intent intent = new Intent(getApplicationContext(), Head2Head_matches.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
        }
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
