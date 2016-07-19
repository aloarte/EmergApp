package com.uc3m.p4r4d0x.emergapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.uc3m.p4r4d0x.emergapp.fragments.RankFragment1;
import com.uc3m.p4r4d0x.emergapp.fragments.RankFragment2;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBManager;

public class RankingActivity extends AppCompatActivity {


    TabLayout tabLayoutRanking;
    ViewPager viewPagerRanking;
    //Info to use shared preferences to have a session
    final String MyPREFERENCES = "userPreferences";
    SharedPreferences sharedpreferences;

    /*Inner class implemented for the TabLayout*/
    private class CustomAdapter extends FragmentPagerAdapter {
        //String array with the fragments names
        private String fragmentsNames[] = new String[]{"XP Ranking", "AP Ranking"};

        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            //For each position create the correspond fragment, created by a fragment class
            switch (position) {
                case 0:
                    return new RankFragment1();
                case 1:
                    return new RankFragment2();
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
        setContentView(R.layout.activity_ranking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Load the toolbar
        loadToolbar();


        //FOR THE TAB LAYOUT
        //Get the viewPager
        viewPagerRanking = (ViewPager) findViewById(R.id.viewPagerRanking);
        viewPagerRanking.setAdapter(new CustomAdapter(getSupportFragmentManager(), getApplicationContext()));

        //Get the tabLayout and set the viewPager
        tabLayoutRanking = (TabLayout) findViewById(R.id.tabLayoutRanking);
        tabLayoutRanking.setupWithViewPager(viewPagerRanking);

        //Override onTabSelected methods to let the tab respond with the viewPager
        tabLayoutRanking.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerRanking.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPagerRanking.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPagerRanking.setCurrentItem(tab.getPosition());
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
                myIntent = new Intent(getApplicationContext(), AccountConfigurationActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_profile:
                myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_ranking:
                myIntent = new Intent(getApplicationContext(), RankingActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_achievements:
                myIntent = new Intent(getApplicationContext(), AchievementsActivity.class);
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
    public void loadToolbar() {
        //Get sharedpreferences item and the username asociated
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username = sharedpreferences.getString("username", "default");

        //Check the username
        if (username.compareTo("default") == 0) {
            //If is empty (error) do nothing
        } else {
            //Put username in the toolbar text view
            TextView tvToolbarUser = (TextView) findViewById(R.id.tvToolbarUser);
            tvToolbarUser.setText(username);

        }
        DBManager managerDB = new DBManager(this);
        //Select the user
        Cursor resultQuery = managerDB.selectUser(username);
        //If the user exists
        if (resultQuery.moveToFirst() == true) {
            //Get the password by searching first the column index
            int level = resultQuery.getInt(resultQuery.getColumnIndex(DBManager.FN_LEVEL));
            int points = resultQuery.getInt(resultQuery.getColumnIndex(DBManager.FN_POINTS));

            TextView tvToolbarPointsNumber = (TextView) findViewById(R.id.tvToolbarCurrentXP);
            tvToolbarPointsNumber.setText("" + points);

        }
    }

    /*
    * Desc: performs a logout from the current logged user
    *
    * */
    public void performLogout() {

        //Remove from the shared preferences the username
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("username");
        editor.commit();

        //Create and launch login activity
        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(myIntent);
    }
}
