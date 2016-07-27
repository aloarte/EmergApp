package com.uc3m.p4r4d0x.emergapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uc3m.p4r4d0x.emergapp.helpers.database.DBAchievementsManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBAvatarsManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBTitlesManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBUserManager;

import java.util.ArrayList;

public class AccountConfigurationActivity extends AppCompatActivity {

    //Info to use shared preferences to have a session
    final String MyPREFERENCES = "userPreferences";
    SharedPreferences sharedpreferences;


    Spinner colorOptionsSpinner;
    String colorSelected="";
    int colorSelectedInt=0;

    String [][] colors = new String[][] {
                                            {"Default","#009688","#26a69a"},
                                            {"Red"    ,"#d32f2f","#ffcdd2"},
                                            {"Blue"   ,"#303f9f","#3f51b5"},
                                            {"Green"  ,"#43a047","#4caf50"},
                                            {"Yellow" ,"#ffa000","#ffc107"},
                                            {"Pink"   ,"#e91e63","#f8bbd0"},
                                            {"Grey"   ,"#bdbdbd","#e0e0e0"}
    };

    /*
    * Desc: method overrided from AppCompatActivity
    *       this method is called when activity starts
    *       Initialize all the neccessary parts of the main screen
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_configuration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAC);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Load the toolbar
        loadToolbar();
        //Load the color
        loadColor();

        //Get the spinner
        colorOptionsSpinner = (Spinner) findViewById(R.id.spinnerAccountConfiguration);

        //Build an array list with the elements to fill the spinner
        ArrayList<ItemSpinnerData> alSpinnerData= new ArrayList<>();
        //Add all the elements
        alSpinnerData.add(new ItemSpinnerData("Default",R.drawable.defaultsquare));
        alSpinnerData.add(new ItemSpinnerData("Red",R.drawable.redsquare));
        alSpinnerData.add(new ItemSpinnerData("Blue",R.drawable.bluesquare));
        alSpinnerData.add(new ItemSpinnerData("Green",R.drawable.greensquare));
        alSpinnerData.add(new ItemSpinnerData("Yellow",R.drawable.yellowsquare));
        alSpinnerData.add(new ItemSpinnerData("Pink",R.drawable.pinksquare));
        alSpinnerData.add(new ItemSpinnerData("Grey",R.drawable.greysquare));



        //Build the spinner adapter using the array list
        SpinnerAdapter adapter = new SpinnerAdapter (this,R.layout.spinner_layout,R.id.tvSpinnerAccountConfiguration,alSpinnerData);
        //Set the adapter to the spinner to set the info in the spinner
        colorOptionsSpinner.setAdapter(adapter);

        colorOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                colorSelected=((TextView)view.findViewById(R.id.tvSpinnerAccountConfiguration)).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    /*
    * Desc: method overrided from AppCompatActivity
    *       this method is called when activity starts
    *       Prepare the toolbar menu
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_emergency_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    * Desc: method overrided from AppCompatActivity
    *       this method is called when activity starts
    *       Prepare the elements on the toolbar menu
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent;

        switch (item.getItemId()) {
            case R.id.action_close_session:
                Toast.makeText(this, getText(R.string.action_close_session), Toast.LENGTH_SHORT).show();
                performLogout();
                return true;
            case R.id.action_acount_configuration:
                myIntent= new Intent(getApplicationContext(), AccountConfigurationActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_profile:
                myIntent= new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_ranking:
                myIntent= new Intent(getApplicationContext(), RankingActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_achievements:
                myIntent= new Intent(getApplicationContext(), AchievementsActivity.class);
                startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    /*
    * Desc: on click method to change for the button to change the app color style
    *
    * */
    public void onClickChangeColor(View v){
        Log.d("ALR","Button pressed. Color: "+ colorSelected);

        //if the color is not chosen , skip
        if(colorSelected.compareTo("")!=0){
            String[] colorsToSet=getColorsCodes(colorSelected);


            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("colorprimary", colorsToSet[0]);
            editor.putString("colorsecondary",colorsToSet[1]);
            editor.commit();

            //insert on the database that color
            insertColorOnDDBB(colorSelectedInt);

            loadColor();
        }


    }

    /*
    * Desc: auxiliar function to retrieve the hex codes for each color
    *
    * */
    public String[] getColorsCodes(String color){
        String[] colorsToReturn = new String []{"",""};

        //Search for the requested color on the array
        for(int i=0; i<colors.length;i++){
            //When matches, fill the return string with the codes
            if(color.compareTo(colors[i][0])==0){
                colorsToReturn[0]=colors[i][1];
                colorsToReturn[1]=colors[i][2];
                colorSelectedInt=i;
                break;
            }
        }

        if(colorsToReturn[0].compareTo("")==0 || colorsToReturn[1].compareTo("")==0){
            colorsToReturn[0]="#009688";
            colorsToReturn[1]="#26a69a";
        }
        return colorsToReturn;

    }

