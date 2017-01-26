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

class DBConnect_groups {

    private final ProgressDialog pDialog;
    private static final int tmOut=5000;
    private static final String address="https://matty-christopher.000webhostapp.com/";

    public DBConnect_groups(Context context){
        pDialog=new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.setTitle("Processing");
        pDialog.setMessage("Please Wait");
    }


    public void retreiveJoinedGroups(String username, AsyncResponse<ArrayList<ArrayList>> response){
        new retreiveJoinedGroupsAsync(username,response).execute();
    }

    public void retreiveSelectedQuestion(int selected_group, AsyncResponse<ContentValues> response){
        new retreiveSelectedQuestionAsync(selected_group,response).execute();
    }

    public void addGroup(String question_id,ArrayList<String> users,Db_response<String> response){
        new addGroupAsync(question_id,users,response).execute();
    }

    public class retreiveJoinedGroupsAsync extends AsyncTask<Void,Void,ArrayList<ArrayList>> {

        final String username;
        final AsyncResponse<ArrayList<ArrayList>> response;
        final ArrayList<Integer> quizids;
        final ArrayList<String>  questionids;


        public retreiveJoinedGroupsAsync(String username, AsyncResponse<ArrayList<ArrayList>> response){
            this.username=username;
            this.response=response;
            questionids=new ArrayList<>();
            quizids=new ArrayList<>();
        }


        @Override
        protected void onPostExecute(ArrayList<ArrayList> output) {

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
        protected ArrayList<ArrayList> doInBackground(Void... params) {

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("username", username);


            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            OutputStreamWriter writer=null;
            ArrayList<ArrayList> output=new ArrayList<>();

            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "view_partof_groups.php");
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
                    JSONArray jsonArray = new JSONArray(line);

                    for(int i=0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        int groupid       = jsonobject.getInt("groupid");
                        String questionid    = jsonobject.getString("question_id");
                        quizids.add(groupid);
                        questionids.add(questionid);
                    }
                    output.add(quizids);
                    output.add(questionids);

                }


            }
            catch (Exception e) {
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


    public class retreiveSelectedQuestionAsync extends AsyncTask<Void,Void,ContentValues> {

        final int groupid;
        final AsyncResponse<ContentValues> response;
        final ContentValues question_contents;


        public retreiveSelectedQuestionAsync(int groupid, AsyncResponse<ContentValues> response){
            this.groupid=groupid;
            this.response=response;
            question_contents=new ContentValues();
        }


        @Override
        protected void onPostExecute(ContentValues output) {

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
        protected ContentValues doInBackground(Void... params) {

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("selected_group", groupid);


            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            OutputStreamWriter writer=null;

            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "retrieve_question.php");
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
                    JSONArray jsonArray = new JSONArray(line);

                    if(jsonArray.length()>0) {

                        JSONObject js = jsonArray.getJSONObject(0);

                        String questionid    = js.getString("question_id");
                        int quiz_id = js.getInt("quiz_id");
                        String question=js.getString("question");
                        String creator_question=js.getString("creator_question");

                        String answer1=js.getString("answer1");
                        String creator_ans1=js.getString("creator_ans1");

                        String answer2=js.getString("answer2");
                        String creator_ans2=js.getString("creator_ans2");

                        String answer3=js.getString("answer3");
                        String creator_ans3=js.getString("creator_ans3");

                        String answer4=js.getString("answer4");
                        String creator_ans4=js.getString("creator_ans4");

                        int complete = js.getInt("complete");
                        int correct_answer = js.getInt("correct_answer");

                        question_contents.put("questionid",questionid);
                        question_contents.put("quiz_id",quiz_id);
                        question_contents.put("question",question);
                        question_contents.put("creator_question",creator_question);

                        question_contents.put("answer1",answer1);
                        question_contents.put("creator_ans1",creator_ans1);
                        question_contents.put("answer2",answer2);
                        question_contents.put("creator_ans2",creator_ans2);

                        question_contents.put("answer3",answer3);
                        question_contents.put("creator_ans3",creator_ans3);
                        question_contents.put("answer4",answer4);
                        question_contents.put("creator_ans4",creator_ans4);

                        question_contents.put("complete",complete);
                        question_contents.put("correct_answer",correct_answer);
                    }


                }


            }
            catch (Exception e) {
                Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);
            }
            finally {
                if (con != null) {con.disconnect();}
                if(writer!=null){try {writer.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
                if (reader != null) {try {reader.close();} catch (IOException e) {Log.e("Tag", "Explanation of what was being attempted when the exception was thrown", e);}}
            }


            return question_contents;
        }
    }

    public class addGroupAsync extends AsyncTask<Void,Void,String> {

        final String question_id;
        final ArrayList<String>  users;
        final Db_response<String> response;


        public addGroupAsync(String question_id,ArrayList<String> users,Db_response<String> response){
            this.question_id=question_id;
            this.users=users;
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
            dataToSend.put("question_id", question_id);
            int j=0;
            for(int i=0;i<users.size();i++){
                dataToSend.put("users"+i, users.get(i));
                j++;
            }
            for(int i=j;i<users.size();j++){
                dataToSend.put("users"+j, "");
            }

            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            OutputStreamWriter writer=null;
            String output="";
            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "create_group.php");
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
