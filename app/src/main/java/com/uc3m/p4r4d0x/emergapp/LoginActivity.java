package com.uc3m.p4r4d0x.emergapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uc3m.p4r4d0x.emergapp.helpers.database.DBAchievementsManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBAvatarsManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBQuestsManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBTitlesManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBUserManager;


public class LoginActivity extends AppCompatActivity {

    Button bLogin,bNewAc;
    EditText etPassword,etUser;
    final String MyPREFERENCES="userPreferences";
    SharedPreferences sharedpreferences;
    TextView tvFailLogin;
    int retriesLogin = 3;
    Context context;

    String [][] colors = new String[][] {
            {"Default","#009688","#4db6ac"},
            {"Red"    ,"#d32f2f","#ffcdd2"},
            {"Blue"   ,"#303f9f","#7986cb"},
            {"Green"  ,"#43a047","#a5d6a7"},
            {"Purple" ,"#8e24aa","#ba68c8"},
            {"Yellow" ,"#ffca28","#ffe082"},
            {"Pink"   ,"#e91e63","#f8bbd0"},
            {"Grey"   ,"#bdbdbd","#e0e0e0"}
            };

    int colorSelected=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarL);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        context= getApplicationContext();
        //Get the buttons
        bLogin=(Button)findViewById(R.id.bSSignIn);
        bNewAc=(Button)findViewById(R.id.bLSignIn);
        //Get the edit text fields
        etPassword=(EditText)findViewById(R.id.etPassword);
        etUser=(EditText)findViewById(R.id.etUser);
        //Get the text view for the retries
        tvFailLogin=(TextView)findViewById(R.id.tvRetry);
        //hide text view
        tvFailLogin.setVisibility(View.GONE);

        insertInitialValues();

        //Check if there is any user logged into the aplication checking shared preferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username = sharedpreferences.getString("username", "default");
        //if there is no user
        if(username.compareTo("default")==0){
            //Continue:no session
        }
        else{

            changeToHomeActivity();
        }


    }

    @Override
    public void onBackPressed() {
    }

    /*
     * Desc: onClickListener for button bLogIn
     *       Check if the login was successfull
     */
    public void onClickLogin(View v){
        //If the login was correct
        if (checkLogIn()) {
            // If the loggin is successfoul, save the user as a logged user into a shared preferences

            String username=etUser.getText().toString();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("username", username);

            //Using a switch, put the colors into the shared preferences based on the color selected by user in the bbdd
            switch(colorSelected){
                //DefaultColor
                case 0:
                    editor.putString("colorprimary", colors[0][1]);
                    editor.putString("colorsecondary",colors[0][2]);
                    break;
                //Red
                case 1:
                    editor.putString("colorprimary", colors[1][1]);
                    editor.putString("colorsecondary",colors[1][2]);
                    break;
                //Blue
                case 2:
                    editor.putString("colorprimary", colors[2][1]);
                    editor.putString("colorsecondary",colors[2][2]);
                    break;
                //Green
                case 3:
                    editor.putString("colorprimary", colors[3][1]);
                    editor.putString("colorsecondary",colors[3][2]);
                    break;
                //Purple
                case 4:
                    editor.putString("colorprimary", colors[4][1]);
                    editor.putString("colorsecondary",colors[4][2]);
                    break;
                //Yellow
                case 5:
                    editor.putString("colorprimary", colors[4][1]);
                    editor.putString("colorsecondary",colors[4][2]);
                    break;
                //Pink
                case 6:
                    editor.putString("colorprimary", colors[5][1]);
                    editor.putString("colorsecondary",colors[5][2]);
                    break;
                //Grey
                case 7:
                    editor.putString("colorprimary", colors[6][1]);
                    editor.putString("colorsecondary",colors[6][2]);
                    break;
                default:
                    break;
            }
            changeToHomeActivity();
            editor.commit();


        }
        //Wrong login
        else {
            //Change the retries text view
            tvFailLogin.setVisibility(View.VISIBLE);
            tvFailLogin.setBackgroundColor(Color.RED);
            retriesLogin--;
            tvFailLogin.setText(Integer.toString(retriesLogin));
            //If retries==0, set the login button to not enabled
            if (retriesLogin == 0) {
               bLogin.setEnabled(false);
            }

        }
    }

    /*
     * Desc: onClickListener for button bLogIn
     *       Swap to another activity (SignIn)
     */
    public void onClickSignIn(View v){
         //Crear un nuevo intent
         Intent myIntent = new Intent(v.getContext(), SignIn.class);
         //Iniciar actividad
         startActivity(myIntent);
    }

    /*
     * Desc: check the loggin process using the data from the DDBB and the data setted in the Text views
     * Ret:  A boolean true if the login was correct
     *
     */
    public boolean checkLogIn(){
        //Auxiliar strings
        String userS,passwordS,passwordSBBD;
        userS= etUser.getText().toString();
        passwordS =etPassword.getText().toString();
        //Control boolean
        boolean logged=true;
        //Database manager
        DBUserManager managerDB = new DBUserManager(this);

        //Select the user
        Cursor resultQuery= managerDB.selectUser(userS);
        //If the user exists
        if(resultQuery.moveToFirst()==true){
            //Set the color selected by the user (or default = 0)
            colorSelected = resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_COLOR));

            //Get the password by searching first the column index
            passwordSBBD = resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_PASSWORD));
            //if the password match
            if(passwordS.compareTo(passwordSBBD)==0){
                Toast.makeText(getApplicationContext(), "Login correct", Toast.LENGTH_SHORT).show();
            }
            //If password dont match
            else{
                logged=false;
                Toast.makeText(getApplicationContext(), "Wrong password.", Toast.LENGTH_SHORT).show();
            }
        }
        //If user dont exist
        else{
            logged=false;
            Toast.makeText(getApplicationContext(), "Wrong user.", Toast.LENGTH_SHORT).show();
        }

        return logged;
    }

    /*
     * Desc: Perform the launch of a new intent to navegate to HomeScreen activity
     *
     */
    public void changeToHomeActivity(){
        //Create and launch a new activity
        Intent myIntent = new Intent(context, HomeScreenActivity.class);
        startActivity(myIntent);

    }

    public void insertInitialValues(){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        sharedpreferences.edit().putBoolean("first_time", true).commit();
        if (sharedpreferences.getBoolean("first_time", true)){
            insertExampleUsers();
            insertQuests();
            sharedpreferences.edit().putBoolean("first_time", false).commit();
        }
        else{
        }
    }
    public boolean insertExampleUsers(){

            DBUserManager dbUser= new DBUserManager(this);
            boolean users=
                            dbUser.insertFullFieldsUser("AdminUser1", "1234", "admin1@gmail.com", "10/10/2010", "Traveler", "-"              , 65 , 40, 3,R.mipmap.avatar_hombre1, 1, 1, 1)&
                            dbUser.insertFullFieldsUser("AdminUser2", "1234", "admin2@gmail.com", "10/10/2010", "Champion", "-"              , 100, 170, 5,R.mipmap.avatar_mujer1, 1, 1, 1) &
                            dbUser.insertFullFieldsUser("AdminUser3", "1234", "admin3@gmail.com", "10/10/2010", "Veteran", "Seeker of Truth" , 325, 140, 2,R.mipmap.avatar_hombre2, 1, 1, 1) &
                            dbUser.insertFullFieldsUser("AdminUser4", "1234", "admin4@gmail.com", "10/10/2010", "Champion", "-"              , 175, 160, 6,R.mipmap.avatar_mujer2, 1, 1, 1) &
                            dbUser.insertFullFieldsUser("AdminUser5", "1234", "admin5@gmail.com", "10/10/2010", "Veteran", "Top Reporter"    , 100, 100, 1,R.mipmap.avatar_hipster1, 1, 1, 1);
            boolean titles=
                            insertUserTitles("AdminUser1") & insertUserTitles("AdminUser2") &
                            insertUserTitles("AdminUser3") & insertUserTitles("AdminUser4") &
                            insertUserTitles("AdminUser5") ;

            boolean achievements=
                            insertUserAchievements("AdminUser1") & insertUserAchievements("AdminUser2") &
                            insertUserAchievements("AdminUser3") & insertUserAchievements("AdminUser4") &
                            insertUserAchievements("AdminUser5") ;

            boolean avatars=
                            insertUserAvatars("AdminUser1") & insertUserAvatars("AdminUser2") &
                            insertUserAvatars("AdminUser3") & insertUserAvatars("AdminUser4") &
                            insertUserAvatars("AdminUser5") ;

        return users & titles & achievements & avatars;

    }
    /*
     * Desc: Insert the titles of the user
     * Param: an String with the name of the user for calling the DDBB
     * Ret value: true or false if anything fails
     * */
    public boolean insertUserTitles(String username){
        DBTitlesManager titleDB = new DBTitlesManager(this);
        return  titleDB.inserttitle("tBegginer", username,1) &
                titleDB.inserttitle("tChampion", username,1) &
                titleDB.inserttitle("tTop", username,1) &
                titleDB.inserttitle("tSeeker", username,1);

    }

    /*
     * Desc: Insert the achievements of the user
     * Param: an String with the name of the user for calling the DDBB
     * Ret value: true or false if anything fails
     * */
    public boolean insertUserAchievements(String username){
        DBAchievementsManager achievementsDB = new DBAchievementsManager(this);
        boolean achievementsNovel, achievementsExpert, achievementsSecret;
        achievementsNovel=
                achievementsDB.insertAchievement("aNovelMeta","First Steps"                             ,5 ,50 ,50 ,1,1,username) &
                achievementsDB.insertAchievement("aNovel1"   ,"Photo Editor"                            ,0 ,5  ,5  ,1,1,username) &
                achievementsDB.insertAchievement("aNovel2"   ,"Video Editor"                            ,0 ,5  ,5  ,1,1,username) &
                achievementsDB.insertAchievement("aNovel3"   ,"Message Editor"                          ,0 ,5  ,0  ,1,1,username) &
                achievementsDB.insertAchievement("aNovel4"   ,"Ubication Editor"                        ,0 ,5  ,0  ,1,1,username) &
                achievementsDB.insertAchievement("aNovel5"   ,"Reporter"                                ,0 ,5  ,0  ,1,1,username) ;

        achievementsExpert= achievementsDB.insertAchievement("aExpertMeta","Community Helper"                       ,5 ,100,50,1,1,username) &
                achievementsDB.insertAchievement("aExpert1"   ,"Pictures Lover"                         ,10,10 ,10 ,1,1,username) &
                achievementsDB.insertAchievement("aExpert2"   ,"Videos Lover"                           ,10,10 ,10 ,1,1,username) &
                achievementsDB.insertAchievement("aExpert3"   ,"Expert Reporter"                        ,0 ,25 ,0  ,1,1,username) &
                achievementsDB.insertAchievement("aExpert4"   ,"Hard Worker"                            ,0 ,0  ,20 ,1,1,username) &
                achievementsDB.insertAchievement("aExpert5"   ,"Top Reporter"                           ,0 ,10 ,10 ,1,1,username) &
                achievementsDB.insertAchievement("aExpert6"   ,"Reporting Anywhere"                     ,3 ,20 ,10 ,1,1,username) ;

        achievementsSecret= achievementsDB.insertAchievement("aSecretMeta","Seeker of Truth"                        ,5 ,200,50 ,1,1,username) &
                achievementsDB.insertAchievement("aSecret1"   ,"I give my best"                         ,0 ,10 ,0  ,1,0,username) &
                achievementsDB.insertAchievement("aSecret2"   ,"An image is worth more than 1000 words" ,0 ,10 ,0  ,1,0,username) &
                achievementsDB.insertAchievement("aSecret3"   ,"As fast as I can"                       ,0 ,10 ,0  ,1,0,username) &
                achievementsDB.insertAchievement("aSecret4"   ,"Personal image is allways the first"    ,0 ,10 ,0  ,1,0,username) &
                achievementsDB.insertAchievement("aSecret5"   ,"First my neighborhood"                  ,0 ,10 ,0  ,1,0,username);


        return achievementsNovel & achievementsExpert & achievementsSecret;
    }

    /*
     * Desc: Insert the avatars of the user
     * Param: an String with the name of the user for calling the DDBB
     * Ret value: true or false if anything fails
     * */
    public boolean insertUserAvatars(String username){
        DBAvatarsManager avatarDB = new  DBAvatarsManager(this);
        return  avatarDB.insertAvatar("avAvatarMan1"        , R.mipmap.avatar_hombre1  ,1, username) &
                avatarDB.insertAvatar("avAvatarWoman1"      , R.mipmap.avatar_mujer1  , 1, username) &
                avatarDB.insertAvatar("avAvatarMan2"        , R.mipmap.avatar_hombre2 , 1, username) &
                avatarDB.insertAvatar("avAvatarWoman2"      , R.mipmap.avatar_mujer2  , 1, username) &
                avatarDB.insertAvatar("avAvatarManHipster"  , R.mipmap.avatar_hipster1, 1, username) &
                avatarDB.insertAvatar("avAvatarWomanHipster", R.mipmap.avatar_hipster2, 1, username);

    }

    public boolean insertQuests(){
        DBQuestsManager dbQuest= new DBQuestsManager(this);
        boolean quest1Inserted=
                dbQuest.insertQuest("Q1Tr1", "", "Quest1","Las Rozas"          , "0.0003,0.2223", "Traveler",  5 ,  5 ) &
                dbQuest.insertQuest("Q1Tr2", "", "Quest1","Getafe"             , "0.0003,0.2223", "Traveler",  5 ,  5 ) &
                dbQuest.insertQuest("Q1Vt1", "", "Quest1","Leganes"            , "0.0003,0.2223", "Veteran" ,  10 , 5 ) &
                dbQuest.insertQuest("Q1Vt2", "", "Quest1","Majadahonda"        , "0.0003,0.2223", "Veteran" ,  10 , 5 ) &
                dbQuest.insertQuest("Q1Vt3", "", "Quest1","Alcorcón"           , "0.0003,0.2223", "Veteran" ,  10 , 5 ) &
                dbQuest.insertQuest("Q1Ch1", "", "Quest1","Fuenlabrada"        , "0.0003,0.2223", "Champion",  10 , 10) &
                dbQuest.insertQuest("Q1Ch2", "", "Quest1","Humanes"            , "0.0003,0.2223", "Champion",  10 , 10) &
                dbQuest.insertQuest("Q1Ch3", "", "Quest1","Móstoles"           , "0.0003,0.2223", "Champion",  10 , 10) &
                dbQuest.insertQuest("Q1He1", "", "Quest1","Pinto"              , "0.0003,0.2223", "Hero"    ,  15 , 10) &
                dbQuest.insertQuest("Q1He2", "", "Quest1","Parla"              , "0.0003,0.2223", "Hero"    ,  15 , 10) &
                dbQuest.insertQuest("Q1He3", "", "Quest1","Coslada"            , "0.0003,0.2223", "Hero"    ,  15 , 10) &
                dbQuest.insertQuest("Q1Le1", "", "Quest1","Madrid"             , "0.0003,0.2223", "Legend"  ,  15 , 15) &
                dbQuest.insertQuest("Q1Le2", "", "Quest1","Torrejón de Ardoz"  , "0.0003,0.2223", "Legend"  ,  15 , 15);

        boolean quest2Inserted=
                dbQuest.insertQuest("Q1Tr1", "Waste evaluation"   , "Quest2","Calle del Maestro, Leganés,Madrid"                      , "0.0003,0.2223", "Traveler"  ,  10 , 5 ) &
                dbQuest.insertQuest("Q1Tr2", "Street status"      , "Quest2","Calle Vía Dublín, Madrid, Comunidad de Madrid"          , "0.0003,0.2223", "Traveler"  ,  10 , 5 ) &
                dbQuest.insertQuest("Q1Vt1", "Park evaluation"    , "Quest2","Av. de Menéndez Pelayo,Madrid, Comunidad de Madrid"     , "0.0003,0.2223", "Veteran"   ,  15 , 10) &
                dbQuest.insertQuest("Q1Vt2", "Waste evaluation"   , "Quest2","Calle Vía Dublín, Madrid, Comunidad de Madrid"          , "0.0003,0.2223", "Veteran"   ,  15 , 10) &
                dbQuest.insertQuest("Q1Vt3", "Car crush wastes"   , "Quest2","Calle de Eugenia de Montijo,Madrid, Comunidad de Madrid", "0.0003,0.2223", "Veteran"   ,  15 , 10) &
                dbQuest.insertQuest("Q1Ch1", "Minor car accident" , "Quest2","Calle del Arenal, Madrid, Comunidad de Madrid"          , "0.0003,0.2223", "Champion"  ,  20 , 15) &
                dbQuest.insertQuest("Q1Ch2", "Park evaluation"    , "Quest2","Carr. Cdad. Universitaria,Madrid, Comunidad de Madrid " , "0.0003,0.2223", "Champion"  ,  20 , 15) &
                dbQuest.insertQuest("Q1Ch3", "Waste evaluation"   , "Quest2","Av. Complutense, Madrid, Comunidad de Madrid          " , "0.0003,0.2223", "Champion"  ,  20 , 15) &
                dbQuest.insertQuest("Q1He1", "Damaged building"   , "Quest2","Carr. Cdad. Universitaria,Madrid, Comunidad de Madrid " , "0.0003,0.2223", "Hero"      ,  20 , 20) &
                dbQuest.insertQuest("Q1He2", "Car accident"       , "Quest2","Calle de Eugenia de Montijo,Madrid, Comunidad de Madrid", "0.0003,0.2223", "Hero"      ,  20 , 20) &
                dbQuest.insertQuest("Q1He3", "Storm Remains"      , "Quest2","Av. de Logroño, Madrid, Comunidad de Madrid"            , "0.0003,0.2223", "Hero"      ,  20 , 20) &
                dbQuest.insertQuest("Q1Le1", "Burning"            , "Quest2","Calle del Arenal, Madrid, Comunidad de Madrid"          , "0.0003,0.2223", "Legend"    ,  25 , 25) &
                dbQuest.insertQuest("Q1Le2", "Car accident"       , "Quest2","Av. de Atenas, Alcorcón, Comunidad de Madrid"           , "0.0003,0.2223", "Legend"    ,  25 , 25) ;

        return quest1Inserted && quest2Inserted;

    }
}
