package com.matty_christopher.quizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private User_details_store userLocalStore;
    private User_details loggedIn;
    private ImageView hscore_1;
    private ImageView hscore_2;
    private ImageView correct_1;
    private ImageView answered_1;
    private ProgressBar[] bars;
    private TextView[] achievement_txt;
    private EditText email;
    private EditText pass;
    private TextView hscore_txt;
    private TextView perctent_txt;
    private CheckBox showpwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.85), (int) (height * 0.8));

        email=(EditText)findViewById(R.id.editText_email);
        pass=(EditText)findViewById(R.id.editText_pwd);
        hscore_txt=(TextView)findViewById(R.id.hscore_text);
        perctent_txt=(TextView)findViewById(R.id.percent_text);
        showpwd=(CheckBox)findViewById(R.id.checkBox);
        correct_1=(ImageView)findViewById(R.id.achievement1);
        answered_1=(ImageView)findViewById(R.id.achievement2);
        hscore_1=(ImageView)findViewById(R.id.hscore_achievement_1);
        hscore_2=(ImageView)findViewById(R.id.hscore_achievement_2);

        Button save=(Button)findViewById(R.id.button);
        assert save != null;
        save.setOnClickListener(this);

        bars=new ProgressBar[4];
        achievement_txt=new TextView[4];
        bars[0]=(ProgressBar)findViewById(R.id.progressBar);
        bars[1]=(ProgressBar)findViewById(R.id.progressBar2);
        bars[2]=(ProgressBar)findViewById(R.id.progressBar3);
        bars[3]=(ProgressBar)findViewById(R.id.progressBar4);

        achievement_txt[0]=(TextView)findViewById(R.id.textView_correct);
        achievement_txt[1]=(TextView)findViewById(R.id.textView_answered);
        achievement_txt[2]=(TextView)findViewById(R.id.textView_hscore_1);
        achievement_txt[3]=(TextView)findViewById(R.id.textView_hscore_2);

        userLocalStore=new User_details_store(this);
        loggedIn=userLocalStore.loggedInUser();

        email.setText(loggedIn.email);
        pass.setText(loggedIn.password);

        TabHost tabs=(TabHost)findViewById(R.id.tabHost);

        assert tabs != null;
        tabs.setup();

        TabHost.TabSpec tabspec=tabs.newTabSpec("Profile");
        tabspec.setContent(R.id.tab1);
        tabspec.setIndicator("Profile");
        tabs.addTab(tabspec);

        tabspec=tabs.newTabSpec("Achievements");
        tabspec.setContent(R.id.tab2);
        tabspec.setIndicator("Achievements");
        tabs.addTab(tabspec);

        setProfile();
        checkAchieved();
    }

    private void setProfile() {

        int hscore=loggedIn.highscore;
        float total_answered=loggedIn.total_answered;
        float total_correct=loggedIn.total_correct;
        hscore_txt.setText("Highscore: "+hscore);
        float percent =0;
        if(total_answered >0 && total_correct>0){
            percent = (total_correct * 100.0f) / total_answered;
        }
        perctent_txt.setText("Correct answer %: "+percent);
        showpwd.setOnClickListener(this);

    }

    private void checkAchieved() {
        int hscore=loggedIn.highscore;
        int total_answered=loggedIn.total_answered;
        int total_correct=loggedIn.total_correct;

        bars[0].setMax(250);
        bars[0].setProgress(total_correct);
        achievement_txt[0].setText("correct answers " + total_correct + "/250");
        bars[1].setMax(500);
        bars[1].setProgress(total_answered);
        achievement_txt[1].setText("total answered " + total_answered + "/500");
        bars[2].setMax(50);
        bars[2].setProgress(hscore);
        achievement_txt[2].setText("high score " + hscore + "/50");
        bars[3].setMax(100);
        bars[3].setProgress(hscore);
        achievement_txt[3].setText("high score " + hscore + "/100");

        if(hscore>=50){
            hscore_1.setImageResource(R.drawable.highscore_temp);
        }
        if(hscore>=100){
            hscore_2.setImageResource(R.drawable.highscore_temp_2);
        }
        if(total_correct>=250){
            correct_1.setImageResource(R.drawable.achievement_template);
        }
        if(total_answered>=500){
            answered_1.setImageResource(R.drawable.achievement_template_2);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkBox:
                if(showpwd.isChecked()){
                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                }
                else{
                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD  | InputType.TYPE_CLASS_TEXT );
                }
                break;

            case R.id.button:
                checkProfile();
                break;
        }
    }

    private void checkProfile() {

        String curr_email=loggedIn.email;
        String curr_password=loggedIn.password;

        String new_email=email.getText().toString();
        String new_pass=pass.getText().toString();

        if(curr_email.equals(new_email) && curr_password.equals(new_pass)){
            Toast.makeText(Profile.this, "No Changes made", Toast.LENGTH_SHORT).show();
        }
        else{
            updateProfile(new_email,new_pass);
        }



    }

    private void updateProfile(String new_email, String new_password) {

        final String email=new_email;
        final String password=new_password;

        String curr_email=loggedIn.email;
        String curr_password=loggedIn.password;

        DBConnect connect=new DBConnect(this);
        connect.setProfile(password,email,loggedIn.username,curr_email,curr_password, new AsyncResponse<String>() {
                    @Override
                    public void processFinish(String output) {

                        if(output.contains("updated")){
                            Toast.makeText(Profile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            User_details current=userLocalStore.loggedInUser();
                            userLocalStore.updateStats(current,email,password);
                            finish();
                        }
                        else{
                            Toast.makeText(Profile.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


    }
}
