package com.matty_christopher.quizapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.util.ArrayList;
import java.util.Collections;

/*
*class uses MPAndroidChart API from github.com/PhilJay/MPAndroidChart
* author PhilJay, used to create pie chart dispaly
* followed tutorial to implement using: https://www.youtube.com/watch?v=VfLop_oLYU0
 */

public class Stats extends AppCompatActivity{

    private PieChart piechart;
    private final float[] stats=new float[4];
    private final String[] users=new String[4];
    private final String[]answers={"answer 1","answer 2","answer 3","answer 4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Question_details current = Question_template.current;
        int pos = getIntent().getIntExtra("position",0);

        if(current!=null) {
            ArrayList<Integer> currentStats=Question_template.userstats.get(pos);

            for (int i = 0; i < stats.length; i++) {
                stats[i] = (float) current.qs_tot_ans[i];
                users[i] = current.users[i];
                if(currentStats.get(i)==1){
                    stats[i]++;
                }
            }


            TextView user1 = (TextView) findViewById(R.id.answer1_txt);
            TextView user2 = (TextView) findViewById(R.id.answer2_txt);
            TextView user3 = (TextView) findViewById(R.id.answer3_txt);
            TextView user4 = (TextView) findViewById(R.id.answer4_txt);
            assert user1 != null;
            user1.setText("answer 1: " + users[0]);
            assert user2 != null;
            user2.setText("answer 2: " + users[1]);
            assert user3 != null;
            user3.setText("answer 3: " + users[2]);
            assert user4 != null;
            user4.setText("answer 4: " + users[3]);

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            getWindow().setLayout((int) (width * 0.85), (int) (height * 0.8));

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.statsLayout);
            piechart = new PieChart(this);

            assert layout != null;
            layout.addView(piechart);
            piechart.setUsePercentValues(true);
            piechart.setDrawHoleEnabled(true);
            piechart.setDescription("");
            piechart.setHoleColorTransparent(true);
            piechart.setHoleRadius(5);
            piechart.setTransparentCircleRadius(0);

            piechart.setRotationEnabled(false);

            addData();

            Legend l = piechart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7);
            l.setYEntrySpace(5);

            piechart.getLegend().setEnabled(false);

        }
    }

    private void addData() {

        ArrayList<Entry> listStats=new ArrayList<>();

        for(int i=0;i<stats.length;i++){
            listStats.add(new Entry(stats[i],i));
        }

        ArrayList<String> listAnswers=new ArrayList<>();

        Collections.addAll(listAnswers, answers);

        PieDataSet dataset=new PieDataSet(listStats,"Question breakdown");
        dataset.setSliceSpace(1);
        dataset.setSelectionShift(3);

        ArrayList<Integer>colors=new ArrayList<>();


        for(int c: ColorTemplate.COLORFUL_COLORS){
            colors.add(c);
        }

        colors.add(ColorTemplate.getHoloBlue());
        dataset.setColors(colors);

        PieData pd=new PieData(listAnswers, dataset);
        pd.setValueFormatter(new PercentFormatter());
        pd.setValueTextSize(15f);
        pd.setValueTextColor(Color.WHITE);

        piechart.setData(pd);
        piechart.highlightValues(null);
        piechart.invalidate();
    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
}
