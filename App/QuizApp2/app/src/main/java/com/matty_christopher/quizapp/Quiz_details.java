package com.matty_christopher.quizapp;

public class Quiz_details {

    final int quiz_id;
    String qname;
    String pwd;
    String creator;

    public Quiz_details(int id){
        this.quiz_id=id;
    }

    public Quiz_details(int id,String qname,String pwd,String creator){
        this.quiz_id=id;
        this.qname=qname;
        this.pwd=pwd;
        this.creator=creator;
    }

}
