package com.matty_christopher.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class register extends AppCompatActivity implements View.OnClickListener {

    private EditText ed_username;
    private EditText ed_password;
    private EditText ed_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ed_username=(EditText) findViewById(R.id.username_edit);
        ed_password=(EditText) findViewById(R.id.pass_edit);
        ed_email=(EditText) findViewById(R.id.edit_email);
        Button register_btn = (Button) findViewById(R.id.register_btn);

        assert register_btn != null;
        register_btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.register_btn:



                String u_name=ed_username.getText().toString();
                String u_pass=ed_password.getText().toString();
                String u_email=ed_email.getText().toString();

                if(u_name.equals("") || u_pass.equals("") || u_email.equals("")){
                    Toast.makeText(getApplicationContext(), "Please Fill in all fields",
                    Toast.LENGTH_SHORT).show();
        }
                else {
                    User_details registeredUser = new User_details(u_name, u_pass, u_email,0,0,0,0,0);
                    registerUser(registeredUser);
                }
                break;
        }

    }

    private void registerUser(User_details user){
        User_details_store userLocalStore = new User_details_store(this);
        User_details currentUser= userLocalStore.loggedInUser();
        if(currentUser!=null) {
            userLocalStore.logoutUser();
        }

        DBConnect dbConnect=new DBConnect(this);
        dbConnect.setUserData(user, new Db_response<User_details>() {
            @Override
            public void processFinish(User_details retUser) {

                String message = DBConnect.DatabaseMessage;

                if (message.contains("success")) {
                    Toast.makeText(getApplicationContext(), "Account created",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();

                } else if (message.contains("PRIMARY")) {
                    Toast.makeText(getApplicationContext(), "The username provided already exists",
                            Toast.LENGTH_SHORT).show();
                } else if (message.contains("Duplicate") && message.contains("email")) {
                    Toast.makeText(getApplicationContext(), "The Email address already exists",
                            Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(), "Could not create account!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}
