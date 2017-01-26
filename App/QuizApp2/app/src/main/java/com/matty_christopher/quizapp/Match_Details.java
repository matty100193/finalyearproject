package com.matty_christopher.quizapp;


public class Match_Details {

    public int creator_score;
    public int challenger_score;
    public String status_challenger;
    public String status_creator;
    public String creator_name;
    public String challenger_name;

    public Match_Details(int creator_score,int challenger_score,String status_challenger,String status_creator,String challenger_name,String creator_name){
        this.creator_score=creator_score;
        this.challenger_score=challenger_score;
        this.status_challenger=status_challenger;
        this.status_creator=status_creator;
        this.creator_name=creator_name;
        this.challenger_name=challenger_name;
    }

}
