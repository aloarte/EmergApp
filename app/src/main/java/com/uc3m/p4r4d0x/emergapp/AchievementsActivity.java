package com.uc3m.p4r4d0x.emergapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uc3m.p4r4d0x.emergapp.fragments.AchievementFragment1;
import com.uc3m.p4r4d0x.emergapp.fragments.AchievementFragment2;
import com.uc3m.p4r4d0x.emergapp.fragments.AchievementFragment3;
import com.uc3m.p4r4d0x.emergapp.fragments.AchievementFragment4;
import com.uc3m.p4r4d0x.emergapp.fragments.RankFragment2;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBAchievementsManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBQuestsManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBUserManager;

public class AchievementsActivity extends AppCompatActivity {

    //Info to use shared preferences to have a session
    final String MyPREFERENCES = "userPreferences";
    SharedPreferences sharedpreferences;

    TabLayout tabLayoutAchievements;
    ViewPager viewPagerAchievements;

    /*Inner class implemented for the TabLayout*/
    private class CustomAdapter extends FragmentPagerAdapter {
        //String array with the fragments names
        private String fragmentsNames[] = new String[]{"Novel", "Expert", "Secret", "Quests"};

        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            AchievementFragment1 achievementFragment1;
            AchievementFragment2 achievementFragment2;
            AchievementFragment3 achievementFragment3;
            AchievementFragment4 achievementFragment4;
            Fragment retFragment;
            //For each position create the correspond fragment, created by a fragment class
            switch (position) {
                case 0:
                    achievementFragment1= new AchievementFragment1();
                    loadNovelAchievements(achievementFragment1);
                    retFragment=achievementFragment1;
                    return retFragment;
                case 1:
                    achievementFragment2= new AchievementFragment2();
                    loadExpertAchievements(achievementFragment2);
                    retFragment=achievementFragment2;
                    return retFragment;
                case 2:
                    achievementFragment3= new AchievementFragment3();
                    loadSecretAchievements(achievementFragment3);
                    retFragment=achievementFragment3;
                    return retFragment;
                case 3:
                    achievementFragment4= new AchievementFragment4();
                    loadQuests(achievementFragment4);
                    retFragment=achievementFragment4;
                    return retFragment;
                default:
                    return null;
            }
        }

        //Return the size of the array
        @Override
        public int getCount() {
            return fragmentsNames.length;
        }