    /*
    * Desc: insert the color ID to the DDBB of the current user
    *
    * */
    public void insertColorOnDDBB(int color){
        String username = sharedpreferences.getString("username", "default");
        Long result=99L;
        if(username.compareTo("default")!=0){
            DBUserManager managerDB = new DBUserManager(this);
            result=managerDB.upgradeUserColor(username, color);

            DBTitlesManager titleDB = new DBTitlesManager(this);
            DBAchievementsManager achievementsDB=new DBAchievementsManager(this);
            DBAvatarsManager avatarsDB= new DBAvatarsManager(this);

            int r1,r2,r3;
            Cursor resultQuery= titleDB.selecttitle("tBegginer",username);
            String titleid,Suser,s1;
            //If the title exists
            if(resultQuery.moveToFirst()==true) {

                //Get the id to make the update
                titleid = resultQuery.getString(resultQuery.getColumnIndex(DBTitlesManager.TT_NAME_ID));
                Suser = resultQuery.getString(resultQuery.getColumnIndex(DBTitlesManager.TT_USER_NAME));
                Log.d("ALR","TITULO " + titleid+" seleccionado, usuario: " +Suser);
            }


            resultQuery= achievementsDB.selectAchievement("aExpertMeta", username);
            //If the title exists
            if(resultQuery.moveToFirst()==true) {

                //Get the id to make the update
                titleid = resultQuery.getString(resultQuery.getColumnIndex(DBAchievementsManager.TA_NAME_ID));
                s1      = resultQuery.getString(resultQuery.getColumnIndex(DBAchievementsManager.TA_NAME_ACHIEVEMENT));
                r1      = resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_PROGRESS));
                r2      = resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_REWARD_AP));
                r3      = resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_REWARD_XP));
                Suser = resultQuery.getString(resultQuery.getColumnIndex(DBAchievementsManager.TA_USER_NAME));
                Log.d("ALR","LOGRO " + titleid+" seleccionado, usuario: " +Suser + " DATA:"+s1+","+r1+","+r2+","+r3);
            }



            resultQuery= titleDB.selecttitle("avInitial",username);
            //If the title exists
            if(resultQuery.moveToFirst()==true) {

                //Get the id to make the update
                titleid = resultQuery.getString(resultQuery.getColumnIndex(DBAvatarsManager.TAV_NAME_ID));
                s1 =    resultQuery.getString(resultQuery.getColumnIndex(DBAvatarsManager.TAV_SOURCE));
                Suser = resultQuery.getString(resultQuery.getColumnIndex(DBAvatarsManager.TAV_USER_NAME));
                Log.d("ALR","AVATAR " + titleid+" seleccionado, usuario: " +Suser+" Source: "+s1);
            }




        }
    }

    public void loadToolbar(){
        //Get sharedpreferences item and the username asociated
        sharedpreferences                  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username                    = sharedpreferences.getString("username", "default");

        //Check the username
        if(username.compareTo("default")==0){
            //If is empty (error) do nothing
        }
        else{
            //Put username in the toolbar text view
            TextView tvToolbarUser         = (TextView) findViewById(R.id.tvToolbarUser);
            tvToolbarUser.setText(username);

        }
        DBUserManager managerDB                = new DBUserManager(this);
        //Select the user
        Cursor resultQuery                 = managerDB.selectUser(username);
        //If the user exists
        if(resultQuery.moveToFirst()==true){
            //Set the level
            String level                      = resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_LEVEL));
            TextView tvToolbarLevel = (TextView) findViewById(R.id.tvToolbarLevel);
            tvToolbarLevel.setText(level);
            //Set the title
            String title                      = resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_TITLE));
            TextView tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
            tvToolbarTitle.setText(title);
            //Set the AP points
            int APpoints                     = resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_AP_POINTS));
            TextView tvToolbarAP = (TextView) findViewById(R.id.tvToolbarCurrentAP);
            tvToolbarAP.setText(""+APpoints);
            //Set the XP points
            int XPpoints                     = resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_XP_POINTS));
            TextView tvToolbarXPMax = (TextView) findViewById(R.id.tvToolBarNextLevelXP);
            TextView tvToolbarXP = (TextView) findViewById(R.id.tvToolbarCurrentXP);

            switch(level){
                case "Traveler":
                    tvToolbarXPMax.setText(""+50);
                    tvToolbarXP.setText(""+XPpoints);
                    break;
                case "Veteran":
                    tvToolbarXPMax.setText(""+150);
                    tvToolbarXP.setText(""+XPpoints);
                    break;
                case "Champion":
                    tvToolbarXPMax.setText(""+300);
                    tvToolbarXP.setText(""+XPpoints);
                    break;
                case "Hero":
                    tvToolbarXPMax.setText(""+500);
                    tvToolbarXP.setText(""+XPpoints);
                    break;
                case "Legend":
                    tvToolbarXPMax.setText(""+999);
                    tvToolbarXP.setText(""+XPpoints);
                    break;
                default:
                    break;

            }

            int avatar = resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_AVATAR));
            ImageView fragmentImageView = (ImageView) findViewById(R.id.ivLogoToolbar);
            //Set text to it
            fragmentImageView.setImageResource(avatar);
        }
    }

    /*
    * Desc: performs a logout from the current logged user
    *
    * */
    public void performLogout(){

        //Remove from the shared preferences the username
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("username");
        editor.remove("colorprimary");
        editor.remove("colorsecondary");
        editor.commit();

        //Create and launch login activity
        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(myIntent);
    }

    /*
    * Desc: load the color selected by the user
    *
    * */
    public void loadColor(){

        //Check if there is any user logged into the aplication checking shared preferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //Get the colors codes
        String primaryColor = sharedpreferences.getString("colorprimary", "default");
        String secondaryColor = sharedpreferences.getString("colorsecondary", "default");
        //if there is no color
        if(primaryColor.compareTo("default")==0 || secondaryColor.compareTo("default")==0){
            //Load default color
        }
        else{
            //Load the new color
            Toolbar t= (Toolbar) findViewById(R.id.toolbarAC);
            t.setBackgroundColor(Color.parseColor(primaryColor));

        }
    }
}
