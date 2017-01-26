package com.matty_christopher.quizapp;

public class User_details {

     final String username;
     final String password;
     final String email;
     final int highscore;
     final int totalscore;

     final int total_answered;
     final int total_correct;
     final int total_highscore_attempts;

    public User_details(String u_name,String u_pass,String u_email,int hscore,int tscore,int total_answered,int total_correct,int total_highscore_attempts){
        username=u_name;
        password=u_pass;
        email=u_email;
        highscore=hscore;
        totalscore=tscore;

        this.total_answered=total_answered;
        this.total_correct=total_correct;
        this.total_highscore_attempts=total_highscore_attempts;


    }

    public User_details(String u_name,String u_pass){
        username=u_name;
        password=u_pass;
        email="";
        highscore=0;
        totalscore=0;
        this.total_answered=0;
        this.total_correct=0;
        this.total_highscore_attempts=0;
    }

}