        //Return the name of each fragment
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsNames[position];
        }
    }



    /*
        * Desc: method overrided from AppCompatActivity
        *       this method is called when activity starts
        *       Initialize all the neccessary parts of the main screen
        * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_achievements);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarA);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Load the toolbar
        loadToolbar();

        //FOR THE TAB LAYOUT
        //Get the viewPager
        viewPagerAchievements = (ViewPager) findViewById(R.id.viewPagerAchievements);
        viewPagerAchievements.setAdapter(new CustomAdapter(getSupportFragmentManager(), getApplicationContext()));

        //Get the tabLayout and set the viewPager
        tabLayoutAchievements = (TabLayout) findViewById(R.id.tabLayoutAchievements);
        tabLayoutAchievements.setupWithViewPager(viewPagerAchievements);

        //Override onTabSelected methods to let the tab respond with the viewPager
        tabLayoutAchievements.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerAchievements.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPagerAchievements.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPagerAchievements.setCurrentItem(tab.getPosition());
            }
        });

        //Load the color
        loadColor();
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
                performLogout();
                return true;
            case R.id.action_acount_configuration:
                if(checkUnlockAcountConfiguration()){
                    myIntent= new Intent(getApplicationContext(), AccountConfigurationActivity.class);
                    startActivity(myIntent);
                }
                else{
                    Toast.makeText(this, "This feature is locked", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public void onBackPressed() {
    }

    /*
     * Desc: load the data into the toolbar
     * */
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
     * Desc: load the color on the toolbar and other elements
     * */
    public void loadColor(){

        //Check if there is any user logged into the aplication checking shared preferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String primaryColor = sharedpreferences.getString("colorprimary", "default");
        String secondaryColor = sharedpreferences.getString("colorsecondary", "default");
        //if there is no color
        if(primaryColor.compareTo("default")==0 || secondaryColor.compareTo("default")==0){
            //Load default color
        }
        else{

            //Load the new color
            Toolbar t= (Toolbar) findViewById(R.id.toolbarA);
            t.setBackgroundColor(Color.parseColor(primaryColor));
            tabLayoutAchievements.setBackgroundColor(Color.parseColor(secondaryColor));

        }
    }

    /*
        * Desc: on click function to logout from the aplication
        * */
    public void performLogout(){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AchievementsActivity.this);
        View layView = (LayoutInflater.from(AchievementsActivity.this)).inflate(R.layout.confirm_logout, null);
        alertBuilder.setView(layView);
        alertBuilder.setCancelable(true)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Remove from the shared preferences the username
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.remove("username");
                        editor.remove("colorprimary");
                        editor.remove("colorsecondary");
                        editor.commit();

                        Toast.makeText(getApplicationContext(), "Session Closed", Toast.LENGTH_SHORT).show();
                        //Create and launch login activity
                        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(myIntent);
                    }
                })
        ;
        Dialog dialog = alertBuilder.create();
        dialog.show();
    }

    /*
    * Desc: load the achievements stored on the DDBB for each user, setting the data into a array
    *       and passing it to a fragment
    * Param:AchievementFragment1 with the fragment that will be created
    * */
    public void loadNovelAchievements(AchievementFragment1 achievementFragment){

        //Get sharedpreferences item and the username asociated
        sharedpreferences                  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username                    = sharedpreferences.getString("username", "default");
        //Check the username
        if(username.compareTo("default")==0){
            //If is empty (error) do nothing
        }
        else {
            int obtainedAux=0;
            //String array to save all data recovered from the DDBB
            String [][] data = new String [6][3];
            //Open the DDBB manager
            DBAchievementsManager managerDBAchievements = new DBAchievementsManager(this);
            //Select the users ordered by XP points
            Cursor resultQuery;
            //iterate each user to save data into the array
            int i,color=-1;
            for(i=0;i<6;i++){
                switch (i){
                    case 0:
                        resultQuery = managerDBAchievements.selectAchievement("aNovelMeta",username);
                        break;
                    case 1:
                        resultQuery = managerDBAchievements.selectAchievement("aNovel1",username);
                        break;
                    case 2:
                        resultQuery = managerDBAchievements.selectAchievement("aNovel2",username);
                        break;
                    case 3:
                        resultQuery = managerDBAchievements.selectAchievement("aNovel3",username);
                        break;
                    case 4:
                        resultQuery = managerDBAchievements.selectAchievement("aNovel4",username);
                        break;
                    case 5:
                        resultQuery = managerDBAchievements.selectAchievement("aNovel5",username);
                        break;
                    default:
                        resultQuery = managerDBAchievements.selectAchievement("aNovelMeta",username);
                        break;
                }
                if(resultQuery.moveToFirst()==true) {
                    data[i][0] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_PROGRESS));
                    data[i][1] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_PROGRESS_MAX));
                    data[i][2] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_COMPLETED));
                }
                else{
                    data[i][0]="0";
                    data[i][1]="0";
                    data[i][2]="0";
                }
            }
            //Get the color selected by the user
            DBUserManager dbUserManager = new DBUserManager (this);
            Cursor resultQueryUser = dbUserManager.selectUser(username);
            if(resultQueryUser.moveToFirst()==true) {
                color = resultQueryUser.getInt(resultQueryUser.getColumnIndex(DBUserManager.TU_COLOR));
            }
            //Set the data retrieved into the fragment view
            achievementFragment.setArgumentsToFragment(data, i,color);

        }
    }

    /*
    * Desc: load the achievements stored on the DDBB for each user, setting the data into a array
    *       and passing it to a fragment
    * Param:AchievementFragment2 with the fragment that will be created
    * */
    public void loadExpertAchievements(AchievementFragment2 achievementFragment){

        //Get sharedpreferences item and the username asociated
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username                    = sharedpreferences.getString("username", "default");
        //Check the username
        if(username.compareTo("default")==0){
            //If is empty (error) do nothing
        }
        else {
            int obtainedAux=0;
            //String array to save all data recovered from the DDBB
            String [][] data = new String [7][3];
            //Open the DDBB manager
            DBAchievementsManager managerDBAchievements = new DBAchievementsManager(this);
            //Select the users ordered by XP points
            Cursor resultQuery;
            //iterate each user to save data into the array
            int i,color=-1;
            for(i=0;i<7;i++){
                switch (i){
                    case 0:
                        resultQuery = managerDBAchievements.selectAchievement("aExpertMeta",username);
                        break;
                    case 1:
                        resultQuery = managerDBAchievements.selectAchievement("aExpert1",username);
                        break;
                    case 2:
                        resultQuery = managerDBAchievements.selectAchievement("aExpert2",username);
                        break;
                    case 3:
                        resultQuery = managerDBAchievements.selectAchievement("aExpert3",username);
                        break;
                    case 4:
                        resultQuery = managerDBAchievements.selectAchievement("aExpert4",username);
                        break;
                    case 5:
                        resultQuery = managerDBAchievements.selectAchievement("aExpert5",username);
                        break;
                    case 6:
                        resultQuery = managerDBAchievements.selectAchievement("aExpert6",username);
                        break;
                    default:
                        resultQuery = managerDBAchievements.selectAchievement("aExpertMeta",username);
                        break;
                }
                if(resultQuery.moveToFirst()==true) {
                    data[i][0] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_PROGRESS));
                    data[i][1] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_PROGRESS_MAX));
                    data[i][2] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_COMPLETED));
                }
                else{
                    data[i][0]="0";
                    data[i][1]="0";
                    data[i][2]="0";
                }

            }
            //Get the color selected by the user
            DBUserManager dbUserManager = new DBUserManager (this);
            Cursor resultQueryUser = dbUserManager.selectUser(username);
            if(resultQueryUser.moveToFirst()==true) {
                color = resultQueryUser.getInt(resultQueryUser.getColumnIndex(DBUserManager.TU_COLOR));
            }
            //Set the data retrieved into the fragment view
            achievementFragment.setArgumentsToFragment(data,i,color);

        }
    }

    /*
    * Desc: load the achievements stored on the DDBB for each user, setting the data into a array
    *       and passing it to a fragment
    * Param:AchievementFragment3 with the fragment that will be created
    * */
    public void loadSecretAchievements(AchievementFragment3 achievementFragment){

        //Get sharedpreferences item and the username asociated
        sharedpreferences                  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username                    = sharedpreferences.getString("username", "default");
        //Check the username
        if(username.compareTo("default")==0){
            //If is empty (error) do nothing
        }
        else {
            int obtainedAux=0;
            //String array to save all data recovered from the DDBB
            String [][] data = new String [6][3];
            //Open the DDBB manager
            DBAchievementsManager managerDBAchievements = new DBAchievementsManager(this);
            //Select the users ordered by XP points
            Cursor resultQuery;
            //iterate each user to save data into the array
            int i,color=-1;
            for(i=0;i<6;i++){
                switch (i){
                    case 0:
                        resultQuery = managerDBAchievements.selectAchievement("aSecretMeta",username);
                        break;
                    case 1:
                        resultQuery = managerDBAchievements.selectAchievement("aSecret1",username);
                        break;
                    case 2:
                        resultQuery = managerDBAchievements.selectAchievement("aSecret2",username);
                        break;
                    case 3:
                        resultQuery = managerDBAchievements.selectAchievement("aSecret3",username);
                        break;
                    case 4:
                        resultQuery = managerDBAchievements.selectAchievement("aSecret4",username);
                        break;
                    case 5:
                        resultQuery = managerDBAchievements.selectAchievement("aSecret5",username);
                        break;
                    default:
                        resultQuery = managerDBAchievements.selectAchievement("aSecretMeta",username);
                        break;
                }
                if(resultQuery.moveToFirst()==true) {
                    data[i][0] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_PROGRESS));
                    data[i][1] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_PROGRESS_MAX));
                    data[i][2] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBAchievementsManager.TA_COMPLETED));
                }
                else{
                    data[i][0]="0";
                    data[i][1]="0";
                    data[i][2]="0";
                }


            }
            //Get the color selected by the user
            DBUserManager dbUserManager = new DBUserManager (this);
            Cursor resultQueryUser = dbUserManager.selectUser(username);
            if(resultQueryUser.moveToFirst()==true) {
                color = resultQueryUser.getInt(resultQueryUser.getColumnIndex(DBUserManager.TU_COLOR));
            }
            //Set the data retrieved into the fragment view
            achievementFragment.setArgumentsToFragment(data,i,color);

        }
    }

    /*
    * Desc: load the quests recovered from the DDBB, setting the data into a array
    *       and passing it to a fragment
    * Param:AchievementFragment4 with the fragment that will be created
    * */
    public void loadQuests(AchievementFragment4 achievementFragment){

        //Get sharedpreferences item and the username asociated
        sharedpreferences                  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username                    = sharedpreferences.getString("username", "default");
        //Check the username
        if(username.compareTo("default")==0){
            //If is empty (error) do nothing
        }
        else {
            int obtainedAux=0;
            //String array to save all data recovered from the DDBB
            String [][] data = new String [2][4];
            //Open the DDBB manager
            DBQuestsManager managerDBQuests = new DBQuestsManager(this);
            DBUserManager managerDB = new DBUserManager(this);

            //Select the user
            Cursor resultQueryUser = managerDB.selectUser(username);
            //If the user exists
            if (resultQueryUser.moveToFirst() == true) {

                Cursor resultQuery = managerDBQuests.selectQuest("Quest1", resultQueryUser.getString(resultQueryUser.getColumnIndex(DBUserManager.TU_LEVEL)));
                if(resultQuery.moveToFirst()==true) {
                    data[0][0] = resultQuery.getString(resultQuery.getColumnIndex(DBQuestsManager.TQ_NAME));
                    data[0][1] = resultQuery.getString(resultQuery.getColumnIndex(DBQuestsManager.TQ_DESCRIPTION));
                    data[0][2] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBQuestsManager.TQ_XP_REWARD));
                    data[0][3] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBQuestsManager.TQ_AP_REWARD));
                }
                else {
                    data[0][0] = "-";
                    data[0][1] = "-";
                    data[0][2] = "-";
                    data[0][3] = "-";
                }

                resultQuery = managerDBQuests.selectQuest("Quest2", resultQueryUser.getString(resultQueryUser.getColumnIndex(DBUserManager.TU_LEVEL)));
                if(resultQuery.moveToFirst()==true) {
                    data[1][0] = resultQuery.getString(resultQuery.getColumnIndex(DBQuestsManager.TQ_NAME));
                    data[1][1] = resultQuery.getString(resultQuery.getColumnIndex(DBQuestsManager.TQ_DESCRIPTION));
                    data[1][2] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBQuestsManager.TQ_XP_REWARD));
                    data[1][3] = "" + resultQuery.getInt(resultQuery.getColumnIndex(DBQuestsManager.TQ_AP_REWARD));
                }
                else {
                    data[1][0] = "-";
                    data[1][1] = "-";
                    data[1][2] = "-";
                    data[1][3] = "-";
                }
                achievementFragment.setArgumentsToFragment(data);

            }
            else{
                achievementFragment.setArgumentsToFragment(new String[][]{{"","","",""},{"","","",""}});
            }
        }
    }

    /*
    * Desc: Check from the DDBB if the user can select his account configuration
    * */
    public boolean checkUnlockAcountConfiguration(){
        //Get sharedpreferences item and the username asociated
        sharedpreferences                  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username                    = sharedpreferences.getString("username", "default");
        boolean retValue=false;
        //Get the linear layout

        DBUserManager managerDBUser = new DBUserManager(this);
        //Make que query
        Cursor resultQuery = managerDBUser.selectUser(username);
        //Check if the title selection is unlocked
        if(resultQuery.moveToFirst()==true) {
            if (resultQuery.getInt(resultQuery.getColumnIndex(managerDBUser.TU_MODIFY_TITLE)) == 1) {
                retValue = true;
            } else {
                retValue = false;
            }
        }

        return retValue;
    }

    /*
   * Desc: on click method to navegate from toolbar to profile activity
   * */
    public void onClickChangeProfileActivity(View v){
        Intent myIntent= new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(myIntent);
    }

    /*
   * Desc: on click method to navegate from toolbar to acount configuration activity
   * */
    public void onClickChangeACActivity(View v){
        if(checkUnlockAcountConfiguration()){
            Intent myIntent= new Intent(getApplicationContext(), AccountConfigurationActivity.class);
            startActivity(myIntent);
        }
        else{
            Toast.makeText(this, "This feature is locked", Toast.LENGTH_SHORT).show();
        }
    }
}
