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


public class H2Head_view_invites extends AppCompatActivity implements View.OnClickListener {

    private TableLayout showinvites;
    private int current_invite;
    private User_details currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h2_head_view_invites);

        ImageButton accept = (ImageButton) findViewById(R.id.accept);
        ImageButton reject = (ImageButton) findViewById(R.id.reject);

        assert accept != null;
        accept.setOnClickListener(this);
        assert reject != null;
        reject.setOnClickListener(this);

        showinvites = (TableLayout) findViewById(R.id.invites_table);
    }

    @Override
    protected void onStart() {
        super.onStart();
        current_invite = -1;
        while (showinvites.getChildCount() > 1)
            showinvites.removeView(showinvites.getChildAt(showinvites.getChildCount() - 1));

        User_details_store userLocalStore = new User_details_store(this);
        currentUser = userLocalStore.loggedInUser();
        String challenger = currentUser.username;

        DB_connect_head2head connect = new DB_connect_head2head(this);
        connect.retreiveInvites(challenger, new AsyncResponse<ArrayList<ArrayList>>() {
            @Override
            public void processFinish(ArrayList<ArrayList> output) {
                if (output.size() > 0) {
                    fillTable(output);
                } else {
                    Toast.makeText(H2Head_view_invites.this, "Error connecting to server", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), homepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
        });

    }

    private void fillTable(ArrayList<ArrayList> columns) {

        ArrayList<Object> ids = new ArrayList<>();
        ArrayList<Object> challengers = new ArrayList<>();
        ArrayList<Object> dates = new ArrayList<>();
        ArrayList<Object> expires = new ArrayList<>();

        for (int i = 0; i < columns.get(0).size(); i++) {
            ids.add(columns.get(0).get(i));
            challengers.add(columns.get(1).get(i));
            dates.add(columns.get(2).get(i));
            expires.add(columns.get(3).get(i));
        }


        final ArrayList<RadioButton> groupRadioButtonList = new ArrayList<>();


        for (int i = 0; i < ids.size(); i++) {

            final TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            if ((i % 2) == 0) {
                row.setBackgroundColor(Color.rgb(182, 236, 255));
            }


            final TextView qid = new TextView(this);
            qid.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            qid.setTextColor(Color.BLACK);
            qid.setWidth(100);
            qid.setHeight(150);
            qid.setGravity(Gravity.CENTER);
            qid.setText("" + ids.get(i));
            row.addView(qid);

            TextView challenger = new TextView(this);
            challenger.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            challenger.setTextColor(Color.BLACK);
            challenger.setWidth(100);
            challenger.setHeight(150);
            challenger.setGravity(Gravity.CENTER);
            challenger.setText("" + challengers.get(i));
            row.addView(challenger);


            TextView date = new TextView(this);
            date.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            date.setTextColor(Color.BLACK);
            date.setWidth(120);
            date.setHeight(150);
            date.setGravity(Gravity.CENTER);
            String cleanDate = dates.get(i).toString();
            String sub = cleanDate.substring(0, 16);
            date.setText("" + sub);
            row.addView(date);

            SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long timeinterval=0;
            try {
                Date date1 = new Date();
                Date date2 = sdf.parse(""+expires.get(i));
                timeinterval=printDifference(date2, date1);

            } catch (Exception e) {
                Log.e("Tag", "Error parsing dates in h2h_view_invites", e);
            }
            CountDownActivity countDownTimer = new CountDownActivity(timeinterval, date,row);
            countDownTimer.start();

            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i);
            row.addView(radioButton);
            groupRadioButtonList.add(radioButton);

            //modified from http://stackoverflow.com/questions/22208468/radio-buttons-all-selectable-in-radio-group-after-adding-them-dynamically-android
            //author - Kristy Welsh used to uncheck radio buttons and check selected one
            radioButton.setOnClickListener(new RadioButton.OnClickListener() {
                public void onClick(View v) {
                    int id = v.getId();
                    for (RadioButton button : groupRadioButtonList) {
                        if (id == button.getId()) {
                            button.setChecked(true);
                            String current_value = qid.getText().toString();
                            current_invite = Integer.parseInt(current_value);

                        } else {
                            button.setChecked(false);
                        }
                    }
                }
            });
            row.setMinimumHeight(200);
            showinvites.addView(row);
        }


    }

    private long printDifference(Date endDate, Date startDate){
        return endDate.getTime() - startDate.getTime();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accept:

                if(validate()){
                    notifyChoice("accepted");
                }

                break;

            case R.id.reject:
                if(validate()){
                    notifyChoice("rejected");
                }
                break;
        }
    }

    private void notifyChoice(String userChoice) {

        String user=currentUser.username;
                new DB_connect_head2head(this).notifyChoice(userChoice, current_invite, user, new AsyncResponse<String>() {
                    @Override
                    public void processFinish(String output) {
                        Log.e("tag", "" + output+" "+showinvites.getChildCount());

                        if (output.contains("success")) {
                            Toast.makeText(H2Head_view_invites.this, "You're choice has been updated", Toast.LENGTH_SHORT).show();

                            for(int i = 1; i < showinvites.getChildCount(); i++)
                            {
                                TableRow row = (TableRow) showinvites.getChildAt(i);
                                TextView textView = (TextView) row.getChildAt(0);
                                int bvalue=0;
                                try {
                                     bvalue = Integer.parseInt(textView.getText().toString());
                                }
                                catch (NumberFormatException e){
                                     Log.e("tag","error parsing id (number format exception");
                                }
                                if (bvalue == current_invite) {
                                    showinvites.removeViewAt(i);
                                    showinvites.invalidate();
                                    current_invite = -1;
                                    break;
                                }

                            }

                        }
                        else {
                            Toast.makeText(H2Head_view_invites.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private boolean validate() {

        if(current_invite==-1){
            Toast.makeText(H2Head_view_invites.this, "Please select an invite first", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public class CountDownActivity extends CountDownTimer {
        final TableRow currentRow;
        final TextView currentview;
        public CountDownActivity(long millisInFuture, TextView currentview, TableRow currentRow) {
            super(millisInFuture, (long) 1000);
            this.currentRow=currentRow;
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
            currentview.setText("00:00:00");
            showinvites.removeView(currentRow);
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