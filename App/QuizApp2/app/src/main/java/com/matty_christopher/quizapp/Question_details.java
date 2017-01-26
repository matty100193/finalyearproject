package com.matty_christopher.quizapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Question_details implements Parcelable {

    public final String qid;
    public final String question;
    private final String creator;
    public final String[] answers;
    public final String[] users;
    public final int correct;
    public final int tot_score;
    private final int tot_ans;
    public final int avg;
    public final int[] qs_tot_ans;


    public Question_details(String qid,String question,String creator,String[] answers,String[] users, int correct, int tot_score,int tot_ans, int avg,int[] qs_tot_ans){
        this.qid=qid;
        this.question=question;
        this.creator=creator;
        this.answers=answers;
        this.users=users;
        this.correct=correct;
        this.tot_score=tot_score;
        this.tot_ans=tot_ans;
        this.avg=avg;
        this.qs_tot_ans=qs_tot_ans;
    }

    private Question_details(Parcel in) {
        qid = in.readString();
        question = in.readString();
        creator = in.readString();
        answers = in.createStringArray();
        users = in.createStringArray();
        correct = in.readInt();
        tot_score = in.readInt();
        tot_ans = in.readInt();
        avg = in.readInt();
        qs_tot_ans = in.createIntArray();
    }

    public static final Creator<Question_details> CREATOR = new Creator<Question_details>() {
        @Override
        public Question_details createFromParcel(Parcel in) {
            return new Question_details(in);
        }

        @Override
        public Question_details[] newArray(int size) {
            return new Question_details[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(qid);
        dest.writeString(question);
        dest.writeString(creator);
        dest.writeStringArray(answers);
        dest.writeStringArray(users);
        dest.writeInt(correct);
        dest.writeInt(tot_score);
        dest.writeInt(tot_ans);
        dest.writeInt(avg);
        dest.writeIntArray(qs_tot_ans);
    }
}
