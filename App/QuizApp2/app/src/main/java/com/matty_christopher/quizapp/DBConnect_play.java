package com.matty_christopher.quizapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

class DBConnect_play {

    private final ProgressDialog pDialog;
    private static final int tmOut=5000;
    private static final String address="https://matty-christopher.000webhostapp.com/";

    public DBConnect_play(Context context){
        pDialog=new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.setTitle("Processing");
        pDialog.setMessage("Please Wait");
    }



    public void getQuizzes(AsyncResponse<ArrayList<ArrayList>> response){
        pDialog.show();
        new getQuizzesAsync(response).execute();
    }

    public void getQuestions(String qname,AsyncResponse<ArrayList<Question_details>> response){
        pDialog.show();
        new getQuestionsAsync(qname,response).execute();
    }

    public void uploadQuestionStats(ArrayList<String> ids,ArrayList<ArrayList<Integer>> userstats,ArrayList<Float>user_ratings){
        new uploadQuestionStatsAsync(ids,userstats,user_ratings).execute();

    }

    public void uploadUserStats(String user_id,int h_score,int total_ans,int tot_correct,int isHscore,Db_response<String> response){
        new uploadUserStatsAsync(user_id,h_score, total_ans, tot_correct, isHscore,response).execute();

    }

    public void getMatchQuestions(int matchid,AsyncResponse<ArrayList<Question_details>> response){
        pDialog.show();
        new getMatchQuestionsAsync(matchid,response).execute();
    }




    public class getQuizzesAsync extends AsyncTask<Void,Void,ArrayList<ArrayList>> {

        final AsyncResponse<ArrayList<ArrayList>> response;
        final ArrayList<String> quiznames;
        final ArrayList<Integer>  noquestions;
        final ArrayList<String> creators;
        final ArrayList<Integer> avgs;

        public getQuizzesAsync(AsyncResponse<ArrayList<ArrayList>> response) {
            this.response = response;
            quiznames=new ArrayList<>();
            noquestions=new ArrayList<>();
            creators=new ArrayList<>();
            avgs=new ArrayList<>();
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList> lists) {
            if (pDialog != null) {
                pDialog.dismiss();
            }
            response.processFinish(lists);
        }

        @Override
        protected ArrayList<ArrayList> doInBackground(Void... params) {

            BufferedReader reader = null;
            HttpURLConnection con = null;
            ArrayList<ArrayList> lists=new ArrayList<>();

            try {
                URL u = new URL(address+"select_quizzes.php");
                con = (HttpURLConnection) u.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(tmOut);

                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                if ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                    line = sb.toString();
                    JSONArray jsonArray = new JSONArray(line);

                    for(int i=0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String quiz = jsonobject.getString("quiz_name");
                        String username = jsonobject.getString("username_creator");
                        int num    = jsonobject.getInt("no_questions");
                        int avg    = jsonobject.getInt("avg_score");
                        quiznames.add(quiz);
                        noquestions.add(num);
                        creators.add(username);
                        avgs.add(avg);
                    }
                    lists.add(quiznames);
                    lists.add(noquestions);
                    lists.add(creators);
                    lists.add(avgs);


                }

            }
            catch (Exception e) {
                Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
            }
            finally {
                if (con != null) {con.disconnect();}
                if (reader != null) {try {reader.close();} catch (IOException e){Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}
                }
            }

