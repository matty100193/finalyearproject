package com.matty_christopher.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class homepage extends AppCompatActivity implements View.OnClickListener {

    private User_details_store userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button create = (Button) findViewById(R.id.hm_createQuiz_btn);
        Button play = (Button) findViewById(R.id.hm_play_btn);
        Button leaderboard = (Button) findViewById(R.id.hm_leaderboard_btn);

        ImageView logout=(ImageView)findViewById(R.id.logout_img);
        assert logout != null;
        logout.setOnClickListener(this);

        ImageView profile=(ImageView)findViewById(R.id.profile_img);
        assert profile != null;
        profile.setOnClickListener(this);

        assert create != null;
        create.setOnClickListener(this);
        assert play != null;
        play.setOnClickListener(this);
        assert leaderboard != null;
        leaderboard.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FontTextView username_text = (FontTextView) findViewById(R.id.username_text);
        userLocalStore = new User_details_store(this);
        User_details currentUser= userLocalStore.loggedInUser();
        assert username_text != null;
        username_text.setText(currentUser.username);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.hm_createQuiz_btn:
                Intent intent=new Intent(homepage.this,Create_homepage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;

            case R.id.hm_play_btn:
                Intent intent1=new Intent(homepage.this,Choose_mode.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent1);
                break;

            case R.id.hm_leaderboard_btn:
                Intent intent2=new Intent(homepage.this,leaderboard.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent2);
                break;

            case R.id.logout_img:
                userLocalStore.logoutUser();
                Intent intent3 = new Intent(getApplicationContext(), login.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent3);
                break;

            case R.id.profile_img:
                startActivity(new Intent(homepage.this,Profile.class));
                break;


        }


    }

    @Override
    public void onBackPressed() {
    }
}
