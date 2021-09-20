package com.example.MobHAT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class ConfigActivity extends AppCompatActivity {

    /* BEGIN config textboxes */
    EditText ipEditText;
    EditText sampleTimeEditText;
    EditText sampleQuantity;
    /* END config textboxes */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_acivity);

        // get the Intent that started this Activity
        Intent intent = getIntent();

        // get the Bundle that stores the data of this Activity
        Bundle configBundle = intent.getExtras();

        // set ip text in box
        ipEditText = findViewById(R.id.ipEditTextConfig);
        String ip = configBundle.getString(DATA.CONFIG_IP_ADDRESS, DATA.DEFAULT_IP_ADDRESS);
        ipEditText.setText(ip);

        ipEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                DATA.DEFAULT_IP_ADDRESS = ipEditText.getText().toString();

            }
        });

        // set sample time tex in box
        sampleTimeEditText = findViewById(R.id.sampleTimeEditTextConfig);
        int st = configBundle.getInt(DATA.CONFIG_SAMPLE_TIME, DATA.DEFAULT_SAMPLE_TIME);
        sampleTimeEditText.setText(Integer.toString(st));
        sampleTimeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                DATA.DEFAULT_SAMPLE_TIME = Integer.parseInt(sampleTimeEditText.getText().toString());

            }
        });


        //set sample quantity in box
        sampleQuantity = findViewById(R.id.DataLimitEditTextConfig);
        int sq = configBundle.getInt(DATA.CONFIG_SAMPLE_QUANTITY, DATA.DEFAULT_SAMPLE_QUANTITY);
        sampleQuantity.setText(Integer.toString(sq));
        sampleQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                DATA.DEFAULT_SAMPLE_QUANTITY = Integer.parseInt(sampleQuantity.getText().toString());

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(DATA.CONFIG_IP_ADDRESS, ipEditText.getText().toString());
        intent.putExtra(DATA.CONFIG_SAMPLE_TIME, sampleTimeEditText.getText().toString());
        intent.putExtra(DATA.CONFIG_SAMPLE_QUANTITY, sampleQuantity.getText().toString());
    setResult(RESULT_OK, intent);
    super.onBackPressed();
}
}
