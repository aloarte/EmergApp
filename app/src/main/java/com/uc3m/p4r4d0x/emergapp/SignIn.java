package com.uc3m.p4r4d0x.emergapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uc3m.p4r4d0x.emergapp.helpers.database.DBAchievementsManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBAvatarsManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBTitlesManager;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBUserManager;

public class SignIn extends AppCompatActivity {

    Button bSignIn;
    EditText etUser,etPassword1,etPassword2,etMail,etDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Asociate all the elements from the layout
        bSignIn=(Button)findViewById(R.id.bSSignIn);
        etUser=(EditText)findViewById(R.id.etSIUser);
        etPassword1=(EditText)findViewById(R.id.etSIPassword);
        etPassword2=(EditText)findViewById(R.id.etSIRepPassword);
        etMail=(EditText) findViewById(R.id.etSIemail);
        etDate =(EditText) findViewById(R.id.etSIdate);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /*
    * Desc: onClickLister for button bSignIn
    *       Insert a new user in de DDBB and swap to another activity
    * */
    public void onClickInsert(View v){
        //OnClickListener bSSignIn button
        if (insertNewUser()) {
            //Create a new intent and launch a new activity
            Intent myIntent = new Intent(v.getContext(), LoginActivity.class);
            startActivity(myIntent);
        }


    }

    /*
    * Desc: Check if all the fields from all the EditText are filled.
    * Ret value: true or false
    * */
    public boolean allFieldsFilled(){
       return isFilled(etUser) & isFilled(etPassword1) & isFilled(etPassword2) & isFilled(etMail) & isFilled(etDate);
    }

    /*
    * Desc: Check if the field are filled.
    * Param: an EditText field to check
    * Ret value: true or false
    * */
    public boolean isFilled(EditText eText){

        if(eText.getText().toString().equals(""))    return false;
        else return true;
    }

