package com.uc3m.p4r4d0x.emergapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uc3m.p4r4d0x.emergapp.helpers.database.DBManager;

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
    * Param: DBManager object
    * Ret value: true or false
    * */
    public boolean insertNewUser(){
        DBManager managerDB = new DBManager(this);
        boolean userInsert=true;
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
                        Toast.makeText(getApplicationContext(), "Account created.", Toast.LENGTH_SHORT).show();
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


}
