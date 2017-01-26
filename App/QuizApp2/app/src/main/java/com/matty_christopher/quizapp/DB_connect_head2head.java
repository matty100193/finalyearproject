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

class DB_connect_head2head {

    private final ProgressDialog pDialog;
    private static final int tmOut=5000;
    private static final String address="https://matty-christopher.000webhostapp.com/";

    public DB_connect_head2head(Context context){
        pDialog=new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.setTitle("Processing");
        pDialog.setMessage("Please Wait");
    }

    public void sendInvite(String creator,String toSend,String date,String expires,Db_response<String> response){
        pDialog.show();
        new sendInviteAsync(creator,toSend,date,expires,response).execute();
    }

    public void retreiveInvites(String challenger,AsyncResponse<ArrayList<ArrayList>> response){
        new retreiveInvitesAsync(challenger,response).execute();
    }

    public void notifyChoice(String choice,int id,String user,AsyncResponse<String> response){
        new notifyChoiceAsync(choice,id,user,response).execute();
    }

    public void retreiveMatches(String user,AsyncResponse<ArrayList<ArrayList>> response){
        new retreiveMatchesAsync(user,response).execute();
    }
    public void updateMatch(String username,int score,int match_id){
        new updateMatchAsync(username,score,match_id).execute();
    }

    public void getMatchDetails(int match_id,AsyncResponse<Match_Details> response){
        new getMatchScoresAsync(match_id,response).execute();
    }


    public class sendInviteAsync extends AsyncTask<Void,Void,String> {

        final String creator;
        final String toSend;
        final String date;
        final Db_response<String> response;
        final String expires;

        public sendInviteAsync(String creator,String toSend,String date,String expires,Db_response<String> response) {
            this.creator=creator;
            this.toSend=toSend;
            this.date=date;
            this.response=response;
            this.expires=expires;
        }

        @Override
        protected void onPostExecute(String output) {
            pDialog.dismiss();
            response.processFinish(output);
        }

        @Override
        protected String doInBackground(Void... params) {

            BufferedReader reader = null;
            HttpURLConnection con = null;
            String output="";

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("creator", creator);
            dataToSend.put("user_challenge", toSend);
            dataToSend.put("date", date);
            dataToSend.put("expires", expires);

            String encodedStr = getEncodedData(dataToSend);
            OutputStreamWriter writer;

            try {

                URL u = new URL(address+"send_invite.php");
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
                    output=line;
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

            return output;
        }
    }

    public class retreiveInvitesAsync extends AsyncTask<Void,Void,ArrayList<ArrayList>> {

        final String username;
        final AsyncResponse<ArrayList<ArrayList>> response;
        final ArrayList<Integer> invite_ids;
        final ArrayList<String>  creators;
        final ArrayList<String>  dates;
        final ArrayList<String>  expiry_dates;

        public retreiveInvitesAsync(String challenger, AsyncResponse<ArrayList<ArrayList>> response){
            this.username=challenger;
            this.response=response;
            invite_ids=new ArrayList<>();
            creators=new ArrayList<>();
            dates=new ArrayList<>();
            expiry_dates=new ArrayList<>();
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
            dataToSend.put("user", username);


            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            OutputStreamWriter writer=null;
            ArrayList<ArrayList> output=new ArrayList<>();

            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "retrieve_invites.php");
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
                        int ids       = jsonobject.getInt("match_id");
                        String creator    = jsonobject.getString("creator");
                        String date    = jsonobject.getString("time_created");
                        String expiry_date    = jsonobject.getString("expires");
                        invite_ids.add(ids);
                        creators.add(creator);
                        dates.add(date);
                        expiry_dates.add(expiry_date);
                    }
                    output.add(invite_ids);
                    output.add(creators);
                    output.add(dates);
                    output.add(expiry_dates);

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

    public class notifyChoiceAsync extends AsyncTask<Void,Void,String> {


        final String choice;
        final int id;
        final AsyncResponse<String> response;
        final String user;

        public notifyChoiceAsync(String choice,int id,String user,AsyncResponse<String> response){
            this.choice=choice;
            this.response=response;
            this.id=id;
            this.user=user;
        }

