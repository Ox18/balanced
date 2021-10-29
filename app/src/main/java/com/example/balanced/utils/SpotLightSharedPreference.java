package com.example.balanced.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpotLightSharedPreference {


    private String key = "spotlight";

    private String TUTORIAL_LOBBY = "TUTORIAL_LOBBY";

    private Context context;

    private SharedPreferences rootSP;

    private SharedPreferences.Editor editor;

    public SpotLightSharedPreference(Context context){
        this.context = context;
        this.rootSP = context.getSharedPreferences(this.key, Context.MODE_PRIVATE);
        this.editor = rootSP.edit();
    }

    public void disableTutorialLobby(){
        editor.putBoolean(this.TUTORIAL_LOBBY, false);
        this.save();
    }

    public boolean getTutorialLobby(){
        return true;
    }

    private void save(){
        editor.apply();
    }
}