            return lists;
        }

    }

    private class getQuestionsAsync  extends AsyncTask<Void,Void,ArrayList<Question_details>>  {

        final AsyncResponse<ArrayList<Question_details>> response;
        final String qname;

        public getQuestionsAsync(String qname, AsyncResponse<ArrayList<Question_details>> response) {
           this.qname=qname;
           this.response=response;
        }

        @Override
        protected void onPostExecute(ArrayList<Question_details> lists) {
            if (pDialog != null) {
                pDialog.dismiss();
            }
            response.processFinish(lists);
        }

        @Override
        protected ArrayList<Question_details> doInBackground(Void... params) {

            BufferedReader reader = null;
            OutputStreamWriter writer=null;
            HttpURLConnection con = null;
            ArrayList<Question_details> lists=new ArrayList<>();

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("quizname", qname);
            String encodedStr = getEncodedData(dataToSend);

            try {
                URL u = new URL(address+"getQuestions.php");
                con = (HttpURLConnection) u.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setConnectTimeout(tmOut);

                writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();

                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                if ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                    line = sb.toString();
                    JSONArray js = new JSONArray(line);

                    for(int i=0;i<js.length();i++){
                        JSONObject object = js.getJSONObject(i);

                         String qid = object.getString("question_id");
                         String question = object.getString("question");
                         String creator = object.getString("creator_question");
                         int correct = object.getInt("correct_answer");
                         int tot_score = object.getInt("total_score");
                         int tot_ans = object.getInt("total_answered");
                         int avg = object.getInt("average_score");
                         int[] qs_tot_ans=new int[4];
                         String[] answers=new String[4];
                         String[] users=new String[4];

                        for(int j=0;j<answers.length;j++){
                            int n=j+1;
                            qs_tot_ans[j] = object.getInt("total_answer"+n);
                            answers[j] = object.getString("answer"+n);
                            users[j] = object.getString("creator_ans"+n);
                        }

                        Question_details qd=new Question_details(qid,question,creator,answers,users,correct,tot_score,tot_ans,avg,qs_tot_ans);
                        lists.add(qd);
                    }

                }

            }
            catch (Exception e) {
                Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
            }
            finally {
                if (con != null) {con.disconnect();}
                if(writer!=null){try {writer.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
                if (reader != null) {try {reader.close();} catch (IOException e){Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}
                }
            }

            return lists;
        }
    }


    //part of code commented out incase testing modified version without dialog has bugs

    public class uploadQuestionStatsAsync extends AsyncTask<Void,Void,Void> {

       // final Db_response<String> response;
        final ArrayList<ArrayList<Integer>>  answers;
        final ArrayList<String> qname;
        final ArrayList<Float>user_ratings;
      //  int progress;

        public uploadQuestionStatsAsync(ArrayList<String> ids,ArrayList<ArrayList<Integer>> userstats,ArrayList<Float>user_ratings) {
           // this.response = response;
            this.qname=ids;
            this.answers=userstats;
            this.user_ratings=user_ratings;
           // this.progress=0;
        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            pDialog.setTitle("Uploading stats");
//            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            pDialog.setIndeterminate(false);
//            pDialog.setProgress(0);
//            pDialog.setMax(qname.size());
//            pDialog.show();
//        }

//        @Override
//        protected void onPostExecute(String output) {
//
//            if (pDialog != null) {
//                pDialog.dismiss();
//            }
//            response.processFinish("success");
//        }

        @Override
        protected Void doInBackground(Void... params) {


            //String output="";

            for(int i=0;i<qname.size();i++) {
                ArrayList<Integer>  current=answers.get(i);
                BufferedReader reader = null;
                HttpURLConnection con = null;
                OutputStreamWriter writer=null;

                ContentValues toSend = new ContentValues();
                toSend.put("question_id",qname.get(i));
                toSend.put("answer1",current.get(0) );
                toSend.put("answer2",current.get(1) );
                toSend.put("answer3",current.get(2) );
                toSend.put("answer4", current.get(3));
                toSend.put("ratings",user_ratings.get(i));

                String encodedStr = getEncodedData(toSend);

                try {
                    URL u = new URL(address + "uploadStats.php");
                    con = (HttpURLConnection) u.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    //con.setConnectTimeout(tmOut);
                    writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(encodedStr);
                    writer.flush();
                   // StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

//                    String line;
//                    if ((line = reader.readLine()) != null) {
//                        sb.append(line).append("\n");
//                        line = sb.toString();
//                        //output = line;
//                      //  progress++;
//                    }
                    //pDialog.setProgress(progress);

                } catch (Exception e) {
                   // output = "error";
                    Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
                        }
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
                        }
                    }
                }
            }

            return null;
        }



    }

    public class uploadUserStatsAsync extends AsyncTask<Void,Void,String> {

        final Db_response<String> response;
        final String user_id;
        final int h_score;
        final int total_ans;
        final int tot_correct;
        final int isHscore;

        public uploadUserStatsAsync(String user_id,int h_score,int total_ans,int tot_correct,int isHscore,Db_response<String> response) {
            this.response = response;
            this.user_id=user_id;
            this.h_score=h_score;
            this.total_ans=total_ans;
            this.tot_correct=tot_correct;
            this.isHscore=isHscore;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setTitle("Uploading stats");
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String output) {

            if (pDialog != null) {
                pDialog.dismiss();
            }
            response.processFinish("success");
        }

        @Override
        protected String doInBackground(Void... params) {


            String output="";

            BufferedReader reader = null;
            HttpURLConnection con = null;
            OutputStreamWriter writer=null;

            ContentValues toSend = new ContentValues();
            toSend.put("user_id",user_id);
            toSend.put("h_score",h_score);
            toSend.put("total_answered",total_ans );
            toSend.put("total_correct",tot_correct );
            toSend.put("hscore_mode", isHscore);

            String encodedStr = getEncodedData(toSend);

            try {
                URL u = new URL(address + "uploadUserStats.php");
                con = (HttpURLConnection) u.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                //con.setConnectTimeout(tmOut);
                writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                if ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                    line = sb.toString();
                    output = line;
                }

            } catch (Exception e) {
                output = "error";
                Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
            } finally {
                if (con != null) {con.disconnect();}
                if (writer != null) {try {writer.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
                if (reader != null) {try {reader.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
            }


            return output;
        }

    }



    private class getMatchQuestionsAsync  extends AsyncTask<Void,Void,ArrayList<Question_details>>  {

        final AsyncResponse<ArrayList<Question_details>> response;
        final int matchid;

        public getMatchQuestionsAsync(int matchid, AsyncResponse<ArrayList<Question_details>> response) {
            this.matchid=matchid;
            this.response=response;
        }

        @Override
        protected void onPostExecute(ArrayList<Question_details> lists) {
            if (pDialog != null) {
                pDialog.dismiss();
            }
            response.processFinish(lists);
        }

        @Override
        protected ArrayList<Question_details> doInBackground(Void... params) {

            BufferedReader reader = null;
            OutputStreamWriter writer=null;
            HttpURLConnection con = null;
            ArrayList<Question_details> lists=new ArrayList<>();

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("matchid", matchid);
            String encodedStr = getEncodedData(dataToSend);

            try {
                URL u = new URL(address+"getMatchQuestions.php");
                con = (HttpURLConnection) u.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setConnectTimeout(tmOut);

                writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();

                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                if ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                    line = sb.toString();
                    JSONArray js = new JSONArray(line);

                    for(int i=0;i<js.length();i++){
                        JSONObject object = js.getJSONObject(i);

                        String qid = object.getString("question_id");
                        String question = object.getString("question");
                        String creator = object.getString("creator_question");
                        int correct = object.getInt("correct_answer");
                        int tot_score = object.getInt("total_score");
                        int tot_ans = object.getInt("total_answered");
                        int avg = object.getInt("average_score");
                        int[] qs_tot_ans=new int[4];
                        String[] answers=new String[4];
                        String[] users=new String[4];

                        for(int j=0;j<answers.length;j++){
                            int n=j+1;
                            qs_tot_ans[j] = object.getInt("total_answer"+n);
                            answers[j] = object.getString("answer"+n);
                            users[j] = object.getString("creator_ans"+n);
                        }

                        Question_details qd=new Question_details(qid,question,creator,answers,users,correct,tot_score,tot_ans,avg,qs_tot_ans);
                        lists.add(qd);
                    }

                }

            }
            catch (Exception e) {
                Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
            }
            finally {
                if (con != null) {con.disconnect();}
                if(writer!=null){try {writer.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
                if (reader != null) {try {reader.close();} catch (IOException e){Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}
                }
            }

            return lists;
        }
    }


/*
* modified from http://stackoverflow.com/questions/30740359/namevaluepair-httpparams-httpconnection-params-deprecated-on-server-request-cl,
* author Gagandeep Singh- modified to take in ContentValues
 */

    private String getEncodedData(ContentValues data) {
        StringBuilder sb = new StringBuilder();
        for (String key : data.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(String.valueOf(data.get(key)), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(sb.length()>0)
                sb.append("&");

            sb.append(key).append("=").append(value);
        }
        return sb.toString();
    }



}
