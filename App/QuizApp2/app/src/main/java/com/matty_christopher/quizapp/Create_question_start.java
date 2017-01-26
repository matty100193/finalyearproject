package com.matty_christopher.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Create_question_start extends AppCompatActivity implements View.OnClickListener {

    public static AutoCompleteTextView qname;
    public static EditText qpwd;
    private ArrayList<String> allQuizzes;
    private ArrayList<String> pwds;
    private ArrayList<Integer> ids;
    private ArrayList<String> creators;
    private Quiz_details_store quizLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question_start);

        qname=(AutoCompleteTextView)findViewById(R.id.quiz_name_check);
        qpwd=(EditText)findViewById(R.id.quiz_password_check);
        TextView viewQuizzes=(TextView)findViewById(R.id.txt_btn);

        Button submit = (Button) findViewById(R.id.qstart_choose_quiz_btn);
        assert submit != null;
        submit.setOnClickListener(this);
        assert viewQuizzes != null;
        viewQuizzes.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        qname.setText("");
        qpwd.setText("");
        quizLocalStore=new Quiz_details_store(this);
        getQuizzes();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qstart_choose_quiz_btn:
                checkEmpty();
                break;

            case R.id.txt_btn:
                showQuizzes();
                break;

        }
    }

    private void showQuizzes() {

        Intent intent=new Intent(Create_question_start.this,Show_User_Quizzes.class);
        intent.putStringArrayListExtra("qnames", allQuizzes);
        intent.putStringArrayListExtra("qpasswords", pwds);
        intent.putIntegerArrayListExtra("qid", ids);
        intent.putStringArrayListExtra("creators", creators);
        startActivity(intent);

    }

    private void getQuizzes(){

        DBConnect_create_quiz dbConnect_create_quiz=new DBConnect_create_quiz(this);
        dbConnect_create_quiz.checkQuizDetails(new AsyncResponse<ArrayList<Quiz_details>>() {
            @Override
            public void processFinish(ArrayList<Quiz_details> output) {

                if (output.size() > 0) {
                    fill(output);
                }

            }
        });

    }

    private void fill(ArrayList<Quiz_details> output) {

        allQuizzes=new ArrayList<>();
        pwds=new ArrayList<>();
        ids=new ArrayList<>();
        creators=new ArrayList<>();

        for(Quiz_details current:output){
            allQuizzes.add(current.qname);
            pwds.add(current.pwd);
            ids.add(current.quiz_id);
            creators.add(current.creator);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice, allQuizzes);
        qname.setThreshold(1);
        qname.setAdapter(adapter);
        qname.setValidator(new Validator());

    }

    private void checkEmpty() {
        qname.performValidation();
        String name=qname.getText().toString();
        String pass=qpwd.getText().toString();

        if(name.equals("")||pass.equals("")){
            Toast.makeText(getApplicationContext(), "Please Fill in both fields",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            if(checkValid(name,pass)){
                Intent intent1=new Intent(Create_question_start.this, Create_question_mode_option.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent1);
            }
            else{
                Toast.makeText(getApplicationContext(), "Quiz name or password are incorrect!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkValid(String name, String pass) {
        for(int i=0;i<allQuizzes.size();i++){
            if(allQuizzes.get(i).equals(name)){
                if(pwds.get(i).equals(pass)){
                    int id=ids.get(i);
                    Quiz_details toStore=new Quiz_details(id);
                    quizLocalStore.storeData(toStore);
                    return true;
                }
            }
        }
        return false;
    }


    private class Validator implements AutoCompleteTextView.Validator {

        @Override
        public boolean isValid(CharSequence text) {

            return allQuizzes.contains(text.toString());

        }

        @Override
        public CharSequence fixText(CharSequence invalidText) {
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Create_homepage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        //finish();
    }

}
