package com.matty_christopher.quizapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Play_choose_quiz extends AppCompatActivity implements View.OnClickListener {

    private String current_quiz;
    private TableLayout showGroups;
    private EditText searchbox;
    private ArrayList<ArrayList> research;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_choose_quiz);

        Button edit = (Button) findViewById(R.id.submit);
        assert edit != null;
        edit.setOnClickListener(this);
        showGroups = (TableLayout) findViewById(R.id.group_table);

        searchbox=(EditText)findViewById(R.id.searchBox);
        ImageButton searchicon=(ImageButton)findViewById(R.id.imageView);
        assert searchicon != null;
        searchicon.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        current_quiz="";
        research=new ArrayList<>();
        while (showGroups.getChildCount() > 1)
            showGroups.removeView(showGroups.getChildAt(showGroups.getChildCount() - 1));

        DBConnect_play connect=new DBConnect_play(this);

        connect.getQuizzes(new AsyncResponse<ArrayList<ArrayList>>() {
            @Override
            public void processFinish(ArrayList<ArrayList> output) {
                research = output;
                if (output.size() > 0) {
                    fillTable(output);
                } else {
                    Toast.makeText(Play_choose_quiz.this, "Error connecting to server", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), homepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.submit:

                if(current_quiz.equals("")){
                    Toast.makeText(getApplicationContext(), "No quiz has been selected!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent edit_selected = new Intent(Play_choose_quiz.this, Play_ready_panel.class);
                    edit_selected.putExtra("quiz_name", current_quiz);
                    edit_selected.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(edit_selected);
                }

                break;

            case R.id.imageView:
                String toSearch=searchbox.getText().toString();
                if(showGroups.getChildCount() >= 1 && !toSearch.equals("")){
                    if(research!=null){
                        refillTable();
                    }
                    searchTable(toSearch);
                }
                else if(toSearch.equals("")){
                    Toast.makeText(Play_choose_quiz.this, "Rows reset", Toast.LENGTH_SHORT).show();
                    if(research!=null){
                        refillTable();
                    }
                }

                break;

        }

    }

    private void refillTable(){
        while (showGroups.getChildCount() > 1)
            showGroups.removeView(showGroups.getChildAt(showGroups.getChildCount() - 1));
        fillTable(research);
    }

    private void searchTable(String toSearch) {

        int count=0;

        for(int i=1;i<showGroups.getChildCount();i++){
            TableRow row = (TableRow) showGroups.getChildAt(i);

            TextView txt_creator = (TextView) row.getChildAt(1);
            String current_creator=txt_creator.getText().toString();

            TextView qnm = (TextView) row.getChildAt(0);
            String current_question=qnm.getText().toString();


            if(current_creator.toLowerCase().contains(toSearch.toLowerCase()) || current_question.toLowerCase().contains(toSearch.toLowerCase())){
                count++;
                if((i%2)==1){
                    row.setBackgroundColor(Color.rgb(182, 236, 255));
                }
                else{
                    row.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            else{
                if(current_question.equals(current_quiz)){
                    current_quiz="";
                }
                showGroups.removeView(showGroups.getChildAt(i));
                i--;
            }

        }
        Toast.makeText(Play_choose_quiz.this, "found: "+count+" possible matches", Toast.LENGTH_SHORT).show();
    }

    private void fillTable(ArrayList<ArrayList> columns){

        ArrayList<Object> quiz_names=new ArrayList<>();
        ArrayList<Object> no_questions=new ArrayList<>();
        ArrayList<Object> creators=new ArrayList<>();
        if(columns.size()>0) {
            for (int i = 0; i < columns.get(0).size(); i++) {
                quiz_names.add(columns.get(0).get(i));
                no_questions.add(columns.get(1).get(i));
                creators.add(columns.get(2).get(i));
            }
        }

        final ArrayList<RadioButton>groupRadioButtonList=new ArrayList<>();

        for(int i=0;i<quiz_names.size();i++){

            final TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            if((i%2)==0){
                row.setBackgroundColor(Color.rgb(182, 236, 255));
            }



            final TextView qnm = new TextView(this);
            qnm.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            qnm.setTextColor(Color.BLACK);
            qnm.setWidth(100);
            qnm.setGravity(Gravity.CENTER);
            qnm.setText("" + quiz_names.get(i));
            qnm.setHeight(200);
            row.addView(qnm);


            TextView creator = new TextView(this);
            creator.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            creator.setTextColor(Color.BLACK);
            creator.setWidth(100);
            creator.setHeight(200);
            creator.setGravity(Gravity.CENTER);
            creator.setText("" + creators.get(i));
            row.addView(creator);
            row.setMinimumHeight(200);


            TextView question = new TextView(this);
            question.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            question.setTextColor(Color.BLACK);
            question.setWidth(100);
            question.setHeight(150);
            question.setGravity(Gravity.CENTER);
            question.setText("" + no_questions.get(i));
            row.addView(question);
            row.setMinimumHeight(150);


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
                            current_quiz = qnm.getText().toString();

                        } else {
                            button.setChecked(false);
                        }
                    }
                }
            });
            row.setMinimumHeight(200);

            showGroups.addView(row);

        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Choose_mode.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        //finish();
    }
}
