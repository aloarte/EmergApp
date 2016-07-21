package com.uc3m.p4r4d0x.emergapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uc3m.p4r4d0x.emergapp.helpers.database.DBManager;

import java.util.ArrayList;

public class AccountConfigurationActivity extends AppCompatActivity {

    //Info to use shared preferences to have a session
    final String MyPREFERENCES = "userPreferences";
    SharedPreferences sharedpreferences;


    Spinner colorOptionsSpinner;
    String colorSelected="";

    String [][] colors = new String[][] {{"Grey"   ,"#bdbdbd","#e0e0e0"},
                                         {"Yellow" ,"#ffa000","#ffc107"},
                                         {"Pink"   ,"#e91e63","#f8bbd0"},
                                         {"Green"  ,"#43a047","#4caf50"},
                                         {"Blue"   ,"#303f9f","#3f51b5"},
                                         {"Red"    ,"#d32f2f","#ffcdd2"},
                                         {"Default","#009688","#26a69a"}};

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
        DBManager managerDB                = new DBManager(this);
        //Select the user
        Cursor resultQuery                 = managerDB.selectUser(username);
        //If the user exists
        if(resultQuery.moveToFirst()==true){
            //Get the password by searching first the column index
            int level                      = resultQuery.getInt(resultQuery.getColumnIndex(DBManager.FN_LEVEL));
            int points                     = resultQuery.getInt(resultQuery.getColumnIndex(DBManager.FN_POINTS));

            TextView tvToolbarPointsNumber = (TextView) findViewById(R.id.tvToolbarCurrentXP);
            tvToolbarPointsNumber.setText(""+points);

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


    public String[] getColorsCodes(String color){
        String[] colorsToReturn = new String []{"",""};

        //Search for the requested color on the array
        for(int i=0; i<colors.length;i++){
            //When matches, fill the return string with the codes
            if(color.compareTo(colors[i][0])==0){
                colorsToReturn[0]=colors[i][1];
                colorsToReturn[1]=colors[i][2];
            }
        }

        if(colorsToReturn[0].compareTo("")==0 || colorsToReturn[1].compareTo("")==0){
            colorsToReturn[0]="#009688";
            colorsToReturn[1]="#26a69a";
        }
        return colorsToReturn;

    }

    public void onClickChangeColor(View v){
        Log.d("ALR","Button pressed. Color: "+ colorSelected);
        //if the color is not chosen , skip
        if(colorSelected.compareTo("")!=0){
            String[] colorsToSet=getColorsCodes(colorSelected);


            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("colorprimary", colorsToSet[0]);
            editor.putString("colorsecondary",colorsToSet[1]);
            editor.commit();

            loadColor();
        }


    }

    public void loadColor(){

        //Check if there is any user logged into the aplication checking shared preferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String primaryColor = sharedpreferences.getString("colorprimary", "default");
        String secondaryColor = sharedpreferences.getString("colorsecondary", "default");
        Log.d("ALR","loadColor. Primary: "+ primaryColor+ " Secondary: "+secondaryColor);
        //if there is no color
        if(primaryColor.compareTo("default")==0 || secondaryColor.compareTo("default")==0){
            //Load default color
            Log.d("ALR","default");
        }
        else{

            //Load the new color
            Toolbar t= (Toolbar) findViewById(R.id.toolbarAC);
            t.setBackgroundColor(Color.parseColor(primaryColor));

        }
    }
}
