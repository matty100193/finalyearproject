package com.matty_christopher.quizapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

class DBConnect_question {

    private final ProgressDialog pDialog;
    private static final String address="https://matty-christopher.000webhostapp.com/";

    public DBConnect_question(Context context){
        pDialog=new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.setTitle("Processing");
        pDialog.setMessage("Please Wait");
    }

    public void setUpQuestion(int quizid,String question,ArrayList<String> users,ArrayList<String> answers,int complete,int correctAnswer,Db_response<String>  response){
        new setUpQuestionAsync(quizid,question,users,answers,complete,correctAnswer,response).execute();
    }

    public void updateQuestion(String questionid,String answer,int toupdate,Db_response<String>  response){
        new updateQuestionAsync(questionid,answer,toupdate,response).execute();
    }

    public void completeQuestion(String questionid,Db_response<String> response){
        new completeQuestionAysnc(questionid,response).execute();
    }


    public class setUpQuestionAsync extends AsyncTask<Void,Void,String> {

        final int quizid;
        final String question;
        final ArrayList<String> users;
        final ArrayList<String> answers;
        final int complete;
        final int correctAnswer;
        final Db_response<String>  response;

        public setUpQuestionAsync(int quizid,String question,ArrayList<String> users,ArrayList<String> answers,int complete,int correctAnswer,Db_response<String>  response){
            this.quizid=quizid;
            this.question=question;
            this.users=users;
            this.answers=answers;
            this.complete=complete;
            this.correctAnswer=correctAnswer;
            this.response=response;
        }




        @Override
        protected void onPostExecute(String output) {

            if (pDialog != null) {
                pDialog.dismiss();
            }
            response.processFinish(output);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("quiz_id", quizid);
            dataToSend.put("question", question);
            dataToSend.put("users0", users.get(0));
            dataToSend.put("users1", users.get(1));
            dataToSend.put("users2", users.get(2));
            dataToSend.put("users3", users.get(3));
            dataToSend.put("users4", users.get(4));
            dataToSend.put("answers1", answers.get(0));
            dataToSend.put("answers2", answers.get(1));
            dataToSend.put("answers3", answers.get(2));
            dataToSend.put("answers4", answers.get(3));
            dataToSend.put("correct_ans", complete);
            dataToSend.put("complete", correctAnswer);



            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            OutputStreamWriter writer=null;
            String output="";

            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "setupQuestion.php");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                if ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                    line = sb.toString();
                    output=line;
                    JSONObject js = new JSONObject(line);

                   if(js.length()>0) {
                       output = js.getString("question_id");
                    }
                }

            }
            catch (Exception e) {
                output="error";
                Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
            }
            finally {
                if (con != null) {con.disconnect();}
                if(writer!=null){try {writer.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
                if (reader != null) {try {reader.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
            }


            return output;
        }
    }


    public class updateQuestionAsync extends AsyncTask<Void,Void,String> {

        final String questionid;
        final String answer;
        final int toupdate;
        final Db_response<String>  response;

        public updateQuestionAsync(String questionid,String answer,int toupdate,Db_response<String>  response){
            this.questionid=questionid;
            this.answer=answer;
            this.toupdate=toupdate;
            this.response=response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String output) {

            if (pDialog != null) {
                pDialog.dismiss();
            }
            response.processFinish(output);
        }

        @Override
        protected String doInBackground(Void... params) {

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("questionid",questionid);
            dataToSend.put("answer",answer);
            dataToSend.put("number",toupdate);


            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            OutputStreamWriter writer=null;
            String output="";

            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "update_question.php");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                if ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                    line = sb.toString();
                    output=line;
                }

            }
            catch (Exception e) {
                output="error";
                Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
            }
            finally {
                if (con != null) {con.disconnect();}
                if(writer!=null){try {writer.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
                if (reader != null) {try {reader.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
            }


            return output;
        }
    }


    public class completeQuestionAysnc extends AsyncTask<Void,Void,String> {

        final String questionid;
        final Db_response<String> response;

        public completeQuestionAysnc(String questionid,Db_response<String> response){
            this.questionid=questionid;
            this.response=response;
        }


        @Override
        protected void onPostExecute(String result) {

            if (pDialog != null) {
                pDialog.dismiss();
            }
            response.processFinish(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("questionid", questionid);



            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            OutputStreamWriter writer=null;
            String result="";

            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "complete_question.php");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                if ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                    line = sb.toString();
                }
                result=line;

            }
            catch (Exception e) {
                Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
            }
            finally {
                if (con != null) {con.disconnect();}
                if(writer!=null){try {writer.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
                if (reader != null) {try {reader.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
            }


         return result;
        }
    }

/*
* modified from http://stackoverflow.com/questions/30740359/namevaluepair-httpparams-httpconnection-params-deprecated-on-server-request-cl,
* author Gagandeep Singh- modified to take in ContentValues
 */

    private String getEncodedData(ContentValues data) {
        StringBuilder sb = new StringBuilder();
        for(String key : data.keySet()) {
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
