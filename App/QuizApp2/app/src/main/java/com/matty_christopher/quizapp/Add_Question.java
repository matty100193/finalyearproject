package com.matty_christopher.quizapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class Add_Question extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText question;
    private EditText answer1;
    private EditText answer2;
    private EditText answer3;
    private EditText answer4;
    private RadioGroup correct_choice;
    private User_details currentUser;
    private ArrayList<String> usersList;
    private Spinner user1;
    private Spinner user2;
    private Spinner user3;
    private Spinner user4;
    private String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__question);


        question = (EditText) findViewById(R.id.question_text_edit);
        answer1 = (EditText) findViewById(R.id.answer1_edittext);
        answer2 = (EditText) findViewById(R.id.answer2_edittext);
        answer3 = (EditText) findViewById(R.id.answer3_edittext);
        answer4 = (EditText) findViewById(R.id.answer4_edittext);
        correct_choice=(RadioGroup) findViewById(R.id.radio_group_question_correct);

         user1 = (Spinner) findViewById(R.id.spinner);
         user2 = (Spinner) findViewById(R.id.spinner2);
         user3 = (Spinner) findViewById(R.id.spinner3);
         user4 = (Spinner) findViewById(R.id.spinner4);

        user1.setOnItemSelectedListener(this);
        user2.setOnItemSelectedListener(this);
        user3.setOnItemSelectedListener(this);
        user4.setOnItemSelectedListener(this);


        User_details_store userLocalStore = new User_details_store(this);
        currentUser= userLocalStore.loggedInUser();
        usersList = new ArrayList<>();
        usersList.add(currentUser.username);

        Intent intent = getIntent();
        activity = "";

        if(intent.getExtras()!=null) {
            if (intent.getExtras().containsKey("activity")) {
                 activity = intent.getStringExtra("activity");
            }
        }
        if(activity.equals("group")) {

            Group_details_store groupLocalStore = new Group_details_store(this);
            Group_details currentGroup = groupLocalStore.getGroup();

            if (currentGroup.storedUsers != null) {
                Set<String> groupusers = currentGroup.storedUsers;

                for (String currentuser : groupusers) {
                    usersList.add(currentuser);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, usersList);
        user1.setAdapter(adapter);
        user2.setAdapter(adapter);
        user3.setAdapter(adapter);
        user4.setAdapter(adapter);

        Button save = (Button) findViewById(R.id.save_question_btn);
        assert save != null;
        save.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.save_question_btn:
                authenticate();
                break;
        }

    }

    private void authenticate(){

        String quest=question.getText().toString();
        String ans1=answer1.getText().toString();
        String ans2=answer2.getText().toString();
        String ans3=answer3.getText().toString();
        String ans4=answer4.getText().toString();

        ArrayList<String> answers=new ArrayList<>();
        answers.add(ans1);
        answers.add(ans2);
        answers.add(ans3);
        answers.add(ans4);

        ArrayList<String> users=new ArrayList<>();
        users.add(currentUser.username);
        users.add(user1.getSelectedItem().toString());
        users.add(user2.getSelectedItem().toString());
        users.add(user3.getSelectedItem().toString());
        users.add(user4.getSelectedItem().toString());

        Quiz_details_store quizStore = new Quiz_details_store(this);

        Quiz_details currentquiz= quizStore.currentQuiz();
        int value=currentquiz.quiz_id;

        int id=correct_choice.getCheckedRadioButtonId();
        int getChoice=-1;
        if(id==R.id.radioButton3){
            getChoice=1;
        }
        else if(id==R.id.radioButton4){
            getChoice=2;
        }
        else if(id==R.id.radioButton5){
            getChoice=3;
        }
        else if(id==R.id.radioButton6){
            getChoice=4;
        }

        if((ans1.equals("") || ans2.equals("") || ans3.equals("")|| ans4.equals("")|| quest.equals("")) && !activity.equals("group")){
            Toast.makeText(getApplicationContext(), "Please Fill in all fields",
                    Toast.LENGTH_SHORT).show();
        }
        else if(activity.equals("group") && quest.equals("")){
            Toast.makeText(getApplicationContext(), "Please fill in a question",
                    Toast.LENGTH_SHORT).show();
        }
        else if(activity.equals("group")&& getChoice!=-1 && answers.get(getChoice-1).equals("")){
            Toast.makeText(getApplicationContext(), "Please add the correct answer",
                    Toast.LENGTH_SHORT).show();
        }
        else if(getChoice==-1){
            Toast.makeText(getApplicationContext(), "Please add the correct choice",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            if(activity.equals("group")){
                submitSingleUser(quest, getChoice, answers, users, value,0);
            }
            else{
                submitSingleUser(quest, getChoice, answers, users, value,1);
            }
        }


    }

    private void submitSingleUser(String quest,int correctAnswer,ArrayList<String> answers,ArrayList<String> users,int quizid,int complete){

        DBConnect_question connect=new DBConnect_question(this);
        connect.setUpQuestion(quizid, quest, users, answers, complete, correctAnswer, new Db_response<String>() {
            @Override
            public void processFinish(String output) {
                if (output.contains("fail")) {
                    Toast.makeText(getApplicationContext(), output,
                            Toast.LENGTH_SHORT).show();
                } else if (output.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Could not create question!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Question created!",
                            Toast.LENGTH_SHORT).show();
                    if (activity.equals("group")) {
                        dealWithGroup(output);
                    } else {
                        finished();
                    }
                }
            }
        });

    }


    private void dealWithGroup(String output){
        DBConnect_groups dbconnect=new DBConnect_groups(this);
        dbconnect.addGroup(output, usersList, new Db_response<String>() {
            @Override
            public void processFinish(String output) {
                finished();
            }
        });
    }

    private void finished(){
        Intent intent = new Intent(getApplicationContext(), homepage.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
    }

}
