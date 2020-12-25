package com.example.bgctub_transport_tracker_studentapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bgctub_transport_tracker_studentapp.services.BuildNotificationService;
import com.example.bgctub_transport_tracker_studentapp.services.MyFirebaseMessageService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class AppMainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_transport_info, R.id.nav_profile, R.id.nav_help, R.id.nav_report, R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        timer = new Timer();

        //check user login**
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(AppMainActivity.this, SigninActivity.class));
            finish();
        }

        //Create checking notification
        checkInternetAndLocation(AppMainActivity.this);

    }


    //used for check user again when user back previous session or resume of activity
    //used for solve after delete account problem, open AppMainActivity

    @Override
    protected void onPostResume() {
        super.onPostResume();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //menu action**
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_reload:
                recreate();
                return true;
            case R.id.action_logout:
                mAuth.signOut();
                startActivity(new Intent(AppMainActivity.this, SigninActivity.class));
                finish();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(AppMainActivity.this, SettingsActivity.class));
                return true;

            case R.id.action_exit:
                //if exit notification service will stopped: for unfortunate problem
                stopService(new Intent(this, BuildNotificationService.class));
                System.exit(0);
                finish();
                return true;

            case R.id.action_news_update:
                startActivity(new Intent(AppMainActivity.this, NewsAndUpdateActivity.class));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }




    //*******************Check Internet and location********************
    //Check Internet Connection **
    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            android.net.NetworkInfo wifiNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mobileNetwork != null && mobileNetwork.isConnectedOrConnecting() || wifiNetwork != null && wifiNetwork.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }






    // Internet connection check alert dialog message**

    AlertDialog.Builder alertDialogBuilder(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sorry, your phone not connected with internet");
        builder.setMessage(R.string.off_internet_message);
        builder.setIcon(R.drawable.ic_no_internet);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();

            }
        });
        builder.show();

        return builder;
    }


    //check location connection**
    private boolean isLocationOn() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }


    //Build SnackBar notification for location check and internet connection check**
    void createSnackbar(View view, String message) {

        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }


    //create timer for checking connection a time period**
    private void checkInternetAndLocation(final Context context) {
        if (!isConnected(context)) {
            alertDialogBuilder(context);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isConnected(context)) {

                    createSnackbar(findViewById(R.id.drawer_layout), getString(R.string.off_internet_message));

                }
                if (!isLocationOn()) {

                    createSnackbar(findViewById(R.id.drawer_layout), getString(R.string.off_location_snackbar_message));

                }
                if (!isLocationOn() && !isConnected(context)) {

                    createSnackbar(findViewById(R.id.drawer_layout), getString(R.string.off_location_internet_snackbar_message));

                }
            }
        }, 0, 10000);


    }
}