        @Override
        protected void onPostExecute(String s) {
            response.processFinish(s);
        }

        @Override
        protected String doInBackground(Void... params) {

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("choice", choice);
            dataToSend.put("id", id);
            dataToSend.put("user", user);
            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            OutputStreamWriter writer=null;
            String output="could not send choice";

            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "inviteResponse.php");
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
                    output=line;

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


    public class retreiveMatchesAsync extends AsyncTask<Void,Void,ArrayList<ArrayList>> {

        final String username;
        final AsyncResponse<ArrayList<ArrayList>> response;
        final ArrayList<Integer> match_ids;
        final ArrayList<String>  opponents;
        final ArrayList<String>  expiry_dates;
        final ArrayList<String> statuses;

        public retreiveMatchesAsync(String user, AsyncResponse<ArrayList<ArrayList>> response){
            this.username=user;
            this.response=response;
            match_ids=new ArrayList<>();
            opponents=new ArrayList<>();
            expiry_dates=new ArrayList<>();
            statuses=new ArrayList<>();
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
            dataToSend.put("user", username);


            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            OutputStreamWriter writer=null;
            ArrayList<ArrayList> output=new ArrayList<>();

            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "retreiveMatches.php");
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
                        int ids       = jsonobject.getInt("match_id");
                        String creator    = jsonobject.getString("creator");
                        String challenger = jsonobject.getString("challenger");

                        String status = jsonobject.getString("status_challenger");

                        String opponent=creator;
                        if(username.equals(creator)){
                            opponent=challenger;
                            status = jsonobject.getString("status_creator");
                        }
                        String expiry_date    = jsonobject.getString("expiry_date");
                        match_ids.add(ids);
                        opponents.add(opponent);
                        expiry_dates.add(expiry_date);
                        statuses.add(status);
                    }
                    output.add(match_ids);
                    output.add(opponents);
                    output.add(expiry_dates);
                    output.add(statuses);

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


    public class updateMatchAsync extends AsyncTask<Void,Void,Void> {

        final String username;
        final int score;
        final int match_id;

        public updateMatchAsync(String username, int score,int match_id){
            this.username=username;
            this.score=score;
            this.match_id=match_id;

        }




        @Override
        protected Void doInBackground(Void... params) {

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("username", username);
            dataToSend.put("score", score);
            dataToSend.put("match_id", match_id);
            Log.e("tag", "" + username + ", " + score + ", " + match_id);

            String encodedStr = getEncodedData(dataToSend);
            OutputStreamWriter writer=null;
            BufferedReader reader = null;


            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "updateMatchScore.php");
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
                    Log.e("tag", line);

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

            return null;
        }
    }


    public class getMatchScoresAsync extends AsyncTask<Void,Void,Match_Details> {

        final int match_id;
        final AsyncResponse<Match_Details> response;

        public getMatchScoresAsync(int match_id,AsyncResponse<Match_Details> response){

            this.match_id=match_id;
            this.response=response;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }


        @Override
        protected void onPostExecute(Match_Details output) {

            if (pDialog != null) {
                pDialog.dismiss();
            }
            response.processFinish(output);
        }


        @Override
        protected Match_Details doInBackground(Void... params) {

            Match_Details details=null;
            ContentValues dataToSend = new ContentValues();
            dataToSend.put("match_id", match_id);

            String encodedStr = getEncodedData(dataToSend);
            OutputStreamWriter writer=null;
            BufferedReader reader = null;


            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "getMatchScore.php");
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
                    Log.e("tag",line);

                    JSONObject jsonObject=new JSONObject(line);

                    if(jsonObject.length()>0){

                        String creator=jsonObject.getString("creator");
                        String challenger=jsonObject.getString("challenger");
                        String status_challenger=jsonObject.getString("status_challenger");
                        String status_creator=jsonObject.getString("status_creator");
                        int creator_score=jsonObject.getInt("creator_score");
                        int challenger_score=jsonObject.getInt("challenger_score");

                        details=new Match_Details(creator_score,challenger_score,status_challenger,status_creator,challenger,creator);
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

            return details;
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
