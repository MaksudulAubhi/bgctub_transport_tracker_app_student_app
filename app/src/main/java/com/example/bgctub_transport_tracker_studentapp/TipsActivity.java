package com.example.bgctub_transport_tracker_studentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import com.example.bgctub_transport_tracker_studentapp.services.BuildNotificationService;
import com.example.bgctub_transport_tracker_studentapp.ui.tipsActivity.tips0.Tips0Fragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

public class TipsActivity extends AppCompatActivity {
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Tips0Fragment.newInstance())
                    .commitNow();
        }

        timer = new Timer();

        //Create checking notification
        checkInternetAndLocation(TipsActivity.this);

        //build app notification
        buildAppNotification();
    }


    // Call Build app notification service
    private void buildAppNotification() {
        startService(new Intent(this, BuildNotificationService.class));
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

                    createSnackbar(findViewById(R.id.container), getString(R.string.off_internet_message));

                }
                if (!isLocationOn()) {

                    createSnackbar(findViewById(R.id.container), getString(R.string.off_location_snackbar_message));

                }
                if (!isLocationOn() && !isConnected(context)) {

                    createSnackbar(findViewById(R.id.container), getString(R.string.off_location_internet_snackbar_message));

                }
            }
        }, 0, 10000);


    }

}
