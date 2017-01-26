package com.matty_christopher.quizapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Question_template extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private TextView question,howmany;
    private Button[] choices;
    private int user_correct;
    private Button next;
    private Button stats;
    private Button submit;
    private ArrayList<Question_details> list;

    private ArrayList<Float> all_ratings;
    private ArrayList<Float> user_ratings;
    private int userChoice;

    private ImageView[] lives;
    private int userlives;
    private String mode;
    private int type;

    private RatingBar ratingbar;

    private int pos;
    public static ArrayList<ArrayList<Integer>> userstats;
    public static Question_details current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_template);

        submit = (Button) findViewById(R.id.save_question_btn);
        question=(TextView)findViewById(R.id.question_text_edit);
        howmany=(TextView)findViewById(R.id.position);
        choices = new Button[4];
        choices[0]=(Button)findViewById(R.id.answer1);
        choices[1]=(Button)findViewById(R.id.answer2);
        choices[2]=(Button)findViewById(R.id.answer3);
        choices[3]=(Button)findViewById(R.id.answer4);

        ratingbar=(RatingBar)findViewById(R.id.ratingBar);
        assert ratingbar != null;
        ratingbar.setOnRatingBarChangeListener(this);

        lives=new ImageView[2];
        lives[0]=(ImageView)findViewById(R.id.live1);
        lives[1]=(ImageView)findViewById(R.id.live2);

        lives[0].setVisibility(View.INVISIBLE);
        lives[1].setVisibility(View.INVISIBLE);

        next=(Button)findViewById(R.id.next);
        stats=(Button)findViewById(R.id.stats);
        submit.setOnClickListener(this);

