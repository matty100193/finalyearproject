package com.matty_christopher.quizapp;


import android.content.Context;
import android.content.SharedPreferences;

class Quiz_details_store {

    private static final String quizStore="QuizDetails";
    private final SharedPreferences quizLocalDatabase;

    public Quiz_details_store(Context context){
        quizLocalDatabase=context.getSharedPreferences(quizStore,0);
    }

    public void storeData(Quiz_details quiz){
        SharedPreferences.Editor editor=quizLocalDatabase.edit();
        editor.putInt("quiz_id", quiz.quiz_id);
        editor.apply();
    }


    public Quiz_details currentQuiz(){
        int current_quiz_id=quizLocalDatabase.getInt("quiz_id", -1);

        return new Quiz_details(current_quiz_id);
    }

}
