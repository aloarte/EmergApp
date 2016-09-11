package com.uc3m.p4r4d0x.emergapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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

    /*
   * Desc: method overrided from AppCompatActivity
   *       this method is called when activity starts
   *       Prepare the toolbar menu
   * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    * Desc: method overrided from AppCompatActivity
    *       this method is called when activity starts
    *       Prepare the elements on the toolbar menu
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.changeEmail:
                onClickShowChangeEmail();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
 * Desc: on click function to change email
 * */
    public void onClickShowChangeEmail(){
        //Get the alert dialog based on the resource email_change_input
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
        View layView = (LayoutInflater.from(LoginActivity.this)).inflate(R.layout.email_change_input, null);
        alertBuilder.setView(layView);

        //Get the field
        final EditText userInput = (EditText) layView.findViewById(R.id.tvContentMessage);

        //Build the buttons on the alertbuilder
        alertBuilder.setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        //Set the email into the shared preferences
                        sharedpreferences.edit().putString("email_to_report", userInput.getText().toString()).commit();
                        Toast.makeText(getApplicationContext(), "Email to retrieve reports changed!", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
        ;

        Dialog dialog = alertBuilder.create();
        dialog.show();

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
