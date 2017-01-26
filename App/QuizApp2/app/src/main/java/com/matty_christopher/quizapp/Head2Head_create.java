package com.matty_christopher.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class Head2Head_create extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView users;
    private ArrayList<String> allusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head2_head_create);

        users=(AutoCompleteTextView)findViewById(R.id.user_chosen);

        Button submit = (Button) findViewById(R.id.send_challenge_btn);
        Button viewRequests = (Button) findViewById(R.id.view_requests);

        assert submit != null;
        submit.setOnClickListener(this);
        assert viewRequests != null;
        viewRequests.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        allusers=new ArrayList<>();


        DBConnect connect=new DBConnect(this);
        connect.getAllUsers(new AsyncResponse<ArrayList<String>>() {
            @Override
            public void processFinish(ArrayList<String> output) {
                if (output.size() > 0) {
                    fill(output);
                } else {
                    Toast.makeText(Head2Head_create.this, "Error connecting to server", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), homepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }

            }
        });



    }

    private void fill(ArrayList<String> output) {
        allusers=output;
        User_details_store userLocalStore = new User_details_store(this);
        User_details currentUser= userLocalStore.loggedInUser();
        allusers.remove(currentUser.username);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice, allusers);
        users.setThreshold(1);
        users.setAdapter(adapter);
        users.setValidator(new Validator());

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.send_challenge_btn:
                checkEmpty();
                break;

            case R.id.view_requests:
                Intent intent = new Intent(Head2Head_create.this, H2Head_view_invites.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
        }

    }

    private void checkEmpty() {
        String input;
        users.performValidation();
        input=users.getText().toString();
        if (input.equals("")) {
            Toast.makeText(Head2Head_create.this, "Please enter a user", Toast.LENGTH_SHORT).show();
        }
        else{
            User_details_store userLocalStore = new User_details_store(this);
            User_details currentUser= userLocalStore.loggedInUser();

            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf =  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(dt);

            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, 1);
            String expirytime = sdf.format(c.getTime());

            DB_connect_head2head connect=new DB_connect_head2head(this);
            connect.sendInvite(currentUser.username, input, currentTime,expirytime, new Db_response<String>() {
                @Override
                public void processFinish(String output) {
                    if(output.contains("success")){
                        Toast.makeText(Head2Head_create.this, "Request sent", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(Head2Head_create.this, "Request could not be sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    private class Validator implements AutoCompleteTextView.Validator {

        @Override
        public boolean isValid(CharSequence text) {

            return allusers.contains(text.toString());

        }

        @Override
        public CharSequence fixText(CharSequence invalidText) {
            return "";
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
