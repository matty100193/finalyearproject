package com.matty_christopher.quizapp;


import android.content.Context;
import android.content.SharedPreferences;

class User_details_store {

    private static final String userStore="UserDetails";
    private final SharedPreferences userLocalDatabase;

    public User_details_store(Context context){
        userLocalDatabase=context.getSharedPreferences(userStore,0);
    }

    public void storeData(User_details user){
        SharedPreferences.Editor editor=userLocalDatabase.edit();
        editor.putString("username",user.username);
        editor.putString("password",user.password);
        editor.putString("email",user.email);
        editor.putInt("highscore", user.highscore);
        editor.putInt("totalscore", user.totalscore);

        editor.putInt("total_answered", user.total_answered);
        editor.putInt("total_correct", user.total_correct);
        editor.putInt("total_highscore_attempts", user.total_highscore_attempts);
        editor.apply();
    }

    public void updateStats(User_details user,int hscore,int answered,int correct){
        SharedPreferences.Editor editor=userLocalDatabase.edit();
        editor.putString("username",user.username);
        editor.putString("password",user.password);
        editor.putString("email", user.email);
        if(hscore>user.highscore){
            editor.putInt("highscore", hscore);
        }
        else{
            editor.putInt("highscore", user.highscore);
        }
        editor.putInt("totalscore", user.totalscore);

        editor.putInt("total_answered", user.total_answered+answered);
        editor.putInt("total_correct", user.total_correct+correct);
        editor.putInt("total_highscore_attempts", user.total_highscore_attempts);
        editor.apply();
    }

    public void updateStats(User_details user,String c_email,String c_pass){
        SharedPreferences.Editor editor=userLocalDatabase.edit();
        editor.putString("username",user.username);

            editor.putString("email", c_email);

            editor.putString("password",c_pass);

        editor.putInt("highscore", user.highscore);
        editor.putInt("totalscore", user.totalscore);
        editor.putInt("total_answered", user.total_answered);
        editor.putInt("total_correct", user.total_correct);
        editor.putInt("total_highscore_attempts", user.total_highscore_attempts);
        editor.apply();
    }

    public User_details loggedInUser(){
        String username_loggedIn=userLocalDatabase.getString("username", "");
        String password_loggedIn=userLocalDatabase.getString("password","");
        String email_loggedIn=userLocalDatabase.getString("email", "");
        int hScore_loggedIn=userLocalDatabase.getInt("highscore", -1);
        int totalScore_loggedIn=userLocalDatabase.getInt("totalscore", -1);

        int total_answered=userLocalDatabase.getInt("total_answered", -1);
        int total_correct=userLocalDatabase.getInt("total_correct",-1);
        int total_highscore_attempts=userLocalDatabase.getInt("total_highscore_attempts", -1);

        return new User_details(username_loggedIn,password_loggedIn,email_loggedIn,hScore_loggedIn,totalScore_loggedIn,total_answered,total_correct,total_highscore_attempts);
    }

    public void logoutUser(){
        SharedPreferences.Editor editor=userLocalDatabase.edit();
        editor.clear();
        editor.apply();
    }



}
