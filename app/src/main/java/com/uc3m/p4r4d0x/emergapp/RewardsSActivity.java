package com.uc3m.p4r4d0x.emergapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.uc3m.p4r4d0x.emergapp.helpers.database.DBAvatarsManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBTitlesManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBUserManager;

public class RewardsSActivity extends AppCompatActivity {

    //Info to use shared preferences to have a session
    final String MyPREFERENCES = "userPreferences";
    SharedPreferences sharedpreferences;

    LinearLayout[] llElementsOnShop = new LinearLayout[9];

    int bElementToPurchase=-1;
    boolean purchaseItem=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_rewards_s);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRS);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //Load the toolbar
        loadToolbar();
        //Load the color
        loadColor();

        loadNotificationQuests();

        llElementsOnShop[0]= (LinearLayout) findViewById(R.id.llRewardS1);
        llElementsOnShop[1]= (LinearLayout) findViewById(R.id.llRewardS2);
        llElementsOnShop[2]= (LinearLayout) findViewById(R.id.llRewardS3);
        llElementsOnShop[3]= (LinearLayout) findViewById(R.id.llRewardS4);
        llElementsOnShop[4]= (LinearLayout) findViewById(R.id.llRewardS5);
        llElementsOnShop[5]= (LinearLayout) findViewById(R.id.llRewardS6);
        llElementsOnShop[6]= (LinearLayout) findViewById(R.id.llRewardS7);
        llElementsOnShop[7]= (LinearLayout) findViewById(R.id.llRewardS8);
        llElementsOnShop[8]= (LinearLayout) findViewById(R.id.llRewardS9);


        //Load the progress
        loadProgressUnlocked();
        changeColorOnScreen();
        reloadUserAPPoints();

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
            case R.id.action_rewards:
                myIntent= new Intent(getApplicationContext(), RewardsSActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_quest:
                onClickShowQuest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
    }

    /*
    * Desc: change the color to the header layout to the selected color
    * */
    public void changeColorOnScreen(){
        //Check if there is any user logged into the aplication checking shared preferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String primaryColor = sharedpreferences.getString("colorprimary", "default");
        String secondaryColor = sharedpreferences.getString("colorsecondary", "default");

        //if there is no color
        if(primaryColor.compareTo("default")==0 || secondaryColor.compareTo("default")==0){
            //Load default color
        }
        else{
            LinearLayout llToColor = (LinearLayout) findViewById(R.id.llHeaderRewardS);
            llToColor.setBackgroundColor(Color.parseColor(secondaryColor));
        }

    }

    /*
    * Desc:  Change the image view of any element to their completed status, loading the color chosen by the user
    * */
    public void changeCompletedReward(int idImageView){
        //Get sharedpreferences item and the username asociated
        sharedpreferences                  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username                    = sharedpreferences.getString("username", "default");

        DBUserManager managerDB                = new DBUserManager(this);
        //Select the user
        Cursor resultQuery                 = managerDB.selectUser(username);
        //If the user exists
        if(resultQuery.moveToFirst()==true) {
            //Set the level
            int color = resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_COLOR));

            int resourceID;
            switch (color) {
                //DefaultColor
                case 0:
                    resourceID = R.mipmap.doneicon_ereporter;
                    break;
                //Red
                case 1:
                    resourceID = R.mipmap.doneicon_red;
                    break;
                //Blue
                case 2:
                    resourceID = R.mipmap.doneicon_blue;
                    break;
                //Green
                case 3:
                    resourceID = R.mipmap.doneicon_green;
                    break;
                //Purple
                case 4:
                    resourceID = R.mipmap.doneicon_purple;
                    break;
                //Yellow
                case 5:
                    resourceID = R.mipmap.doneicon_yellow;
                    break;
                //Pink
                case 6:
                    resourceID = R.mipmap.doneicon_pink;
                    break;
                //Grey
                case 7:
                    resourceID = R.mipmap.doneicon_grey;
                    break;
                default:
                    resourceID = R.mipmap.doneicon;
                    break;
            }

            //Get the text view
            ImageView ivDone;
            ivDone = (ImageView) findViewById(idImageView);
            //Set text to it
            ivDone.setImageResource(resourceID);
        }

    }

    /*
    * Load the shop content into the activity
    * */
    public void loadProgressUnlocked(){
        String username = sharedpreferences.getString("username", "default");
        DBUserManager managerUsersDB      = new DBUserManager(this);
        Cursor resultQuery = managerUsersDB.selectUser(username);
        //Check if the title selection is unlocked
        if(resultQuery.moveToFirst()) {
            //Get the strings with the info of the unlocked elements and its views
            String elementsUnlocked         = resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_ELEMENTS_UNLOCKED));
            String elementsUnlockedToView         = resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_ELEMENTS_UNLOCKED_VIEW));
            //Cast them into a boolean array
            boolean[] elementsUnlockedArray = getBooleanFromString(elementsUnlocked);
            boolean[] elementsUnlockedViewArray = getBooleanFromString(elementsUnlockedToView);
            //Iterate to load each element, changin its view if necessary, and their status to completed
            for(int i=0;i<9;i++){
                switch(i){
                    case 0:
                        if(elementsUnlockedArray[i]) {
                            changeCompletedReward(R.id.ivRewardCompleted1);
                            changeCompletedButton(R.id.bReward1, elementsUnlocked, 1);
                        }
                        if(elementsUnlockedViewArray[i]){
                            setElementToVisible(R.id.llRewardS1);
                        }
                        break;
                    case 1:
                        if(elementsUnlockedArray[i]) {
                            changeCompletedReward(R.id.ivRewardCompleted2);
                            changeCompletedButton(R.id.bReward2,elementsUnlocked,2);
                        }
                        if(elementsUnlockedViewArray[i]){
                            setElementToVisible(R.id.llRewardS2);
                        }
                        break;
                    case 2:
                        if(elementsUnlockedArray[i]) {
                            changeCompletedReward(R.id.ivRewardCompleted3);
                            changeCompletedButton(R.id.bReward3, elementsUnlocked, 3);
                        }
                        if(elementsUnlockedViewArray[i]){
                            setElementToVisible(R.id.llRewardS3);
                        }
                        break;
                    case 3:
                        if(elementsUnlockedArray[i]) {
                            changeCompletedReward(R.id.ivRewardCompleted4);
                            changeCompletedButton(R.id.bReward4, elementsUnlocked, 4);
                        }
                        if(elementsUnlockedViewArray[i]){
                            setElementToVisible(R.id.llRewardS4);
                        }
                        break;
                    case 4:
                        if(elementsUnlockedArray[i]) {
                            changeCompletedReward(R.id.ivRewardCompleted5);
                            changeCompletedButton(R.id.bReward5, elementsUnlocked, 5);
                        }
                        if(elementsUnlockedViewArray[i]){
                            setElementToVisible(R.id.llRewardS5);
                        }
                        break;
                    case 5:
                        if(elementsUnlockedArray[i]) {
                            changeCompletedReward(R.id.ivRewardCompleted6);
                            changeCompletedButton(R.id.bReward6, elementsUnlocked, 6);
                        }
                        if(elementsUnlockedViewArray[i]){
                            setElementToVisible(R.id.llRewardS6);
                        }
                        break;
                    case 6:
                        if(elementsUnlockedArray[i]) {
                            changeCompletedReward(R.id.ivRewardCompleted7);
                            changeCompletedButton(R.id.bReward7, elementsUnlocked, 7);
                        }
                        if(elementsUnlockedViewArray[i]){
                            setElementToVisible(R.id.llRewardS7);
                        }
                        break;
                    case 7:
                        if(elementsUnlockedArray[i]) {
                            changeCompletedReward(R.id.ivRewardCompleted8);
                            changeCompletedButton(R.id.bReward8, elementsUnlocked, 8);
                        }
                        if(elementsUnlockedViewArray[i]){
                            setElementToVisible(R.id.llRewardS8);

                        }
                        break;
                    case 8:
                        if(elementsUnlockedArray[i]) {

                            changeCompletedReward(R.id.ivRewardCompleted9);
                            changeCompletedButton(R.id.bReward9, elementsUnlocked, 9);
                        }
                        if(elementsUnlockedViewArray[i]){
                            setElementToVisible(R.id.llRewardS9);
                        }
                        break;
                }
            }
        }
    }

    /*
    * Desc: load the user content into the toolbar
    *
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
    * Desc: load the selected color on the toolbar
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
            Toolbar t= (Toolbar) findViewById(R.id.toolbarRS);
            t.setBackgroundColor(Color.parseColor(primaryColor));

        }
    }

    /*
  * Desc: load the notification icon for the quests
  * */
    public void loadNotificationQuests(){
        //Get the number of notifications
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        int notifNumber   = sharedpreferences.getInt("quest_notifications", 0);
        boolean isQuestS  = sharedpreferences.getBoolean("questB", false);

        //Get the element to change it
        ImageView ivNotif = (ImageView) findViewById(R.id.ivQuestNotification);

        switch(notifNumber){
            case 0:
                ivNotif.setImageResource(R.mipmap.ic_quests);
                break;
            case 1:
                ivNotif.setImageResource(R.mipmap.ic_quests_1);
                break;
            case 2:
                ivNotif.setImageResource(R.mipmap.ic_quests_2);
                break;
            default:
                ivNotif.setImageResource(R.mipmap.ic_quests);
                break;
        }

        LinearLayout llImageProfile = (LinearLayout) findViewById(R.id.llImageProfile);
        LinearLayout llQuestActive = (LinearLayout) findViewById(R.id.llQuestActive);
        if(isQuestS){
            llImageProfile.setVisibility(View.GONE);
            llQuestActive.setVisibility(View.VISIBLE);
        }
        else{
            llImageProfile.setVisibility(View.VISIBLE);
            llQuestActive.setVisibility(View.GONE);
        }
    }

    /*
    * Desc: made visible one element (linearlayout) of the shop
    * */
    public void setElementToVisible(int llId){
        LinearLayout llToVisible = (LinearLayout) findViewById(llId);
        llToVisible.setVisibility(View.VISIBLE);
    }

    /*
    * Desc: Reload to the initial AP values into the header of the activity to show the AP prizes
    * */
    public void reloadUserAPPoints(){
        //Get sharedpreferences item and the username asociated
        sharedpreferences                  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username                    = sharedpreferences.getString("username", "default");
        boolean retValue=false;
        //Get the linear layout

        DBUserManager managerDBUser = new DBUserManager(this);
        //Make que query
        Cursor resultQuery = managerDBUser.selectUser(username);
        //Check if the title selection is unlocked
        if(resultQuery.moveToFirst()) {
            //Get the edit text of the screen
            TextView tvCurrentAp = (TextView) findViewById(R.id.tvRewardCurrentAP);
            TextView tvElementtAp = (TextView) findViewById(R.id.tvRewardElementAP);
            TextView tvTotalAp = (TextView) findViewById(R.id.tvRewardTotalAP);
            TextView tvElementtApS = (TextView) findViewById(R.id.tvRewardElementAPString);
            TextView tvTotalApS = (TextView) findViewById(R.id.tvRewardTotalAPString);
            //Get the user value
            int currentApValue=resultQuery.getInt(resultQuery.getColumnIndex(managerDBUser.TU_AP_POINTS));
            //Put the new values
            tvCurrentAp.setText(""+currentApValue);
            tvElementtAp.setText("");
            tvElementtAp.setTextColor(Color.BLACK);
            tvTotalAp.setText("");
            tvTotalAp.setTextColor(Color.BLACK);
            tvElementtApS.setVisibility(View.INVISIBLE);
            tvTotalApS.setVisibility(View.INVISIBLE);



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
        //Check if the avatar selection is unlocked
        if(resultQuery.moveToFirst()) {
            if (resultQuery.getInt(resultQuery.getColumnIndex(managerDBUser.TU_MODIFY_AVATAR)) == 1
                    ||
                    resultQuery.getInt(resultQuery.getColumnIndex(managerDBUser.TU_MODIFY_COLOR)) == 1
                    ) {
                retValue = true;
            } else {
                retValue = false;
            }
        }

        return retValue;
    }
    /*
     * Desc: on click function to logout from the aplication
     */
    public void performLogout(){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RewardsSActivity.this);
        View layView = (LayoutInflater.from(RewardsSActivity.this)).inflate(R.layout.confirm_logout, null);
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
     * Desc: on click method to navegate from toolbar to profile activity
     * */
    public void onClickChangeProfileActivity(View v){
        Intent myIntent= new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(myIntent);
    }

    /*
     * Desc: onClick method for each button to select the element to unlock
     * */
    public void onClickSelectItemToStore(View v){
        String username = sharedpreferences.getString("username", "default");
        DBUserManager managerUsersDB      = new DBUserManager(this);
        Cursor resultQuery = managerUsersDB.selectUser(username);
        //Check if the title selection is unlocked
        if(resultQuery.moveToFirst()) {
            String elementsUnlocked = resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_ELEMENTS_UNLOCKED));

            //Get the id of the pressed button
            int id = v.getId();
            //switch the id , select the item and set the result of the purchase on screen
            switch (id) {
                case R.id.bReward1:
                    if (!checkElementIsAlreadyUnlocked(elementsUnlocked, 1)) {
                        markElementSelected(1);
                        addAPDifferenceToScreen(75);
                        bElementToPurchase = R.id.bReward1;
                    }
                    break;
                case R.id.bReward2:
                    if (!checkElementIsAlreadyUnlocked(elementsUnlocked, 2)) {
                        markElementSelected(2);
                        addAPDifferenceToScreen(75);
                        bElementToPurchase = R.id.bReward2;
                    }
                    break;
                case R.id.bReward3:
                    if (!checkElementIsAlreadyUnlocked(elementsUnlocked, 3)) {
                        markElementSelected(3);
                        addAPDifferenceToScreen(50);
                        bElementToPurchase = R.id.bReward3;
                    }
                    break;
                case R.id.bReward4:
                    if (!checkElementIsAlreadyUnlocked(elementsUnlocked, 4)) {
                        markElementSelected(4);
                        addAPDifferenceToScreen(50);
                        bElementToPurchase = R.id.bReward4;
                    }
                    break;
                case R.id.bReward5:
                    if (!checkElementIsAlreadyUnlocked(elementsUnlocked, 5)) {
                        markElementSelected(5);
                        addAPDifferenceToScreen(20);
                        bElementToPurchase = R.id.bReward5;
                    }
                    break;
                case R.id.bReward6:
                    if (!checkElementIsAlreadyUnlocked(elementsUnlocked, 6)) {
                        markElementSelected(6);
                        addAPDifferenceToScreen(20);
                        bElementToPurchase = R.id.bReward6;
                    }
                    break;
                case R.id.bReward7:
                    if (!checkElementIsAlreadyUnlocked(elementsUnlocked, 7)) {
                        markElementSelected(7);
                        addAPDifferenceToScreen(30);
                        bElementToPurchase = R.id.bReward7;
                    }
                    break;
                case R.id.bReward8:
                    if (!checkElementIsAlreadyUnlocked(elementsUnlocked, 8)) {
                        markElementSelected(8);
                        addAPDifferenceToScreen(30);
                        bElementToPurchase = R.id.bReward8;
                    }
                    break;
                case R.id.bReward9:
                    if (!checkElementIsAlreadyUnlocked(elementsUnlocked, 9)) {
                        markElementSelected(9);
                        addAPDifferenceToScreen(40);
                        bElementToPurchase = R.id.bReward9;
                    }
                    break;
            }
        }
    }

    /*
     * Desc: onClick method for the BUY button.
     *       Take the selected element and unlock it
     * */
    public void onClickPurchaseElement(View v){
        String username = sharedpreferences.getString("username", "default");
        //Get the managers for the DDBB
        DBUserManager managerUsersDB      = new DBUserManager(this);
        DBAvatarsManager managerAvatarsDB = new DBAvatarsManager(this);
        DBTitlesManager managerTitlesDB   = new DBTitlesManager(this);
        Cursor resultQuery = managerUsersDB.selectUser(username);
        //Check if the title selection is unlocked
        if(resultQuery.moveToFirst()) {
            //Switch based on the selected item. Check if by the AP points can be purchased and if so, unlock it, removing the AP points used
            switch (bElementToPurchase) {
                //Avatar images
                case R.id.bReward1:
                    if (haveUserMoreAPPointsThan(75)) {
                        Toast.makeText(this, "Avatar change unlocked!", Toast.LENGTH_LONG).show();
                        managerUsersDB.upgradeUserUnlockTitleAvatarColor(username,
                                resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_TITLE)),
                                1,
                                resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_COLOR))
                        );
                        upgradeElementViewIntoDDBB(username,5);
                        upgradeElementViewIntoDDBB(username,6);
                        upgradeElementViewIntoDDBB(username,7);
                        upgradeElementViewIntoDDBB(username,8);
                        managerUsersDB.upgradeRemoveAPPoints(username, 75);
                        setElementToUnlockedToUser(username, 1);
                        clearSelectedElement();
                    }
                    else{
                        Toast.makeText(this, "Not enough Achievement Points", Toast.LENGTH_LONG).show();
                    }
                    break;
                //Color changes
                case R.id.bReward2:
                    if (haveUserMoreAPPointsThan(75)) {
                        Toast.makeText(this, "Color change unlocked!", Toast.LENGTH_LONG).show();
                        managerUsersDB.upgradeUserUnlockTitleAvatarColor(username,
                                resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_TITLE)),
                                resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_AVATAR)),
                                1
                        );
                        upgradeElementViewIntoDDBB(username, 4);
                        managerUsersDB.upgradeRemoveAPPoints(username, 75);
                        setElementToUnlockedToUser(username, 2);
                        clearSelectedElement();
                    }
                    else{
                        Toast.makeText(this, "Not enough Achievement Points", Toast.LENGTH_LONG).show();
                    }
                    break;
                //Title changes
                case R.id.bReward3:
                    if (haveUserMoreAPPointsThan(50)) {
                        Toast.makeText(this, "Title change unlocked!", Toast.LENGTH_LONG).show();
                        managerUsersDB.upgradeUserUnlockTitleAvatarColor(username,
                                1,
                                resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_AVATAR)),
                                resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_COLOR))
                        );
                        upgradeElementViewIntoDDBB(username, 9);
                        managerUsersDB.upgradeRemoveAPPoints(username, 50);
                        setElementToUnlockedToUser(username, 3);
                        clearSelectedElement();
                    }
                    else{
                        Toast.makeText(this, "Not enough Achievement Points", Toast.LENGTH_LONG).show();
                    }
                    break;
                //Aditional colors
                case R.id.bReward4:
                    if (haveUserMoreAPPointsThan(50)) {
                        Toast.makeText(this, "New colors unlocked!", Toast.LENGTH_LONG).show();
                        managerUsersDB.upgradeUserMoreColors(username);
                        managerUsersDB.upgradeRemoveAPPoints(username, 50);
                        setElementToUnlockedToUser(username, 4);
                        clearSelectedElement();
                    }
                    else{
                        Toast.makeText(this, "Not enough Achievement Points", Toast.LENGTH_LONG).show();
                    }
                    break;
                //Avatar image man 2
                case R.id.bReward5:
                    if (haveUserMoreAPPointsThan(20)) {
                        Toast.makeText(this, "Avatar unlocked!", Toast.LENGTH_LONG).show();
                        managerAvatarsDB.upgradeAvatarUnlocked("avAvatarMan2", 1, username);
                        managerUsersDB.upgradeRemoveAPPoints(username, 20);
                        setElementToUnlockedToUser(username, 5);
                        clearSelectedElement();
                    }
                    else{
                        Toast.makeText(this, "Not enough Achievement Points", Toast.LENGTH_LONG).show();
                    }
                    break;
                //Avatar image woman2
                case R.id.bReward6:
                    if (haveUserMoreAPPointsThan(20)) {
                        Toast.makeText(this, "Avatar unlocked", Toast.LENGTH_LONG).show();
                        managerAvatarsDB.upgradeAvatarUnlocked("avAvatarWoman2", 1, username);
                        managerUsersDB.upgradeRemoveAPPoints(username, 20);
                        setElementToUnlockedToUser(username, 6);
                        clearSelectedElement();
                    }
                    else{
                        Toast.makeText(this, "Not enough Achievement Points", Toast.LENGTH_LONG).show();
                    }
                    break;
                //Avatar image hipster 1
                case R.id.bReward7:
                    if (haveUserMoreAPPointsThan(30)) {
                        Toast.makeText(this, "Avatar unlocked!", Toast.LENGTH_LONG).show();
                        managerAvatarsDB.upgradeAvatarUnlocked("avAvatarManHipster", 1, username);
                        managerUsersDB.upgradeRemoveAPPoints(username, 30);
                        setElementToUnlockedToUser(username, 7);
                        clearSelectedElement();
                    }
                    else{
                        Toast.makeText(this, "Not enough Achievement Points", Toast.LENGTH_LONG).show();
                    }
                    break;
                //Avatar image hipster 2
                case R.id.bReward8:
                    if (haveUserMoreAPPointsThan(30)) {
                        Toast.makeText(this, "Avatar unlocked!", Toast.LENGTH_LONG).show();
                        managerAvatarsDB.upgradeAvatarUnlocked("avAvatarWomanHipster", 1, username);
                        managerUsersDB.upgradeRemoveAPPoints(username, 30);
                        setElementToUnlockedToUser(username, 8);
                        clearSelectedElement();
                    }
                    else{
                        Toast.makeText(this, "Not enough Achievement Points", Toast.LENGTH_LONG).show();
                    }
                    break;
                //New title
                case R.id.bReward9:
                    if (haveUserMoreAPPointsThan(40)) {
                        Toast.makeText(this, "New title unlocked!", Toast.LENGTH_LONG).show();
                        managerTitlesDB.upgradeTitleObtained("tWorker", 1, username);
                        managerUsersDB.upgradeRemoveAPPoints(username, 40);
                        setElementToUnlockedToUser(username, 9);
                        clearSelectedElement();
                    }
                    else{
                        Toast.makeText(this, "Not enough Achievement Points", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
            loadProgressUnlocked();
            loadToolbar();
        }
    }

    /*
    * Desc:  mark the item on the screen and unselect the rest of them
    * Param: int with the position of the linearlayout to select
    * */
    public void markElementSelected(int idLLElementShop){
        //Iterate all the elements on the screen
        for(int i=0;i<9;i++){
            //If is the selected, mark it
            if(i==(idLLElementShop-1)){
                llElementsOnShop[i].setBackgroundColor(Color.argb(20, 84, 84, 84));
            }
            //if not, set its regular color
            else{
                llElementsOnShop[i].setBackgroundColor(Color.WHITE);
            }
        }

    }

    /*
    * Desc:  put on the screen the ap value of the selected element, the ap value that the user have and the difference
    * Param: int with the ap cost of each element
    * */
    public void addAPDifferenceToScreen(int apvalue){

        //Get sharedpreferences item and the username asociated
        sharedpreferences                  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username                    = sharedpreferences.getString("username", "default");
        boolean retValue=false;
        //Get the linear layout

        DBUserManager managerDBUser = new DBUserManager(this);
        //Make que query
        Cursor resultQuery = managerDBUser.selectUser(username);
        //Check if the title selection is unlocked
        if(resultQuery.moveToFirst()) {
            //Get the edit text of the screen
            TextView tvCurrentAp  = (TextView) findViewById(R.id.tvRewardCurrentAP);
            TextView tvElementtAp = (TextView) findViewById(R.id.tvRewardElementAP);
            TextView tvTotalAp    = (TextView) findViewById(R.id.tvRewardTotalAP);
            TextView tvElementtApS = (TextView) findViewById(R.id.tvRewardElementAPString);
            TextView tvTotalApS = (TextView) findViewById(R.id.tvRewardTotalAPString);
            //Get the ap points of the user
            int currentApValue=resultQuery.getInt(resultQuery.getColumnIndex(managerDBUser.TU_AP_POINTS));
            //Calculate the difference
            int totalApValue= currentApValue-apvalue;
            String elementValueMinus="-"+apvalue;
            //Set the values into the screen
            tvCurrentAp.setText("" + currentApValue);
            tvElementtAp.setText(elementValueMinus);
            //Change the color if the user cant buy the element
            if(totalApValue<0){
                tvTotalAp.setTextColor(Color.RED);
            }
            tvTotalAp.setText("" + totalApValue);
            tvElementtApS.setVisibility(View.VISIBLE);
            tvTotalApS.setVisibility(View.VISIBLE);



        }
    }

    /*
    * Desc:  check if the user have more AP points than the selected element
    * Param: int with the ap cost of each element
    * */
    public boolean haveUserMoreAPPointsThan(int apPointsElement){
        //Get sharedpreferences item and the username asociated
        sharedpreferences                  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username                    = sharedpreferences.getString("username", "default");
        boolean retValue=false;
        //Get the linear layout

        DBUserManager managerDBUser = new DBUserManager(this);
        //Make que query
        Cursor resultQuery = managerDBUser.selectUser(username);
        //Check if the title selection is unlocked
        if(resultQuery.moveToFirst()) {
            int currentApValue = resultQuery.getInt(resultQuery.getColumnIndex(managerDBUser.TU_AP_POINTS));
            if(currentApValue>=apPointsElement) retValue=true;
        }

        return retValue;
    }

    /*
    * Desc:  Set the element and unlock it into the field of the user in the DDBB
    * Param: string with the username and int with the id of the element
    * */
    public void setElementToUnlockedToUser(String username, int element){
        DBUserManager managerUsersDB      = new DBUserManager(this);
        Cursor resultQuery = managerUsersDB.selectUser(username);
        //Check if the title selection is unlocked
        if(resultQuery.moveToFirst()) {
            //Get the value retrieved from the DDBB
            String elementsUnlocked=resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_ELEMENTS_UNLOCKED));

            //Check if the element is not already obtained
            if(!checkElementIsAlreadyUnlocked(elementsUnlocked,element)){
                //Get from the string an boolean array with the elemens unlocked
                boolean[] elementsUnlockedBoolean=getBooleanFromString(elementsUnlocked);
                //Modify the element to obtained into the array
                elementsUnlockedBoolean[element-1] = true;
                //Get from the boolean array a string to upgrade the new value into the DDBB
                elementsUnlocked=getStringFromBooleanArray(elementsUnlockedBoolean);

                //Insert the new value into de DDBB
                managerUsersDB.upgradeUserElementsUnlocked(username, elementsUnlocked);
            }
        }
    }

    /*
    * Desc:  check if the element is already unlocked
    * Param: string with the elementsToUnlock and int with the id of the element
    * Ret:   true if is already unlocked or false if not
    * */
    public boolean checkElementIsAlreadyUnlocked(String elementsToUnlock, int element){
        //Get the array with the boolean elements
        boolean[] elementsUnlockedBoolean=getBooleanFromString(elementsToUnlock);
        return elementsUnlockedBoolean[element-1];
    }

    /*
    * Desc:  transform the string pased by param into a boolean array.
    * Param: string with the elementsToUnlock
    * Ret:   boolean array with the elements to unlock
    * */
    public boolean[] getBooleanFromString (String stringElementsUnlocked){
        boolean [] arrayUnlockedElements = new boolean [9];
        for(int i=0;i<9;i++){
            if(stringElementsUnlocked.charAt(i)=='1'){
                arrayUnlockedElements[i]=true;
            }
            else{
                arrayUnlockedElements[i]=false;
            }
        }
        return arrayUnlockedElements;
    }

    /*
     * Desc:  transform the boolean array pased by param into a string.
     * Param: boolean array with the elements to unlock
     * Ret:   string with the elementsToUnlock
     * */
    public String getStringFromBooleanArray(boolean[] arrayElementsUnlocked){
        String stringUnlockedElements="";
        for(int i=0;i<9;i++){
            if(arrayElementsUnlocked[i]){
                stringUnlockedElements=stringUnlockedElements+"1";
            }
            else{
                stringUnlockedElements=stringUnlockedElements+"0";
            }
        }
        return stringUnlockedElements;
    }

    /*
     * Desc: Change the selection button visibility to hide
     * */
    public void changeCompletedButton(int idButton,String elementsToUnlock, int element){
        //If is unlocked, hide the button
        if(checkElementIsAlreadyUnlocked(elementsToUnlock,element)){
            Button bToChange = (Button) findViewById(idButton);
            bToChange.setVisibility(View.INVISIBLE);
        }
        else{
            Button bToChange = (Button) findViewById(idButton);
            bToChange.setVisibility(View.VISIBLE);
        }
    }

    /*
    * Desc:  unselect the previous selected element from the shop
    * */
    public void clearSelectedElement(){
        reloadUserAPPoints();
        markElementSelected(-1);
        bElementToPurchase=-1;
    }

    /*
    * Desc:  upgrade a view status for some elements in the shop
    * Param: String with the username and int with the element to upgrade its view status
    * */
    public void upgradeElementViewIntoDDBB(String username, int element){
        DBUserManager managerUsersDB      = new DBUserManager(this);
        Cursor resultQuery = managerUsersDB.selectUser(username);
        //Check if the title selection is unlocked
        if(resultQuery.moveToFirst()) {
            //Get the value retrieved from the DDBB
            String elementsUnlocked=resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_ELEMENTS_UNLOCKED_VIEW));

            //Get from the string an boolean array with the elemens unlocked
            boolean[] elementsUnlockedBoolean=getBooleanFromString(elementsUnlocked);
            //Modify the element to obtained into the array
            elementsUnlockedBoolean[element-1] = true;
            //Get from the boolean array a string to upgrade the new value into the DDBB
            elementsUnlocked=getStringFromBooleanArray(elementsUnlockedBoolean);

            //Insert the new value into de DDBB
            managerUsersDB.upgradeUserElementsViewsUnlocked(username, elementsUnlocked);

        }
    }
    /*
     * Desc: on click method to delete the selection in the shop
     * */
    public void onClickDeleteSelectionShop(View v){
        clearSelectedElement();
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

    public void onClickShowQuest(View v){
        onClickShowQuest();
    }



    /*
     * Desc: on click function to show quests
     * */
    public void onClickShowQuest(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RewardsSActivity.this);
        View layView = (LayoutInflater.from(RewardsSActivity.this)).inflate(R.layout.quest_content, null);
        alertBuilder.setView(layView);
        final TextView questName = (TextView) layView.findViewById(R.id.tvQuestPopName);
        final TextView questDesc = (TextView) layView.findViewById(R.id.tvQuestPopDesc);
        final TextView questCity = (TextView) layView.findViewById(R.id.tvQuestPopCity);
        final TextView questStreet = (TextView) layView.findViewById(R.id.tvQuestPopStreet);
        final TextView questAP = (TextView) layView.findViewById(R.id.tvQuestPopAP);
        final TextView questXP = (TextView) layView.findViewById(R.id.tvQuestPopXP);

        LinearLayout llQuest = (LinearLayout) layView.findViewById(R.id.llQuestData);
        LinearLayout llNoQuest = (LinearLayout) layView.findViewById(R.id.llNoQuest);


        sharedpreferences                  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean isQuestS     = sharedpreferences.getBoolean("questB", false);
        String questNameS    = sharedpreferences.getString("quest", "default");
        String questDescS    = sharedpreferences.getString("questDesc", "default");
        String questCityS    = sharedpreferences.getString("questCity", "default");
        String questStreetS  = sharedpreferences.getString("questStreet", "default");
        final int questAPS         = sharedpreferences.getInt("questAP", -1);
        final int questXPS         = sharedpreferences.getInt("questXP", -1);

        if(isQuestS) {
            llQuest.setVisibility(View.VISIBLE);
            llNoQuest.setVisibility(View.GONE);

            if(questNameS.compareTo("Quest1")==0){
                questName.setText("Report on locality");
            }
            else if(questNameS.compareTo("Quest2")==0){
                questName.setText("Report event");
            }
            else{
                questName.setText(questNameS);
            }
            questDesc.setText(questDescS);
            questCity.setText(questCityS);
            questStreet.setText(questStreetS);
            questAP.setText(""+questAPS);
            questXP.setText(""+questXPS);
            alertBuilder.setCancelable(true)
                    .setPositiveButton("Abandon Quest", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(this, "Quest 1 completed! Reward: " + questAPS + " AP, " + questXPS + " XP", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove("questB");
                            editor.remove("quest");
                            editor.remove("questDesc");
                            editor.remove("questCity");
                            editor.remove("questStreet");
                            editor.remove("questAP");
                            editor.remove("questXP");
                            editor.commit();
                        }
                    })
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
            ;
        }
        else{
            llQuest.setVisibility(View.GONE);
            llNoQuest.setVisibility(View.VISIBLE);

            alertBuilder.setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
            ;
        }

        Dialog dialog = alertBuilder.create();
        dialog.show();

    }

    /*
* Desc: on click method to navegate from toolbar to achievements activity
* */
    public void onClickChangeQuestActivity(View v){
        Intent myIntent= new Intent(getApplicationContext(), AchievementsActivity.class);
        startActivity(myIntent);

    }

}



