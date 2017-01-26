package com.matty_christopher.quizapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Edit_question extends AppCompatActivity implements View.OnClickListener {

    private EditText question;
    private EditText[] answer;
    private TextView user1;
    private TextView user2;
    private TextView user3;
    private TextView user4;
    private RadioGroup correct_choice;
    private String questionid;
    private User_details currentUser;
    private ContentValues currentQuestion;
    private String ori_quest;
    private String [] ori_ans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        answer=new EditText[4];
        question=(EditText)findViewById(R.id.question_text_edit);
        answer[0]=(EditText)findViewById(R.id.answer1_edittext);
        answer[1]=(EditText)findViewById(R.id.answer2_edittext);
        answer[2]=(EditText)findViewById(R.id.answer3_edittext);
        answer[3]=(EditText)findViewById(R.id.answer4_edittext);

        user1 = (TextView) findViewById(R.id.user_a1);
        user2 = (TextView) findViewById(R.id.user_a2);
        user3 = (TextView) findViewById(R.id.user_a3);
        user4 = (TextView) findViewById(R.id.user_a4);

        correct_choice=(RadioGroup) findViewById(R.id.radio_group_question_correct);

        Button submit = (Button) findViewById(R.id.save_question_btn);
        assert submit != null;
        submit.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        User_details_store userLocalStore = new User_details_store(this);
        currentUser= userLocalStore.loggedInUser();
        int selected_group = getIntent().getIntExtra("selected_group", -1);
        DBConnect_groups connect=new DBConnect_groups(this);
        connect.retreiveSelectedQuestion(selected_group, new AsyncResponse<ContentValues>() {
            @Override
            public void processFinish(ContentValues output) {
                if (output.size() == 14) {
                    fillForm(output);
                } else {
                    Toast.makeText(Edit_question.this, "Error connecting to server, please try again!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), homepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.save_question_btn:
                if (currentQuestion.size()>0){
                    update();
                    }
            break;
        }
    }

    private void update() {

        String curr_quest=question.getText().toString();


        if(!ori_quest.equals(curr_quest) && !curr_quest.equals("")){
            if(currentUser.username.equals(currentQuestion.get("creator_question"))) {
                DBConnect_question connect=new DBConnect_question(this);
                connect.updateQuestion(questionid,curr_quest,5, new Db_response<String> () {
                    @Override
                    public void processFinish(String output) {
                        Toast.makeText(Edit_question.this, output, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        for(int i=0;i<answer.length;i++){
            String curr=answer[i].getText().toString();
             if(!ori_ans[i].equals(curr)&& !curr.equals("")){
                 int j=i+1;
                 if(currentUser.username.equals(currentQuestion.get("creator_ans"+j))) {
                     DBConnect_question connect=new DBConnect_question(this);
                     connect.updateQuestion(questionid,curr,j, new Db_response<String> () {
                         @Override
                         public void processFinish(String output) {
                             Toast.makeText(Edit_question.this, output, Toast.LENGTH_SHORT).show();
                         }
                     });
                 }
             }

        }

        checkToAdd(curr_quest);

    }



    private void checkToAdd(String curr_quest) {
        if(currentUser.username.equals(currentQuestion.get("creator_question")) && !curr_quest.equals("") && !answersisEmpty()) {
            AlertDialog.Builder message = new AlertDialog.Builder(this);
            message.setMessage("Is the question ready to be added?");
            message.setCancelable(false);

            message.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DBConnect_question complete=new DBConnect_question(Edit_question.this);
                    complete.completeQuestion(questionid, new Db_response<String>() {
                        @Override
                        public void processFinish(String output) {
                            Intent intent = new Intent(Edit_question.this, homepage.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Edit_question.this.finish();
                        }
                    });
                }
            });

            message.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Edit_question.this, "This question can still be edited", Toast.LENGTH_SHORT).show();
                }
            });

            message.show();

        }
    }

    private boolean answersisEmpty() {
        for (EditText anAnswer : answer) {
            String curr = anAnswer.getText().toString();
            if (curr.equals("")) {
                return true;
            }
        }

        return false;
    }


    private void fillForm(ContentValues data){
        currentQuestion=data;
        ori_ans=new String[4];


        questionid=data.get("questionid").toString();

        question.setText(data.get("question").toString());
        ori_quest=question.getText().toString();
        if(!currentUser.username.equals(data.get("creator_question"))){
            question.setEnabled(false);
            question.setTextColor(Color.BLACK);
        }
        else{
            question.setEnabled(true);
            question.setTextColor(Color.BLACK);
        }


        for(int i=0;i<answer.length;i++){
            int j=i+1;
            answer[i].setText(data.get("answer"+j).toString());
            ori_ans[i]=answer[i].getText().toString();
            if(!currentUser.username.equals(data.get("creator_ans"+j))){
                answer[i].setEnabled(false);
                answer[i].setTextColor(Color.BLACK);
            }
            else{
                answer[i].setEnabled(true);
                answer[i].setTextColor(Color.BLACK);
            }
        }

        user1.setText(data.get("creator_ans1").toString());
        user2.setText(data.get("creator_ans2").toString());
        user3.setText(data.get("creator_ans3").toString());
        user4.setText(data.get("creator_ans4").toString());

        correct_choice.setEnabled(false);
        int id=-1;
        int selected=data.get("correct_answer").hashCode();
        if(selected==1){
            id=R.id.radioButton3;
        }
        else if(selected==2){
            id=R.id.radioButton4;
        }
        else if(selected==3){
            id=R.id.radioButton5;
        }
        else if(selected==4){
            id=R.id.radioButton6;
        }

        if(id!=-1) {
            correct_choice.check(id);
        }

    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), View_Groups.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

}
