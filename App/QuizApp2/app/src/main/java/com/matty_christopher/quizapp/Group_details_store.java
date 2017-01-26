package com.matty_christopher.quizapp;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

class Group_details_store {
    private static final String groupStore="GroupDetails";
    private final SharedPreferences groupLocalDatabase;

    public Group_details_store(Context context){
        groupLocalDatabase=context.getSharedPreferences(groupStore,0);
    }

    public void storeData(Group_details group){
        SharedPreferences.Editor editor=groupLocalDatabase.edit();
        editor.putStringSet("group_users", group.storedUsers);
        editor.apply();
    }

    public Group_details getGroup(){
        Set<String> group_users=groupLocalDatabase.getStringSet("group_users", new HashSet<String>());
        return new Group_details(group_users);
    }

    public void clearDetails(){
        SharedPreferences.Editor editor=groupLocalDatabase.edit().clear();
        editor.apply();
    }


}