    /*
    * Desc: Try to insert a new user in the database checking
    *       all the regular issues.
    * Param: DBUserManager object
    * Ret value: true or false
    * */
    public boolean insertNewUser(){
        DBUserManager managerDB = new DBUserManager(this);
        boolean userInsert=true;
        boolean dataUserInserted=false;
        boolean insertUserResult=false;
        //Check if all fields are filled
        if(allFieldsFilled()) {
            //Check if the user already exists
            if(!managerDB.userExist(etUser.getText().toString())){
                //Check if the passwords match
                if(etPassword1.getText().toString().equals(etPassword2.getText().toString())) {
                    //Insert a new user in the database
                    insertUserResult=managerDB.insertUser(
                            etUser.getText().toString(), etPassword1.getText().toString(),
                            etMail.getText().toString(), etDate.getText().toString()
                    );
                    if(insertUserResult){
                                            //Create the titles for the new user
                        dataUserInserted =  insertUserTitles(etUser.getText().toString())&
                                            //Create the achievements for the new user
                                            insertUserAchievements(etUser.getText().toString())&
                                            //Create the avatars for the new user
                                            insertUserAvatars(etUser.getText().toString());
                        if(dataUserInserted){
                            Toast.makeText(getApplicationContext(), "Account created.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "An error has ocurred, data account failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "An error has ocurred, account is not created.", Toast.LENGTH_SHORT).show();
                    }



                }
                //Passwords dont match
                else {
                    userInsert=false;
                    Toast.makeText(getApplicationContext(), "Passwords must match.", Toast.LENGTH_SHORT).show();
                }
            }
            //The user already exist
            else{
                userInsert=false;
                Toast.makeText(getApplicationContext(), "The user "+etUser.getText().toString()
                        +" already exist. Try a new one.", Toast.LENGTH_SHORT).show();
            }
        }
        //Any field is not filled
        else {
            userInsert=false;
            Toast.makeText(getApplicationContext(), "All the fields must be filled.", Toast.LENGTH_SHORT).show();

        }
        return userInsert;
    }

    public boolean insertUserTitles(String username){
        DBTitlesManager titleDB = new DBTitlesManager(this);
        return  titleDB.inserttitle("tBegginer", username) &
                titleDB.inserttitle("tChampion", username) &
                titleDB.inserttitle("tTop", username) &
                titleDB.inserttitle("tSeeker", username);

    }
    public boolean insertUserAchievements(String username){
        DBAchievementsManager achievementsDB = new DBAchievementsManager(this);
        boolean achievementsNovel, achievementsExpert, achievementsSecret;
        achievementsNovel=  achievementsDB.insertAchievement("aNovelMeta","First Steps"                             ,5 ,50 ,50 ,username) &
                            achievementsDB.insertAchievement("aNovel1"   ,"Photo Editor"                            ,0 ,5  ,5  ,username) &
                            achievementsDB.insertAchievement("aNovel2"   ,"Video Editor"                            ,0 ,5  ,5  ,username) &
                            achievementsDB.insertAchievement("aNovel3"   ,"Message Editor"                          ,0 ,5  ,0  ,username) &
                            achievementsDB.insertAchievement("aNovel4"   ,"Ubication Editor"                        ,0 ,5  ,0  ,username) &
                            achievementsDB.insertAchievement("aNovel5"   ,"Reporter"                                ,0 ,5  ,0  ,username) ;

        achievementsExpert= achievementsDB.insertAchievement("aExpertMeta","Community Helper"                       ,5 ,100,50 ,username) &
                            achievementsDB.insertAchievement("aExpert1"   ,"Pictures Lover"                         ,10,10 ,10 ,username) &
                            achievementsDB.insertAchievement("aExpert2"   ,"Videos Lover"                           ,10,10 ,10 ,username) &
                            achievementsDB.insertAchievement("aExpert3"   ,"Expert Reporter"                        ,0 ,25 ,0  ,username) &
                            achievementsDB.insertAchievement("aExpert4"   ,"Hard Worker"                            ,0 ,0  ,20 ,username) &
                            achievementsDB.insertAchievement("aExpert5"   ,"Top Reporter"                           ,0 ,10 ,10 ,username) &
                            achievementsDB.insertAchievement("aExpert6"   ,"Reporting Anywhere"                     ,3 ,20 ,10 ,username) ;

        achievementsSecret= achievementsDB.insertAchievement("aSecretMeta","Seeker of Truth"                        ,5 ,200,50 ,username) &
                            achievementsDB.insertAchievement("aSecret1"   ,"I give my best"                         ,0 ,10 ,0  ,username) &
                            achievementsDB.insertAchievement("aSecret2"   ,"An image is worth more than 1000 words" ,0 ,10 ,0  ,username) &
                            achievementsDB.insertAchievement("aSecret3"   ,"As fast as I can"                       ,0 ,10 ,0  ,username) &
                            achievementsDB.insertAchievement("aSecret4"   ,"Personal image is allways the first"    ,0 ,10 ,0  ,username) &
                            achievementsDB.insertAchievement("aSecret5"   ,"First my neighborhood"                  ,0 ,10 ,0  ,username);


        return achievementsNovel & achievementsExpert & achievementsSecret;
    }

    public boolean insertUserAvatars(String username){
        DBAvatarsManager avatarDB = new  DBAvatarsManager(this);
        return  avatarDB.insertAvatar("avAvatarMan1"         ,R.mipmap.avatar_hombre1  ,1, username) &
                avatarDB.insertAvatar("avAvatarWoman1"       ,R.mipmap.avatar_mujer1   ,1, username) &
                avatarDB.insertAvatar("avAvatarMan2"         ,R.mipmap.avatar_hombre2  ,0, username) &
                avatarDB.insertAvatar("avAvatarWoman2"       ,R.mipmap.avatar_mujer2   ,0, username) &
                avatarDB.insertAvatar("avAvatarManHipster"   ,R.mipmap.avatar_hipster1 ,0, username) &
                avatarDB.insertAvatar("avAvatarWomanHipster" ,R.mipmap.avatar_hipster2 ,0, username);

    }


}
