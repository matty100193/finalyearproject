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


class DBConnect {

    public static String DatabaseMessage="";
    private final ProgressDialog pDialog;
    private static final int tmOut=5000;
    private static final String address="https://matty-christopher.000webhostapp.com/";

    public DBConnect(Context context){
        pDialog=new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.setTitle("Processing");
        pDialog.setMessage("Please Wait");
    }

    public void setUserData(User_details user,Db_response<User_details> callBack){
        pDialog.show();
        new StoreUserDataAsync(user,callBack).execute();
    }

    public void getUserData(User_details user,Db_response<User_details> callBack){
        pDialog.show();
        new GetUserDataAsync(user,callBack).execute();
    }

    public void sendUserEmail(String email){
        pDialog.show();
        new SendUserEmailAsync(email).execute();
    }

    public void getTop50(AsyncResponse<ArrayList<ArrayList>> response){
        pDialog.show();
        new getTop50Async(response).execute();
    }

    public void getAllUsers(AsyncResponse<ArrayList<String>> response){
        pDialog.show();
        new getAllUsersAysnc(response).execute();
    }

    public void setProfile(String password,String email,String username,String curr_email,String curr_password,AsyncResponse<String> response){
        pDialog.show();
        new setProfileAysnc(username,email,password,curr_email,curr_password,response).execute();
    }


    public class StoreUserDataAsync extends AsyncTask<Void,Void,Void>{

        final User_details user;
        final Db_response<User_details> callBack;

        public StoreUserDataAsync(User_details user,Db_response<User_details> callBack){
            this.user=user;
            this.callBack=callBack;
        }


        @Override
        protected void onPostExecute(Void aVoid) {

            pDialog.dismiss();
            callBack.processFinish(null);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("username", user.username);
            dataToSend.put("password", user.password);
            dataToSend.put("email", user.email);
            dataToSend.put("high_score", user.highscore);
            dataToSend.put("total_score", user.totalscore);
            dataToSend.put("total_answered", user.total_answered);
            dataToSend.put("total_correct", user.total_correct);
            dataToSend.put("total_highscore_attempts", user.total_highscore_attempts);



            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            OutputStreamWriter writer=null;

            HttpURLConnection con = null;
            try {
                URL url = new URL(address + "register.php");
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
                    DatabaseMessage=line;
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


    public class GetUserDataAsync extends AsyncTask<Void,Void,User_details> {

        final User_details user;
        final Db_response<User_details> callBack;

        public GetUserDataAsync(User_details user, Db_response<User_details> callBack) {
            this.user = user;
            this.callBack = callBack;
        }

        @Override
        protected void onPostExecute(User_details user) {
            pDialog.dismiss();
            callBack.processFinish(user);
            super.onPostExecute(user);
        }

        @Override
        protected User_details doInBackground(Void... params) {

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("username", user.username);
            dataToSend.put("password", user.password);
            Log.e("test",""+user.username);
            Log.e("test",""+user.password);
            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            HttpURLConnection con = null;
            User_details retUser=null;
            OutputStreamWriter writer=null;
            DatabaseMessage="";
            JSONObject js;
            String line;
            try {

                URL u = new URL(address+"login.php");
                con = (HttpURLConnection) u.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setConnectTimeout(tmOut);
                writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));


                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                line = sb.toString();
                js = new JSONObject(line);

                if (js.length() == 0) {
                    retUser = null;
                } else {
                    String email = js.getString("email");
                    int highs = js.getInt("high_score");
                    int totscore = js.getInt("total_score");
                    int totansw = js.getInt("total_answered");
                    int totcorr = js.getInt("total_correct");
                    int toths_att = js.getInt("total_highscore_attempts");

                    retUser = new User_details(user.username, user.password, email, highs, totscore,totansw,totcorr,toths_att);
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

            return retUser;
        }
    }




    public class SendUserEmailAsync extends AsyncTask<Void,Void,Void> {

        final String tosend;

        public SendUserEmailAsync(String enteredEmail) {
            tosend=enteredEmail;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pDialog.dismiss();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("email", tosend);
            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            HttpURLConnection con = null;
            OutputStreamWriter writer=null;
            try {

                URL u = new URL(address+"forgot_login_details.php");
                con = (HttpURLConnection) u.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(tmOut);
                con.setDoOutput(true);
                writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();

                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));


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

            return null;
        }



    }


    public class getTop50Async extends AsyncTask<Void,Void,ArrayList<ArrayList>> {

        final ArrayList<String> top50_username;
        final ArrayList<Integer>  top50_hscores;
        final AsyncResponse<ArrayList<ArrayList>> response;

        public getTop50Async(AsyncResponse<ArrayList<ArrayList>> response) {
            top50_username=new ArrayList<>();
            top50_hscores=new ArrayList<>();
            this.response=response;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList> lists) {
            pDialog.dismiss();
            response.processFinish(lists);
        }

        @Override
        protected ArrayList<ArrayList> doInBackground(Void... params) {

            BufferedReader reader = null;
            HttpURLConnection con = null;
            ArrayList<ArrayList> lists=new ArrayList<>();

            try {

                URL u = new URL(address+"leaderboard.php");
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
                        String username       = jsonobject.getString("username");
                        int hscore    = jsonobject.getInt("high_score");
                        top50_username.add(username);
                        top50_hscores.add(hscore);
                    }
                    lists.add(top50_username);
                    lists.add(top50_hscores);


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

    public class getAllUsersAysnc extends AsyncTask<Void,Void,ArrayList<String>> {

        final ArrayList<String> all_usernames;
        final AsyncResponse<ArrayList<String>> response;

        public getAllUsersAysnc(AsyncResponse<ArrayList<String>> response) {
            all_usernames=new ArrayList<>();
            this.response=response;
        }

        @Override
        protected void onPostExecute(ArrayList<String> users) {
            pDialog.dismiss();
            response.processFinish(users);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            BufferedReader reader = null;
            HttpURLConnection con = null;

            try {

                URL u = new URL(address+"getAllUsers.php");
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
                        String username       = jsonobject.getString("username");
                        all_usernames.add(username);
                    }


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

            return all_usernames;
        }



    }


    public class setProfileAysnc extends AsyncTask<Void,Void,String> {

        final String username;
        final String password;
        final String curr_email;
        final String curr_password;
        final String email;
        final AsyncResponse<String> response;

        public setProfileAysnc(String username,String email,String password,String curr_email,String curr_password,AsyncResponse<String> response) {
            this.username=username;
            this.email=email;
            this.password=password;
            this.response=response;
            this.curr_email=curr_email;
            this.curr_password=curr_password;
        }

        @Override
        protected void onPostExecute(String users) {
            pDialog.dismiss();
            response.processFinish(users);
        }

        @Override
        protected String doInBackground(Void... params) {

            BufferedReader reader = null;
            HttpURLConnection con = null;
            OutputStreamWriter writer;
            String output="success";

            ContentValues dataToSend = new ContentValues();
            dataToSend.put("username", username);
            dataToSend.put("password", password);
            dataToSend.put("email", email);
            dataToSend.put("curr_password", curr_password);
            dataToSend.put("curr_email", curr_email);


            String encodedStr = getEncodedData(dataToSend);

            try {

                URL u = new URL(address+"update_profile.php");
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
