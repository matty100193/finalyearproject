package com.matty_christopher.quizapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class leaderboard extends AppCompatActivity{

    private User_details_store userLocalStore;
    private TableLayout showLeaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        userLocalStore=new User_details_store(this);

        showLeaderboard = (TableLayout) findViewById(R.id.leaderboard_table);

    }



    @Override
    protected void onStart() {
        super.onStart();
        while (showLeaderboard.getChildCount() > 1)
            showLeaderboard.removeView(showLeaderboard.getChildAt(showLeaderboard.getChildCount() - 1));

        DBConnect dbConnect=new DBConnect(this);
        dbConnect.getTop50(new AsyncResponse <ArrayList<ArrayList>> () {

            @Override

            public void processFinish(ArrayList<ArrayList> output) {
                if(output.size()>0){
                    fillTable(output);
                }
                else {
                    Toast.makeText(leaderboard.this, "Error connecting to server", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), homepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }

        });




    }

    private void fillTable(ArrayList<ArrayList> output){
        ArrayList<Object> users=new ArrayList<>();

        ArrayList<Object> hscores=new ArrayList<>();

        for(int i=0;i<output.get(0).size();i++){
            users.add(output.get(0).get(i));
            hscores.add(output.get(1).get(i));
        }



        for (int i = 0; i <users.size(); i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            if((i%2)!=0){
                row.setBackgroundColor(Color.rgb(182,236,255));
            }

            TextView rank = new TextView(this);
            rank.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            rank.setTextColor(Color.BLACK);
            rank.setWidth(50);
            rank.setHeight(75);
            rank.setGravity(Gravity.START);
            rank.setText("" + (i + 1));
            row.addView(rank);

            TextView username = new TextView(this);
            username.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            username.setTextColor(Color.BLACK);
            username.setWidth(100);
            username.setHeight(75);
            username.setGravity(Gravity.CENTER);
            username.setText("" + users.get(i));
            row.addView(username);

            TextView score = new TextView(this);
            score.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            score.setTextColor(Color.BLACK);
            score.setWidth(100);
            score.setHeight(75);
            score.setGravity(Gravity.CENTER);
            score.setText("" + hscores.get(i));
            row.addView(score);

            row.setMinimumHeight(75);

            User_details currentUser=userLocalStore.loggedInUser();

            if(users.get(i).equals(currentUser.username)){
                row.setBackgroundColor(Color.rgb(0,255,51));
            }

            row.setMinimumHeight(150);
            showLeaderboard.addView(row);

        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), homepage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

    }

}
