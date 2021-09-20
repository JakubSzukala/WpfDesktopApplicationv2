package com.example.MobHAT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class TablesActivity extends AppCompatActivity {
    TextView textViewError;
    double Temperature, Pressure, Humidity;
    TextView Readings_TempC;
    TextView Readings_TempF;
    TextView Readings_PressurehPa;
    TextView Readings_PressuremmHg;
    TextView Readings_Humidity1;
    TextView Readings_Humidity2;
    TextView Readings_RollD;
    TextView Readings_RollR;
    TextView Readings_PitchD;
    TextView Readings_PitchR;
    TextView Readings_YawD;
    TextView Readings_YawR;
    TextView Readings_JoystickX;
    TextView Readings_JoystickY;
    TextView Readings_JoystickC;




    int sampleTime =DATA.DEFAULT_SAMPLE_TIME;
    String ipAddress =DATA.DEFAULT_IP_ADDRESS;
    int sampleQuantity =DATA.DEFAULT_SAMPLE_QUANTITY;

    /* BEGIN request timer */
    private RequestQueue queue;
    private Timer requestTimer;
    private long requestTimerTimeStamp = 0;
    private long requestTimerPreviousTime = -1;
    private boolean requestTimerFirstRequest = true;
    private boolean requestTimerFirstRequestAfterStop;
    private TimerTask requestTimerTask;
    private final Handler handler = new Handler();
    /* END request timer */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);
        textViewError = findViewById(R.id.textViewErrorMsg);
        textViewError.setText("");


        // get the Intent that started this Activity
        Intent intent = getIntent();

        // get the Bundle that stores the data of this Activity
        Bundle configBundle = intent.getExtras();
        ipAddress = configBundle.getString(DATA.CONFIG_IP_ADDRESS, DATA.DEFAULT_IP_ADDRESS);

        sampleTime = configBundle.getInt(DATA.CONFIG_SAMPLE_TIME, DATA.DEFAULT_SAMPLE_TIME);

        sampleQuantity = configBundle.getInt(DATA.CONFIG_SAMPLE_QUANTITY, DATA.DEFAULT_SAMPLE_QUANTITY);



        Log.e("149","149");
        queue = Volley.newRequestQueue(TablesActivity.this);


    }
    /**
     * @brief Create JSON file URL from IoT server IP.
     * @param ip IP address (string)
     * @retval GET request URL
     */
    private String getURL(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Temperature);

    }
    private String getURLPressure(String ipAddress) {

        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Pressure);
    }
    private String getURLHumidity(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Humidity);
    }
    private String getURLTempF(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Temperature2);

    }
    private String getURLPresmmHg(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Pressure2);

    }
    private String getURLHumi2(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Humidity2);

    }
    private String getURLRollD(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.RPY_FILE_NAME);

    }
    private String getURLPitchD(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Pitch);

    }
    private String getURLYawD(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Yaw);

    }
    private String getURLRollR(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.RPY_FILE_NAME2);

    }
    private String getURLPitchR(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Pitch2);

    }
    private String getURLYawR(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Yaw2);

    }
    private String getURLJoystickX(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.JOYSTICK_FILE_NAME);

    }
    private String getURLJoystickY(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.JOYSTICK_Y_FILE_NAME);

    }
    private String getURLJoystickC(String ipAddress) {
        Log.e("160","160");
        return ("http://" + ipAddress + "/" + DATA.JOYSTICK_C_FILE_NAME);

    }




    public void btns_onClick(View v) {

        switch (v.getId()) {
            case R.id.startBtnTable: {
                Log.e("176","176");
                startRequestTimer();
                break;
            }
            case R.id.stopBtnTable: {
                stopRequestTimerTask();
                break;
            }
            default: {
                // do nothing
            }
        }
    }
    private void errorHandling(int errorCode) {
        switch(errorCode) {
            case DATA.ERROR_TIME_STAMP:
                textViewError.setText("ERR #1");
                Log.d("errorHandling", "Request time stamp error.");
                break;
            case DATA.ERROR_NAN_DATA:
                textViewError.setText("ERR #2");
                Log.d("errorHandling", "Invalid JSON data.");
                break;
            case DATA.ERROR_RESPONSE:
                textViewError.setText("ERR #3");
                Log.d("errorHandling", "GET request VolleyError.");
                break;
            default:
                textViewError.setText("ERR ??");
                Log.d("errorHandling", "Unknown error.");
                break;
        }
    }

    /* @brief Starts new 'Timer' (if currently not exist) and schedules periodic task.
     */
    private void startRequestTimer() {
        if(requestTimer == null) {
            // set a new Timer
            requestTimer = new Timer();

            // initialize the TimerTask's job
            initializeRequestTimerTask();
            requestTimer.schedule(requestTimerTask, 0, sampleTime);

            // clear error message
            // textViewError.setText("");
        }
    }

    /* @brief Stops request timer (if currently exist)
     * and sets 'requestTimerFirstRequestAfterStop' flag.
     */
    private void stopRequestTimerTask() {
        // stop the timer, if it's not already null
        if (requestTimer != null) {
            requestTimer.cancel();
            requestTimer = null;
            requestTimerFirstRequestAfterStop = true;
        }
    }

    private double getRawDataFromResponse(String response, String item) {
        JSONObject jObject;
        double x = Double.NaN;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return x;
        }

        // Read chart data form JSON object
        try {
            x = (double)jObject.get(item);
            Log.e("229",""+x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }
    private int getRawDataFromResponseint(String response, String item) {
        JSONObject jObject;
        int x = 0;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return x;
        }

        // Read chart data form JSON object
        try {
            x = (int)jObject.get(item);
            Log.e("229",""+x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }


    /**
     * @brief Initialize request timer period task with 'Handler' post method as 'sendGetRequest'.
     */
    private void initializeRequestTimerTask() {
        requestTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() { sendGetRequest();
                        sendGetRequestPressure();
                        sendGetRequestHumidity();
                        sendGetRequestTemp2();
                        sendGetRequestPres2();
                        sendGetRequestHumi2();
                        sendGetRequestRoll1();
                        sendGetRequestRoll2();
                        sendGetRequestPitch1();
                        sendGetRequestPitch2();
                        sendGetRequestYaw1();
                        sendGetRequestYaw2();
                        sendGetRequestJoyX();
                        sendGetRequestJoyY();
                        sendGetRequestJoyC();}

                });
            }
        };
    }

    /**
     * @brief Sending GET request to IoT server using 'Volley'.
     */
    private void sendGetRequest()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURL(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandling(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestPressure()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLPressure(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingPressure(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestHumidity()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLHumidity(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingHumidity(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestTemp2()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLTempF(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingTemp2(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestPres2()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLPresmmHg(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingPress2(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestHumi2()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLHumi2(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingHumi2(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestRoll1()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLRollD(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingRoll1(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestRoll2()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLRollR(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingRoll2(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestPitch1()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLPitchD(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingPitch1(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestPitch2()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLPitchR(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingPitch2(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestYaw1()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLYawD(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingYaw1(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestYaw2()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLYawR(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingYaw2(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestJoyX()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLJoystickX(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingJoyx(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestJoyY()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLJoystickY(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingJoyY(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestJoyC()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLJoystickC(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingJoyC(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * @brief Validation of client-side time stamp based on 'SystemClock'.
     */
    private long getValidTimeStampIncrease(long currentTime)
    {
        // Right after start remember current time and return 0
        if(requestTimerFirstRequest)
        {
            requestTimerPreviousTime = currentTime;
            requestTimerFirstRequest = false;
            return 0;
        }

        // After each stop return value not greater than sample time
        // to avoid "holes" in the plot
        if(requestTimerFirstRequestAfterStop)
        {
            if((currentTime - requestTimerPreviousTime) > sampleTime)
                requestTimerPreviousTime = currentTime - sampleTime;

            requestTimerFirstRequestAfterStop = false;
        }

        // If time difference is equal zero after start
        // return sample time
        if((currentTime - requestTimerPreviousTime) == 0)
            return sampleTime;

        // Return time difference between current and previous request
        return (currentTime - requestTimerPreviousTime);
    }

    /**
     * @brief GET response handling - chart data series updated with IoT server data.
     */
//    private void drawcharts(double temp, double press, double humi)
//    {
//        // update plot series
//        double timeStamp = requestTimerTimeStamp / 1000.0; // [sec]
//
//        boolean scrollGraph1 = (timeStamp > dataGraph1MaxX);
//        dataSeries1.appendData(new DataPoint(timeStamp, temp), scrollGraph1, sampleQuantity);
//
//        // refresh chart
//        dataGraph1.onDataChanged(true, true);
//
//        // update plot series
//        boolean scrollGraph2 = (timeStamp > dataGraph2MaxX);
//        dataSeries2.appendData(new DataPoint(timeStamp, press), scrollGraph2, sampleQuantity);
//
//        // refresh chart
//        dataGraph2.onDataChanged(true, true);
//
//        // update plot series
//        boolean scrollGraph3 = (timeStamp > dataGraph3MaxX);
//        dataSeries3.appendData(new DataPoint(timeStamp, humi), scrollGraph3, sampleQuantity);
//
//        // refresh chart
//        dataGraph3.onDataChanged(true, true);
//
//    }
    private void responseHandling(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
            double Temperature = getRawDataFromResponse(response,"value");
            double tempround = Math.round(Temperature * 1000.0) / 1000.0;
//            double press = getRawDataFromResponse(response,"value");
//            double humi = getRawDataFromResponse(response,"value");
//            drawcharts(Temperature,Pressure,Humidity);

            Log.e("404","Temperature"+Temperature+"Pressure"+Pressure +"Humidity"+ Humidity);
            Readings_TempC=findViewById(R.id.value1);
            Readings_TempC.setText(""+tempround + " °C");
            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingPressure(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//           int Pressure = getRawDataFromResponseint(response,"value");
//            double humi = getRawDataFromResponse(response,"humi");
//            drawcharts(Temperature,Pressure,Humidity);
            Log.e("414","Temperature"+Temperature+"Pressure"+Pressure +"Humidity"+ Humidity);
            // remember previous time stamp
            Readings_PressurehPa=findViewById(R.id.value2);
            double PressurehPa= getRawDataFromResponse(response,"value");
            double Pressurhround = Math.round(PressurehPa * 1000.0) / 1000.0;
            Readings_PressurehPa.setText(""+Pressurhround+" hPa");
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingHumidity(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            double Humidity = getRawDataFromResponse(response,"value");
            double Humround = Math.round(Humidity * 1000.0) / 1000.0;
            Readings_Humidity1=findViewById(R.id.value3);
            Readings_Humidity1.setText(""+Humround+ " %");

            // remember previous time getURLstamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }

    private void responseHandlingTemp2(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            double Temperature = getRawDataFromResponse(response,"value");
            double Temperatureround = Math.round(Temperature * 1000.0) / 1000.0;
            Readings_TempF=findViewById(R.id.unit1);
            Readings_TempF.setText(""+Temperatureround+" °F");

            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingPress2(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            Pressure = getRawDataFromResponse(response,"value");
            double Pressureround = Math.round(Pressure * 1000.0) / 1000.0;
            Readings_PressuremmHg=findViewById(R.id.unit2);
            Readings_PressuremmHg.setText(""+Pressureround+" mmHg");

            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingHumi2(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            double Humidity = getRawDataFromResponse(response,"value");
            double Humidityround = Math.round(Humidity * 1000.0) / 1000.0;
            Readings_Humidity1=findViewById(R.id.unit3);
            Readings_Humidity1.setText(""+Humidityround + " [-]");

            // remember previous time getURLstamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingRoll1(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            double Roll = getRawDataFromResponse(response,"value");
            double Rollround = Math.round(Roll * 1000.0) / 1000.0;
            Readings_RollD=findViewById(R.id.value4);
            Readings_RollD.setText(""+ Rollround + " Deg");

            // remember previous time getURLstamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingRoll2(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            double Roll = getRawDataFromResponse(response,"value");
            double Rollround = Math.round(Roll * 1000.0) / 1000.0;
            Readings_RollR=findViewById(R.id.unit4);
            Readings_RollR.setText(""+Rollround + " Rad");

            // remember previous time getURLstamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingPitch1(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            double Pitch = getRawDataFromResponse(response,"value");
            double Pitchround = Math.round(Pitch * 1000.0) / 1000.0;
            Readings_PitchD=findViewById(R.id.value5);
            Readings_PitchD.setText(""+Pitchround + " Deg");

            // remember previous time getURLstamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingPitch2(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            double Pitch = getRawDataFromResponse(response,"value");
            double Pitchround = Math.round(Pitch * 1000.0) / 1000.0;
            Readings_PitchR=findViewById(R.id.unit5);
            Readings_PitchR.setText(""+Pitchround + " Rad");

            // remember previous time getURLstamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingYaw1(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            double Yaw = getRawDataFromResponse(response,"value");
            double Yawround = Math.round(Yaw * 1000.0) / 1000.0;
            Readings_YawD=findViewById(R.id.value6);
            Readings_YawD.setText(""+Yawround + " Deg");

            // remember previous time getURLstamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingYaw2(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            double Yaw = getRawDataFromResponse(response,"value");
            double Yawround = Math.round(Yaw * 1000.0) / 1000.0;
            Readings_YawR=findViewById(R.id.unit6);
            Readings_YawR.setText(""+Yawround + " Rad");

            // remember previous time getURLstamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingJoyx(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            int Joyx = getRawDataFromResponseint(response,"value");

            Readings_JoystickX=findViewById(R.id.value7);
            Readings_JoystickX.setText(""+Joyx + "");

            // remember previous time getURLstamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingJoyY(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            int JoyY = getRawDataFromResponseint(response,"value");

            Readings_JoystickY=findViewById(R.id.value8);
            Readings_JoystickY.setText(""+JoyY + "");

            // remember previous time getURLstamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingJoyC(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
//            double temp = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
            int JoyC = getRawDataFromResponseint(response,"value");

            Readings_JoystickC=findViewById(R.id.value9);
            Readings_JoystickC.setText(""+JoyC + "");

            // remember previous time getURLstamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }

}

