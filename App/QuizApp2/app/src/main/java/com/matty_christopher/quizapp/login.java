package com.matty_christopher.quizapp;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class login extends AppCompatActivity implements View.OnClickListener {

    private EditText ed_username;
    private EditText ed_password;
    private User_details_store userLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_username=(EditText) findViewById(R.id.username_edit);
        ed_password=(EditText) findViewById(R.id.pass_edit);
        Button submit = (Button) findViewById(R.id.login_btn);
        TextView forgotDetails = (TextView) findViewById(R.id.request_email);
        userLocalStore=new User_details_store(this);
        assert forgotDetails != null;
        forgotDetails.setOnClickListener(this);

        assert submit != null;
        submit.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        User_details currentUser= userLocalStore.loggedInUser();

        if(!currentUser.username.equals("") && !currentUser.password.equals("")) {
            ed_username.setText(currentUser.username);
            ed_password.setText(currentUser.password);
            Intent i = new Intent(this, homepage.class);
           // i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);


        }
        else{
            ed_username.setText("");
            ed_password.setText("");
        }


    }

    @Override
    public void onClick(View v) {

       switch (v.getId()){

           case R.id.login_btn:

               String username=ed_username.getText().toString();
               String pass=ed_password.getText().toString();
               User_details user=new User_details(username,pass);
               authenticate(user);

               break;


           case R.id.request_email:
               alert();

               break;

       }

    }

    private void authenticate(User_details user){

        final DBConnect dbConnect= new DBConnect(this);
        dbConnect.getUserData(user, new Db_response<User_details>() {
            @Override
            public void processFinish(User_details retUser) {
                if (retUser == null) {
                    Toast.makeText(getApplicationContext(), "Username or Password is incorrect",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    loginUser(retUser);
                }
            }
        });

    }

    private void loginUser(User_details user){
        userLocalStore.storeData(user);
        Intent i = new Intent(this, homepage.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }



    /*
    * used from http://www.mkyong.com/android/android-prompt-user-input-dialog-example/
    * author - mkyong  used to allow user to enter email address to send user details
     */
    private void alert(){
        LayoutInflater layoutInflater = LayoutInflater.from(login.this);
        @SuppressLint("InflateParams") View promptView = layoutInflater.inflate(R.layout.emailprompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(login.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.enterEmail);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        DBConnect dbConnect= new DBConnect(login.this);
                        dbConnect.sendUserEmail(editText.getText().toString());

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
