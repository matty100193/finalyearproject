package com.matty_christopher.quizapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Show_User_Quizzes extends AppCompatActivity implements View.OnClickListener {
    private TableLayout showQuizzes;
    private int current_selected;
    private ArrayList<Integer> ids;
    private ArrayList<String> qnames;
    private ArrayList<String> passwords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__user__quizzes);

        Button edit = (Button) findViewById(R.id.selectQuiz);
        assert edit != null;
        edit.setOnClickListener(this);
        showQuizzes = (TableLayout) findViewById(R.id.showQuizzes);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.85), (int) (height * 0.8));

    }

    @Override
    protected void onStart() {
        super.onStart();
        current_selected=-1;
        while (showQuizzes.getChildCount() > 1)
            showQuizzes.removeView(showQuizzes.getChildAt(showQuizzes.getChildCount() - 1));
        Intent i = getIntent();
        ArrayList<String> qnames=i.getStringArrayListExtra("qnames");
        ArrayList<String> passwords=i.getStringArrayListExtra("qpasswords");
        ArrayList<String> creators=i.getStringArrayListExtra("creators");
        ArrayList<Integer> ids=i.getIntegerArrayListExtra("qid");

        if(ids.size()>0){
            fillTable(ids,qnames,passwords,creators);
        }

    }

    private void fillTable(final ArrayList<Integer> old_ids,ArrayList<String> old_qnames, ArrayList<String> old_passwords,ArrayList<String> creators) {

         ids=new ArrayList<>();
         qnames=new ArrayList<>();
         passwords=new ArrayList<>();

        User_details_store user_details_store=new User_details_store(this);
        User_details current_user=user_details_store.loggedInUser();

        for(int i=0;i<old_ids.size();i++){
            //Log.e("tag", "" + creators.get(i));
            if(creators.get(i).equals(current_user.username)){
                ids.add(old_ids.get(i));
                qnames.add(old_qnames.get(i));
                passwords.add(old_passwords.get(i));
            }
        }

        final ArrayList<RadioButton>groupRadioButtonList=new ArrayList<>();


        for(int i=0;i<qnames.size();i++){

            final TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            if((i%2)==0){
                row.setBackgroundColor(Color.rgb(182, 236, 255));
            }
            final int qid=ids.get(i);

            final TextView qname = new TextView(this);
            qname.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            qname.setTextColor(Color.BLACK);
            qname.setWidth(100);
            qname.setHeight(100);
            qname.setGravity(Gravity.LEFT);
            qname.setText("" + qnames.get(i));
            row.addView(qname);


            TextView pass = new TextView(this);
            pass.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            pass.setTextColor(Color.BLACK);
            pass.setWidth(100);
            pass.setHeight(100);
            pass.setGravity(Gravity.CENTER);
            pass.setText("" + passwords.get(i));
            row.addView(pass);
            row.setMinimumHeight(100);

            RadioButton radioButton=new RadioButton(this);
            radioButton.setId(i);
            row.addView(radioButton);
            groupRadioButtonList.add(radioButton);


            //modified from http://stackoverflow.com/questions/22208468/radio-buttons-all-selectable-in-radio-group-after-adding-them-dynamically-android
            //author - Kristy Welsh used to uncheck radio buttons and check selected one
            radioButton.setOnClickListener(new RadioButton.OnClickListener() {
                public void onClick(View v) {
                    int id = v.getId();
                    for (RadioButton button : groupRadioButtonList) {
                        if (id == button.getId()) {
                            button.setChecked(true);
                            current_selected = qid;

                        } else {
                            button.setChecked(false);
                        }
                    }
                }
            });
            row.setMinimumHeight(120);
            showQuizzes.addView(row);
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.selectQuiz:

                if(current_selected==-1){
                    Toast.makeText(getApplicationContext(), "No Quiz has been selected!", Toast.LENGTH_SHORT).show();
                }
                else{
                    String curr_name="";
                    String curr_pass="";
                    for(int i=0;i<ids.size();i++){
                        if(current_selected==ids.get(i)){
                            curr_name=qnames.get(i);
                            curr_pass=passwords.get(i);
                            break;
                        }
                    }
                    Create_question_start.qname.setText(curr_name);
                    Create_question_start.qpwd.setText(curr_pass);
                    finish();
                }

                break;

        }
    }
}
