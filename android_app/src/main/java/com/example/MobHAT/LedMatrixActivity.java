package com.example.MobHAT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class LedMatrixActivity extends AppCompatActivity {

    /*BEGIN VARIABLES*/
    //Ip address
    private String ipAddress = DATA.DEFAULT_IP_ADDRESS;

    //Widgets
    SeekBar seekBarR, seekBarG, seekBarB;
    View colourPreviewView;
    TextView ConnectTextView;

    //Colour variables
    private int alpha, red, green, blue, currentColour;
    int ledDefaultColour;
    Vector ledDefaultColourVec;
    Integer[][][] ledColours = new Integer[8][8][3]; //[row][column][colour selection]

    //Intent and Bundle variable
    Intent intent;
    Bundle configBundle;

    //Volley request variables
    private RequestQueue queue;
    Map<String, String> paramClear = new HashMap<String, String>();
    /*END VARIABLES*/


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.led_matrix);

        Init();
    }
    private void Init(){
        intent = getIntent();
        configBundle = intent.getExtras();

        /*BEGIN TEXT INIT*/
        ipAddress = configBundle.getString(DATA.CONFIG_IP_ADDRESS, DATA.DEFAULT_IP_ADDRESS);

        ConnectTextView=findViewById(R.id.LedConnectTextView);
        ConnectTextView.setText("Connecting to: "+getURL(ipAddress));
        /*END TEXT INIT*/

        /*BEGIN COLOUR INIT*/
        alpha=0xff;
        red=0x00;
        green=0x00;
        blue=0x00;
        ledDefaultColour= ResourcesCompat.getColor(getResources(), R.color.ledDefaultColour, null);
        ledDefaultColourVec=intToRgbConverter(ledDefaultColour);
        currentColour = ledDefaultColour;
        colourPreviewView=findViewById(R.id.ColourPreviewView);
        /*END COLOUR INIT*/

        /*BEGIN SEEKBAR INIT*/
        seekBarR=(SeekBar)findViewById(R.id.seekBarR);
        seekBarG=(SeekBar)findViewById(R.id.seekBarG);
        seekBarB=(SeekBar)findViewById(R.id.seekBarB);

        seekBarR.setMax(255);
        seekBarG.setMax(255);
        seekBarB.setMax(255);

        seekBarR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue=progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentColour=seekBarUpdate('R',progressChangedValue);
                colourPreviewView.setBackgroundColor(currentColour);
            }
        });

        seekBarG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue=progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentColour=seekBarUpdate('G',progressChangedValue);
                colourPreviewView.setBackgroundColor(currentColour);
            }
        });

        seekBarB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue=progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentColour=seekBarUpdate('B',progressChangedValue);
                colourPreviewView.setBackgroundColor(currentColour);
            }
        });
        /*END SEEKBAR INIT*/

        /*BEGIN VOLLEY REQUEST QUEUE INIT*/
        queue= Volley.newRequestQueue(this);
        for (int i=0; i<8; i++) {
            for(int j=0; j<8; j++){
                String data = "["+Integer.toString(i)+","+Integer.toString(j)+",0,0,0]";
                paramClear.put(ledIndexToTagConverter(i,j), data);
            }
        }
        /*END VOLLEY REQUEST QUEUE INIT*/
    }

    private int seekBarUpdate(char colourSelect, int changedValue){
        switch (colourSelect){
            case 'R': red=changedValue; break;
            case 'G': green=changedValue; break;
            case 'B': blue=changedValue; break;
            default: break;
        }
        alpha=(red+green+blue)/3;
        return argbToIntConverter(alpha, red, green, blue);
    }

    private int argbToIntConverter(int _alpha, int _red, int _green, int _blue){
        return (_alpha & 0xff) << 24 | (_red & 0xff) << 16 |(_green & 0xff) << 8 | (_blue & 0xff);
    }

    private String ledIndexToTagConverter(int x, int y){
        return "LED"+Integer.toString(x)+Integer.toString(y);
    }

    private Vector intToRgbConverter(int RGBColour){
        int _red = (RGBColour >> 16) & 0xff;
        int _green = (RGBColour >> 8) & 0xff;
        int _blue = RGBColour & 0xff;
        Vector rgb= new Vector(3);
        rgb.add(0, _red);
        rgb.add(1, _green);
        rgb.add(2, _blue);
        return  rgb;
    }

    private String getURL(String ip){
        return ("http://" + ip + "/" + DATA.LED_FILE_NAME);
    }

    public void ledBtn_onClick(View v) {
        switch (v.getId()){
            case R.id.ledBtnClear:{
                setAllLedColoursToDefault();
                sendLedClearRequest();
                break;
            }
            case R.id.ledBtnSend:{
                sendLedChangeRequest();
                break;
            }
            default:{/*Do nothing*/}
        }
    }

    public void View_onClick(View v) {
        v.setBackgroundColor(currentColour);
        String tag=(String)v.getTag();
        Vector index=ledTagToIndexConverter(tag);
        int _x=(int)index.get(0);
        int _y=(int)index.get(1);

        ledColours[_x][_y][0]=red;
        ledColours[_x][_y][1]=green;
        ledColours[_x][_y][2]=blue;
    }

    private Vector ledTagToIndexConverter(String tag){
        Vector _vector=new Vector(2);
        _vector.add(0, Character.getNumericValue(tag.charAt(3)));
        _vector.add(1, Character.getNumericValue(tag.charAt(4)));
        return _vector;
    }
    private String ledIndexToJsonStringConverter(int x, int y){
        String _y=Integer.toString(x);
        String _x=Integer.toString(y);
        String _red= Integer.toString(ledColours[x][y][0]);
        String _green= Integer.toString(ledColours[x][y][1]);
        String _blue= Integer.toString(ledColours[x][y][2]);
        // Example: [0,0,255,100,10]
        return "["+_x+","+_y+","+_red+","+_green+","+_blue+"]";
    }

    private boolean isLedColourNotNull(int x, int y){
        return !((ledColours[x][y][0]==null)||(ledColours[x][y][1]==null)||(ledColours[x][y][2]==null));
    }

    private void setAllLedColoursInArrayToDefault(){
        for(int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                ledColours[i][j][0]=null;
                ledColours[i][j][1]=null;
                ledColours[i][j][2]=null;
            }
        }
    }

    private void setAllLedColoursToDefault(){
        TableLayout tb=(TableLayout)findViewById(R.id.ledTableLayout);
        View ledInd;
        for(int i=0; i<8; i++) {
            for (int j = 0; j < 8; j++) {
                ledInd = tb.findViewWithTag(ledIndexToTagConverter(i, j));
                ledInd.setBackgroundColor(ledDefaultColour);
            }
        }
        setAllLedColoursInArrayToDefault();
        sendLedClearRequest();
    }

    private Map<String, String> getLedDisplayParams(){
        String led;
        String colour;
        Map<String, String> params = new HashMap<String, String>();
        for(int i=0; i<8; i++) {
            for (int j = 0; j < 8; j++) {
                if(isLedColourNotNull(j, i)){
                    led=ledIndexToTagConverter(j, i);
                    colour=ledIndexToJsonStringConverter(j, i);
                    params.put(led, colour);
                }
            }
        }
        return params;
    }
    private void sendLedChangeRequest(){
        String url = getURL(ipAddress);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ConnectTextView.setText("Connected to: "+getURL(ipAddress));
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        String msg = error.getMessage();
                        if( msg != null) {
                            Log.d("Error.Response", msg);
                            ConnectTextView.setText("Failed to Connect");
                        } else {

                            Log.d("Error.Response", "UNKNOWN");
                            ConnectTextView.setText("Failed to Connect");
                            // error type specific code
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                return getLedDisplayParams();
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void sendLedClearRequest(){
        String url = getURL(ipAddress);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        String msg = error.getMessage();
                        if( msg != null) {
                            Log.d("Error.Response", msg);
                        } else {
                            // error type specific code
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                return paramClear;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

}
