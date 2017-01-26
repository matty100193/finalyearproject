package com.matty_christopher.quizapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;


public class View_Groups extends AppCompatActivity implements View.OnClickListener {

    private int current_groupid;
    private TableLayout showGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__groups);

        Button edit = (Button) findViewById(R.id.Edit_group_question);
        assert edit != null;
        edit.setOnClickListener(this);
        showGroups = (TableLayout) findViewById(R.id.group_table);
    }

    @Override
    protected void onStart() {
        super.onStart();
        current_groupid=-1;
        while (showGroups.getChildCount() > 1)
            showGroups.removeView(showGroups.getChildAt(showGroups.getChildCount() - 1));

        User_details_store userLocalStore = new User_details_store(this);
        User_details currentUser= userLocalStore.loggedInUser();
        String user=currentUser.username;

        DBConnect_groups connect=new DBConnect_groups(this);
        connect.retreiveJoinedGroups(user, new AsyncResponse<ArrayList<ArrayList>>() {
            @Override
            public void processFinish(ArrayList<ArrayList> output) {
                if(output.size()>0){
                    fillTable(output);
                }
                else {
                    Toast.makeText(View_Groups.this, "Error connecting to server", Toast.LENGTH_LONG).show();
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

            case R.id.Edit_group_question:

                if(current_groupid==-1){
                    Toast.makeText(getApplicationContext(),"No group has been selected!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent edit_selected = new Intent(View_Groups.this, Edit_question.class);
                    edit_selected.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    edit_selected.putExtra("selected_group", current_groupid);
                    startActivity(edit_selected);
                }

                break;

        }
    }

    private void fillTable(ArrayList<ArrayList> columns){

        ArrayList<Object> ids=new ArrayList<>();
        ArrayList<Object> questions=new ArrayList<>();

        for(int i=0;i<columns.get(0).size();i++){
            ids.add(columns.get(0).get(i));
            questions.add(columns.get(1).get(i));
        }


        final ArrayList<RadioButton>groupRadioButtonList=new ArrayList<>();


        for(int i=0;i<ids.size();i++){

            final TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            if((i%2)==0){
                row.setBackgroundColor(Color.rgb(182,236,255));
            }


            final TextView qid = new TextView(this);
            qid.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            qid.setTextColor(Color.BLACK);
            qid.setWidth(100);
            qid.setHeight(100);
            qid.setGravity(Gravity.CENTER);
            qid.setText("" + ids.get(i));
            row.addView(qid);


            TextView question = new TextView(this);
            question.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            question.setTextColor(Color.BLACK);
            question.setWidth(100);
            question.setHeight(100);
            question.setGravity(Gravity.CENTER);
            question.setText("" + questions.get(i));
            row.addView(question);
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
                            String current_value = qid.getText().toString();
                            current_groupid = Integer.parseInt(current_value);

                        } else {
                            button.setChecked(false);
                        }
                    }
                }
            });
            row.setMinimumHeight(150);
            showGroups.addView(row);
            }


        }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(View_Groups.this, Create_homepage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        //finish();
    }
}
