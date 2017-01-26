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

class DBConnect_create_quiz {

    private final ProgressDialog pDialog;
    private static final int tmOut=5000;
    private static final String address="https://matty-christopher.000webhostapp.com/";

    public DBConnect_create_quiz(Context context){
        pDialog=new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.setTitle("Processing");
        pDialog.setMessage("Please Wait");
    }


    public void setUpQuiz(String quizname,String pwd,String username,Db_response<String> response){
        pDialog.show();
        new setUpQuizAsync(quizname,pwd,username,response).execute();
    }

    public void checkQuizDetails(AsyncResponse<ArrayList<Quiz_details>> response){
        pDialog.show();
        new checkQuizDetailsAsync(response).execute();
    }




    public class setUpQuizAsync extends AsyncTask<Void,Void,String> {

        final String username;
        final String pwd;
        final String qname;
        final Db_response<String>  response;

        public setUpQuizAsync(String qname,String pwd,String username,Db_response<String>  response){
            this.username=username;
            this.pwd=pwd;
            this.qname=qname;
            this.response=response;
        }


        @Override
        protected void onPostExecute(String output) {

            pDialog.dismiss();
            response.processFinish(output);
        }

        @Override
        protected String doInBackground(Void... params) {

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("creator", username);
            dataToSend.put("password", pwd);
            dataToSend.put("quiz_name", qname);


            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            OutputStreamWriter writer=null;
            String output="";

            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "setupQuiz.php");
                con = (HttpURLConnection) url.openConnection();
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
                }
                output=line;


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


    public class checkQuizDetailsAsync extends AsyncTask<Void,Void,ArrayList<Quiz_details> > {


        final AsyncResponse<ArrayList<Quiz_details>> response;
        final ArrayList<Quiz_details> quiz_details;

        public checkQuizDetailsAsync(AsyncResponse<ArrayList<Quiz_details>> response){

            this.response=response;
            quiz_details=new ArrayList<>();
        }


        @Override
        protected void onPostExecute(ArrayList<Quiz_details> lists) {
            if (pDialog != null) {
                pDialog.dismiss();
            }
            response.processFinish(lists);
        }

        @Override
        protected ArrayList<Quiz_details>  doInBackground(Void... params) {



            BufferedReader reader = null;
            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "checkQuiz.php");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(tmOut);

                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                if ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                    line = sb.toString();
                    JSONArray js = new JSONArray(line);

                    for(int i=0;i<js.length();i++){
                        JSONObject object = js.getJSONObject(i);

                        int qid = object.getInt("quiz_id");
                        String qname = object.getString("quiz_name");
                        String creator=object.getString("username_creator");
                        String pwd = object.getString("password");

                        Quiz_details qd=new Quiz_details(qid,qname,pwd,creator);
                        quiz_details.add(qd);
                    }

                }


            }
            catch (Exception e) {
                Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
            }
            finally {
                if (con != null) {con.disconnect();}
                if (reader != null) {try {reader.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
            }


            return quiz_details;
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
