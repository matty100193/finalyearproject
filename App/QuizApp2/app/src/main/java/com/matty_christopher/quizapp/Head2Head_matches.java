package com.matty_christopher.quizapp;


import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Head2Head_matches extends AppCompatActivity implements View.OnClickListener {

    private TableLayout showMatches;
    private int matchselected;
    private ArrayList<Object> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head2_head_matches);

        ImageButton playMatch = (ImageButton) findViewById(R.id.select);
        assert playMatch != null;
        playMatch.setOnClickListener(this);

        showMatches = (TableLayout) findViewById(R.id.matches_table);
    }

    @Override
    protected void onStart() {
        super.onStart();
        matchselected=-1;

        while (showMatches.getChildCount() > 1)
            showMatches.removeView(showMatches.getChildAt(showMatches.getChildCount() - 1));

        User_details_store userLocalStore = new User_details_store(this);
        User_details user=userLocalStore.loggedInUser();

        new DB_connect_head2head(this).retreiveMatches(user.username, new AsyncResponse<ArrayList<ArrayList>>() {
            @Override
            public void processFinish(ArrayList<ArrayList> output) {
                if (output.size() > 0) {
                    fillTable(output);
                } else {
                    Toast.makeText(Head2Head_matches.this, "Error connecting to server", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), homepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
        });

    }

    private void fillTable(ArrayList<ArrayList> responseOutput) {

         final ArrayList<Object> match_ids= new ArrayList<>();
         ArrayList<Object>  opponents= new ArrayList<>();
         ArrayList<Object>  expiry_dates= new ArrayList<>();
         ArrayList<Object>  statuses= new ArrayList<>();

        for (int i = 0; i < responseOutput.get(0).size(); i++) {
            match_ids.add(responseOutput.get(0).get(i));
            opponents.add(responseOutput.get(1).get(i));
            expiry_dates.add(responseOutput.get(2).get(i));
            statuses.add(responseOutput.get(3).get(i));
        }
        ids=match_ids;

        final ArrayList<RadioButton> groupRadioButtonList = new ArrayList<>();


        for (int i = 0; i < match_ids.size(); i++) {

            final TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            if ((i % 2) == 0) {
                row.setBackgroundColor(Color.rgb(182, 236, 255));
            }



            TextView challenger = new TextView(this);
            challenger.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            challenger.setTextColor(Color.BLACK);
            challenger.setWidth(170);
            challenger.setHeight(150);
            challenger.setGravity(Gravity.CENTER);
            challenger.setText("" + opponents.get(i));
            row.addView(challenger);


            TextView date = new TextView(this);
            date.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            date.setTextColor(Color.BLACK);
            date.setWidth(70);
            date.setHeight(150);
            date.setGravity(Gravity.CENTER);
            date.setText("00:00:00");
            row.addView(date);


            TextView status = new TextView(this);
            status.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            status.setTextColor(Color.BLACK);
            status.setWidth(120);
            status.setHeight(150);
            status.setGravity(Gravity.CENTER);
            status.setText(""+statuses.get(i));
            row.addView(status);

            SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long timeinterval=0;
            try {
                Date date1 = new Date();
                Date date2 = sdf.parse(""+expiry_dates.get(i));
                timeinterval=printDifference(date2, date1);

            } catch (Exception e) {
                Log.e("Tag", "Error parsing dates in h2h_view_invites", e);
            }
            CountDownActivity countDownTimer = new CountDownActivity(timeinterval, date,status);
            countDownTimer.start();

            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i);
            row.addView(radioButton);
            groupRadioButtonList.add(radioButton);
            final String current_id= String.valueOf(match_ids.get(i));

            radioButton.setOnClickListener(new RadioButton.OnClickListener() {
                public void onClick(View v) {
                    int id = v.getId();
                    for (RadioButton button : groupRadioButtonList) {
                        if (id == button.getId()) {
                            button.setChecked(true);
                            matchselected = Integer.parseInt(current_id);

                        } else {
                            button.setChecked(false);
                        }
                    }
                }
            });
            row.setMinimumHeight(200);
            showMatches.addView(row);
        }

    }

    private long printDifference(Date endDate, Date startDate){
        return endDate.getTime() - startDate.getTime();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.select:
                if( matchselected==-1){
                    Toast.makeText(Head2Head_matches.this, "Please select a match", Toast.LENGTH_SHORT).show();
                }
                else{
                    //if incomplete take user to play, if waiting = output message waiting for opponent, if view take user to screen showing result,
                    //if complete then match shouldnt show in table

                    String status="error";
                    int j=1;
                    for(int i = 0; i < ids.size() && j<showMatches.getChildCount(); i++)
                    {
                        TableRow row = (TableRow) showMatches.getChildAt(j);
                        int bvalue=0;
                        try {
                            bvalue = Integer.parseInt(String.valueOf(ids.get(i)));
                        }
                        catch (NumberFormatException e){
                            Log.e("tag","error parsing id (number format exception");
                        }
                        if (bvalue == matchselected) {
                            TextView stat = (TextView) row.getChildAt(2);
                            status=stat.getText().toString();
                            break;
                        }
                        j++;
                    }
                    dealWithStatus(status);

                }
                break;

        }
    }

    //not fully implemented yet
    private void dealWithStatus(String status) {
        switch (status){

            case "incomplete":
                Toast.makeText(Head2Head_matches.this, "play now", Toast.LENGTH_SHORT).show();
                //take user to play quiz
                Intent edit_selected = new Intent(Head2Head_matches.this, Play_ready_panel.class);
                edit_selected.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                edit_selected.putExtra("match", matchselected);
                startActivity(edit_selected);
                break;

            case "view now":
                //Toast.makeText(Head2Head_matches.this, "you can view match result", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Head2Head_matches.this, MatchView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("match_id", matchselected);
                intent.putExtra("status","view now");
                startActivity(intent);
                //take user to popupwindow showing results - unless opponent status is in_progress- tell user that opponent is currently playing quiz
                break;

            case "check":
                Intent intent1 = new Intent(Head2Head_matches.this, MatchView.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent1.putExtra("match_id",matchselected);
                intent1.putExtra("status","check");
                startActivity(intent1);
                //Toast.makeText(Head2Head_matches.this, "waiting for opponent to finish", Toast.LENGTH_SHORT).show();
                break;

            default:
                Toast.makeText(Head2Head_matches.this, "error", Toast.LENGTH_SHORT).show();
                break;

        }

    }


    public class CountDownActivity extends CountDownTimer {
        final TextView status;
        final TextView currentview;
        public CountDownActivity(long millisInFuture, TextView currentview, TextView status) {
            super(millisInFuture, (long) 1000);
            this.status=status;
            this.currentview=currentview;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //hms used from https://www.youtube.com/watch?v=ZqqP69rJVmg author - Indragni Soft Solutions
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

            currentview.setText(hms);
        }

        @Override
        public void onFinish() {
            currentview.setText("Ended");
            status.setText("view now");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Head2Head_home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        //finish();
    }
}
