package com.uc3m.p4r4d0x.emergapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uc3m.p4r4d0x.emergapp.R;

/**
 * Created by Alvaro Loarte on 19/07/16.
 * Desc: this class extends a fragment from a TabLayout of Achievements activity
 *       Gets the forth fragment (tab) of the TabLayout: Quest Achievements
 *
 */
public class AchievementFragment4 extends Fragment {
    View mView;
    String [][] data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_achievem_4,container,false);
        this.mView = view;
        setQuestIntoViews();
        return view;
    }

    /*
    * Desc:  Set the data from Activity into the array
    * */
    public void setArgumentsToFragment(String [][] data){

        this.data=data;
    }
    /*
    * Desc:  Using the data obtained from the activity, change the TextViews with the data retrieved from DDBB
    * */
    public void setQuestIntoViews(){


        changeText(data[0][1], R.id.tvQuestContentQ1);
        changeText(data[0][2]+" AP", R.id.tvQuestAPQ1);
        changeText(data[0][3]+" XP", R.id.tvQuestXPQ1);

        changeText(data[1][0], R.id.tvQuestContentDescriptionQ2);
        changeText(data[1][1], R.id.tvQuestContentUbicationQ2);
        changeText(data[1][2]+" AP", R.id.tvQuestAPQ2);
        changeText(data[1][3]+" XP", R.id.tvQuestXPQ2);


    }

    /*
    * Desc:  change the text on the text view on the fragment
    * Param: String with the text to change and a int with the id of the text view
    * */
    public void changeText(String text, int idTextView)
    {
        //Get the text view
        TextView fragmentTextView;
        fragmentTextView = (TextView) mView.findViewById(idTextView);
     //Set text to it
        fragmentTextView.setText(text);

    }
}
