package com.matty_christopher.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Create_quiz extends AppCompatActivity implements View.OnClickListener {

    private EditText quiz_name;
    private EditText quiz_pwd;
    private User_details_store userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        quiz_name=(EditText)findViewById(R.id.quiz_name_edit);
        quiz_pwd=(EditText)findViewById(R.id.quiz_password_edit);

        Button submit = (Button) findViewById(R.id.create_submit_btn);
        assert submit != null;
        submit.setOnClickListener(this);

        userLocalStore=new User_details_store(this);




    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.create_submit_btn:

                User_details currentUser= userLocalStore.loggedInUser();
                String username=currentUser.username;

                String qname=quiz_name.getText().toString();
                String password=quiz_pwd.getText().toString();

                if(qname.equals("") || password.equals("")){
                    Toast.makeText(getApplicationContext(), "Please Fill in all fields",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    submitQuiz(qname,password,username);
                }

                break;

        }
    }

    private void submitQuiz(String qname,String pwd,String username){

        DBConnect_create_quiz dbConnect_create_quiz=new DBConnect_create_quiz(this);
        dbConnect_create_quiz.setUpQuiz(qname, pwd, username, new Db_response<String> () {
            @Override
            public void processFinish(String output) {

                if (output.contains("success")) {
                    Toast.makeText(getApplicationContext(), "Quiz created!",
                            Toast.LENGTH_SHORT).show();

                } else if (output.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Could not create quiz!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "A quiz already exists with that name!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Create_quiz.this, Create_homepage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        //finish();
    }


}
