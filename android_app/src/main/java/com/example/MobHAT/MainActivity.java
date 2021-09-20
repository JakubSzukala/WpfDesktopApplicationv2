package com.example.MobHAT;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    /* BEGIN config data */
    private String ipAddress = DATA.DEFAULT_IP_ADDRESS;
    private int sampleTime = DATA.DEFAULT_SAMPLE_TIME;
    private int sampleQuantity = DATA.DEFAULT_SAMPLE_QUANTITY;
    private Intent dataIntent;
    /* END config data */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void btns_onClick(View v) {
        switch (v.getId()) {
            case R.id.button_Settings: {
                configurebutton_settings();
                // openConfig();
                break;
            }
/*            case R.id.button_LED: {

                break;
            }*/
            case R.id.button_data: {
                configurebutton_data();
                break;
            }


            case R.id.button_tables: {
                configurebutton_tables();

                break;
            }

            case R.id.button_LED: {
                dataIntent=new Intent(this, LedMatrixActivity.class);
                getDataIntent();
                startActivity(dataIntent);
                break;

            }
            case R.id.button_rpy: {
                configurebutton_rpy();
                break;
            }
            default: {
                // do nothing
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        if ((requestCode == DATA.REQUEST_CODE_CONFIG) && (resultCode == RESULT_OK)) {

            // IoT server IP address
            ipAddress = dataIntent.getStringExtra(DATA.CONFIG_IP_ADDRESS);


            // Sample time (ms)
            String sampleTimeText = dataIntent.getStringExtra(DATA.CONFIG_SAMPLE_TIME);
            sampleTime = Integer.parseInt(sampleTimeText);

            // Sample quantity
            String sampleQuantityText = dataIntent.getStringExtra(DATA.CONFIG_SAMPLE_QUANTITY);
            sampleQuantity = Integer.parseInt(sampleQuantityText);

        }
    }



    private void configurebutton_data() {
                Intent openDataIntent = new Intent(this, ChartsActivity.class);
                Bundle configBundle = new Bundle();
                configBundle.putInt(DATA.CONFIG_SAMPLE_TIME, sampleTime);
                configBundle.putString(DATA.CONFIG_IP_ADDRESS, ipAddress);//to
                configBundle.putInt(DATA.CONFIG_SAMPLE_QUANTITY, sampleQuantity);
                openDataIntent.putExtras(configBundle);
                startActivity(openDataIntent);
    }

    private void configurebutton_rpy() {
        Intent openDataIntent = new Intent(this, RPYActivity.class);
        Bundle configBundle = new Bundle();
        configBundle.putInt(DATA.CONFIG_SAMPLE_TIME, sampleTime);
        configBundle.putString(DATA.CONFIG_IP_ADDRESS, ipAddress);//to
        configBundle.putInt(DATA.CONFIG_SAMPLE_QUANTITY, sampleQuantity);
        openDataIntent.putExtras(configBundle);
        startActivity(openDataIntent);
    }


    private void configurebutton_tables() {
        Intent openDataIntent = new Intent(this, TablesActivity.class);
        Bundle configBundle = new Bundle();
        configBundle.putInt(DATA.CONFIG_SAMPLE_TIME, sampleTime);
        configBundle.putString(DATA.CONFIG_IP_ADDRESS, ipAddress);//to
        configBundle.putInt(DATA.CONFIG_SAMPLE_QUANTITY, sampleQuantity);
        openDataIntent.putExtras(configBundle);
        startActivity(openDataIntent);
    }

    private void configurebutton_settings() {
                Intent openConfigIntent = new Intent(this, ConfigActivity.class);
                Bundle configBundle = new Bundle();
                configBundle.putString(DATA.CONFIG_IP_ADDRESS, ipAddress);
                configBundle.putInt(DATA.CONFIG_SAMPLE_TIME, sampleTime);
                configBundle.putInt(DATA.CONFIG_SAMPLE_QUANTITY, sampleQuantity);
                openConfigIntent.putExtras(configBundle);
                startActivityForResult(openConfigIntent, DATA.REQUEST_CODE_CONFIG);


    }

    private void getDataIntent(){
        Bundle dataBundle = new Bundle();
        dataBundle.putString(DATA.CONFIG_IP_ADDRESS, ipAddress);
        dataBundle.putInt(DATA.CONFIG_SAMPLE_TIME, sampleTime);
        dataIntent.putExtra(DATA.CONFIG_IP_ADDRESS, ipAddress);
        dataIntent.putExtra(DATA.CONFIG_SAMPLE_TIME, Integer.toString(sampleTime));
        dataIntent.putExtras(dataBundle);
    }


}


