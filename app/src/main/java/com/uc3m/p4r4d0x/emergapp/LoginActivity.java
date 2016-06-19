package com.uc3m.p4r4d0x.emergapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uc3m.p4r4d0x.emergapp.helpers.database.DBManager;


public class LoginActivity extends AppCompatActivity {

    Button bLogin,bNewAc;
    EditText etPassword,etUser;
    final String MyPREFERENCES="userPreferences";
    SharedPreferences sharedpreferences;
    TextView tvFailLogin;
    int retriesLogin = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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


        //Check if there is any user logged into the aplication checking shared preferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username = sharedpreferences.getString("username", "default");
        //if there is no user
        if(username.compareTo("default")==0){
            //Continue:no session
        }
        else{

            //Create and launch next activity: EmMessage1
            Intent myIntent = new Intent(getApplicationContext(), EmMessage1.class);
            startActivity(myIntent);
        }


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
            editor.commit();

            //Create and launch a new activity
            Intent myIntent = new Intent(getApplicationContext(), EmMessage1.class);
            startActivity(myIntent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean checkLogIn(){
        //Auxiliar strings
        String userS,passwordS,passwordSBBD;
        userS= etUser.getText().toString();
        passwordS =etPassword.getText().toString();
        //Control boolean
        boolean logged=true;
        //Database manager
        DBManager managerDB = new DBManager(this);

        //Select the user
        Cursor resultQuery= managerDB.selectUser(userS);
        //If the user exists
        if(resultQuery.moveToFirst()==true){
            //Get the password by searching first the column index
            passwordSBBD = resultQuery.getString(resultQuery.getColumnIndex(DBManager.FN_PASSWORD));
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

}
