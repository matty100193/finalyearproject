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
import java.util.HashSet;
import java.util.Set;

public class AddGroup extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView[] users;

    private ArrayList<String> allusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        users=new AutoCompleteTextView[3];
        users[0]=(AutoCompleteTextView)findViewById(R.id.user1);
        users[1]=(AutoCompleteTextView)findViewById(R.id.user2);
        users[2]=(AutoCompleteTextView)findViewById(R.id.user3);

        Button submit = (Button) findViewById(R.id.submit);


        assert submit != null;
        submit.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        allusers=new ArrayList<>();

        DBConnect connect=new DBConnect(this);
        connect.getAllUsers(new AsyncResponse<ArrayList<String>>() {
            @Override
            public void processFinish(ArrayList<String> output) {
                if(output.size()>0){
                    fill(output);
                }
                else{
                    Toast.makeText(AddGroup.this, "Error connecting to server", Toast.LENGTH_LONG).show();
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
        for (AutoCompleteTextView user : users) {
            user.setThreshold(1);
            user.setAdapter(adapter);
            user.setValidator(new Validator());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                checkEmpty();
                break;
        }

    }

    private void checkEmpty() {
        String[] input=new String[users.length];
        for(int i=0;i<users.length;i++){
            users[i].performValidation();
            input[i]=users[i].getText().toString();
        }

        for (String anInput : input) {
            if (!anInput.equals("")) {
                saveInput(input);
                return;
            }
        }
        Toast.makeText(AddGroup.this, "Please enter at least one user", Toast.LENGTH_SHORT).show();

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


    private void saveInput(String[] input) {

        Set<String> userset=new HashSet<>();

        for (String anInput : input) {
            if (!anInput.equals("")) {
                userset.add(anInput);
            }
        }
        Group_details_store store=new Group_details_store(this);
        Group_details group=new Group_details(userset);
        store.storeData(group);

        Intent intent = new Intent(AddGroup.this, Add_Question.class);
        intent.putExtra("activity","group");
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Create_question_mode_option.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        //finish();
    }

}