//        for (Button choice : choices) {
//            choice.setOnClickListener(this);
//        }
        next.setOnClickListener(this);
        stats.setOnClickListener(this);
       // list=new ArrayList<>();
       // setUp();

    }

    @Override
    protected void onStart() {
        super.onStart();
        list=new ArrayList<>();
        for (Button choice : choices) {
            choice.setOnClickListener(this);
        }
        next.setVisibility(View.INVISIBLE);
        stats.setVisibility(View.INVISIBLE);
        ratingbar.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.VISIBLE);
        lives[0].setVisibility(View.INVISIBLE);
        lives[1].setVisibility(View.INVISIBLE);
        next.setText("Next");
        setUp();
    }

    private void setUp() {
        userstats=new ArrayList<>();
        all_ratings=new ArrayList<>();
        user_ratings=new ArrayList<>();
        Drawable progress = ratingbar.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.rgb(218, 165, 32));

        ratingbar.setVisibility(View.INVISIBLE);
        pos=0;
        user_correct=0;
        userChoice=-1;
        mode="normal";
        type=-1;
        Intent intent=getIntent();
        if(intent.getExtras()!=null) {
            if (intent.getExtras().containsKey("mode")) {
                mode = intent.getStringExtra("mode");
            }
            if (intent.getExtras().containsKey("h2h_id")) {
                type = intent.getIntExtra("h2h_id",-1);
            }
        }

        if(mode.equals("highscore")){
            lives[0].setVisibility(View.VISIBLE);
            lives[1].setVisibility(View.VISIBLE);
            userlives=2;
        }

        Intent mIntent = getIntent();
        list = mIntent.getParcelableArrayListExtra("Questions_list");

        for(int i=0;i<list.size();i++){
            handleRatings(i);
        }

        currentQuestion();
    }

    private void handleRatings(int curr) {
        int tot=list.get(curr).tot_score;
        int todivide=list.get(curr).avg;

        if(tot==0 || todivide==0){
            all_ratings.add((float)0);
            user_ratings.add((float)0);
        }
        else{
            float avg=tot/todivide;
            float halved=avg/2;
            all_ratings.add(halved);
            user_ratings.add(halved);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.answer1:
                selectButton(0);
                break;

            case R.id.answer2:
                selectButton(1);
                break;

            case R.id.answer3:
                selectButton(2);
                break;

            case R.id.answer4:
                selectButton(3);
                break;

            case R.id.save_question_btn:

                if(userChoice==-1){
                    Toast.makeText(Question_template.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                }
                else{
                    handleAnswer();
                    for(Button b: choices){
                        b.setClickable(false);
                    }
                    submit.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.VISIBLE);
                    stats.setVisibility(View.VISIBLE);
                    ratingbar.setVisibility(View.VISIBLE);
                    ratingbar.setRating(all_ratings.get(pos));
                }
                break;

            case R.id.next:
                  pos++;

                 if(mode.equals("highscore") && userlives==0){
                     Toast.makeText(Question_template.this, "You scored: "+user_correct, Toast.LENGTH_SHORT).show();
                     int finalpos=pos;
                     uploadStats(finalpos);
                 }

                  if(pos>=list.size()-1){
                      next.setText("Finish");
                  }
                  if(currentQuestion()){
                      submit.setVisibility(View.VISIBLE);
                      next.setVisibility(View.INVISIBLE);
                      stats.setVisibility(View.INVISIBLE);
                      ratingbar.setVisibility(View.INVISIBLE);
                      for(Button b: choices){
                          b.setClickable(true);
                      }
                  }
                  else{
                      Toast.makeText(Question_template.this, "You scored: "+user_correct, Toast.LENGTH_SHORT).show();
                      int finalpos=pos;


                      uploadStats(finalpos);
                  }
                break;

            case R.id.stats:
                setUpPopup();
                break;

        }

    }


    private void uploadStats(int finalpos) {

        ArrayList<String>ids=new ArrayList<>();
        for(int i=0;i<finalpos;i++){
           ids.add(list.get(i).qid);
        }
        DBConnect_play connect=new DBConnect_play(this);
//        connect.uploadQuestionStats(ids, userstats,user_ratings, new Db_response<String>() {
//            @Override
//            public void processFinish(String output) {
//
//                finishQuiz();
//
//            }
//        });
        connect.uploadQuestionStats(ids, userstats, user_ratings);


        finishQuiz();



    }


    private void finishQuiz() {

        User_details_store userLocalStore = new User_details_store(this);
        final User_details currentUser= userLocalStore.loggedInUser();

        Log.e("tag",""+type);
        if(type!=-1){
            uploadH2HData(currentUser);
        }


        int isHscore=0;
        int h_score=0;
        int total_ans=pos;
        int tot_correct=user_correct;
        if(mode.equals("highscore")){
            isHscore=1;
            h_score=tot_correct;

            if(h_score>currentUser.highscore){
                Toast.makeText(Question_template.this, "New Highscore", Toast.LENGTH_SHORT).show();
//                new ParticleSystem(this, 80, R.drawable.confeti2, 10000)
//                        .setSpeedModuleAndAngleRange(0f, 0.3f, 180, 180)
//                        .setRotationSpeed(144)
//                        .setAcceleration(0.00005f, 90)
//                        .emit(findViewById(R.id.emiter_top_right), 8);
//
//                new ParticleSystem(this, 80, R.drawable.confeti3, 10000)
//                        .setSpeedModuleAndAngleRange(0f, 0.3f, 0, 0)
//                        .setRotationSpeed(144)
//                        .setAcceleration(0.00005f, 90)
//                        .emit(findViewById(R.id.emiter_top_left), 8);
            }


        }

        userLocalStore.updateStats(currentUser, h_score, total_ans, tot_correct);

        DBConnect_play connect=new DBConnect_play(this);
        connect.uploadUserStats(currentUser.username, h_score, total_ans, tot_correct, isHscore, new Db_response<String>() {
            @Override
            public void processFinish(String output) {
                Intent intent = new Intent(Question_template.this, homepage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                userstats = null;
                current = null;
                //finish();
            }
        });

    }

    private void uploadH2HData(User_details currentUser) {
        DB_connect_head2head connect=new DB_connect_head2head(this);
        connect.updateMatch(currentUser.username, user_correct, type);

    }

    private void setUpPopup() {

        Intent intent=new Intent(Question_template.this,Stats.class);
        intent.putExtra("position", pos);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

    }

    private void deselectButtons(){
        for(Button b: choices){
            b.setBackgroundResource(R.drawable.button_template);
        }
    }

    private void selectButton(int choice){
        deselectButtons();
        choices[choice].setBackgroundResource(R.drawable.chosen_template);
        userChoice=choice+1;
    }

    private void handleAnswer(){

        int user=userChoice-1;
        int correct=current.correct-1;

        ArrayList<Integer> sublist=new ArrayList<>();
        for (int i=0;i<4;i++){
            sublist.add(0);
        }
        sublist.set(user, 1);
        userstats.add(sublist);

        if(correct==user){
            choices[user].setBackgroundResource(R.drawable.button_template_correct);
            user_correct++;
        }
        else{
            choices[user].setBackgroundResource(R.drawable.button_template_wrong);
            choices[correct].setBackgroundResource(R.drawable.button_template_correct);

            if(mode.equals("highscore")&&userlives>0){
                userlives--;
                lives[userlives].setVisibility(View.INVISIBLE);
                if(userlives==0){
                    next.setText("Finish");
                }
            }

        }

    }

    private boolean currentQuestion(){
            userChoice=-1;
            deselectButtons();
            if (list.size() > 0 && pos < list.size()) {
                howmany.setText((pos + 1) + "/" + list.size());
                current = list.get(pos);
                question.setText(current.question);
                for (int n = 0; n < choices.length; n++) {
                    choices[n].setText(current.answers[n]);
                }
                return true;
            }
        return false;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        user_ratings.set(pos, rating * 2);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }



}
