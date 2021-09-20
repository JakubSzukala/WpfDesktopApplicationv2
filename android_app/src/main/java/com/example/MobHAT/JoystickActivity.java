package com.example.MobHAT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class JoystickActivity extends AppCompatActivity {
    TextView textViewError;
int Joystick_x, Joystick_y, Joystick_c;
    int sampleTime =DATA.DEFAULT_SAMPLE_TIME;
    String ipAddress =DATA.DEFAULT_IP_ADDRESS;
    int sampleQuantity =DATA.DEFAULT_SAMPLE_QUANTITY;
    TextView z_val;

    /* Graph1 */
    private GraphView dataGraph_joystick;
    private PointsGraphSeries<DataPoint> dataSeriesx;
    private final int dataGraph1MaxDataPointsNumber = 1000;
    private final double dataGraph1MaxX = 10.0d;
    private final double dataGraph1MinX =  -10.0d;
    private final double dataGraph1MaxY =  10.0d;
    private final double dataGraph1MinY = -10.0d;

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
        setContentView(R.layout.activity_joystick);
        textViewError = findViewById(R.id.textViewErrorMsg);
        textViewError.setText("");
        // get the Intent that started this Activity
        Intent intent = getIntent();

        // get the Bundle that stores the data of this Activity
        Bundle configBundle = intent.getExtras();
        ipAddress = configBundle.getString(DATA.CONFIG_IP_ADDRESS, DATA.DEFAULT_IP_ADDRESS);

        sampleTime = configBundle.getInt(DATA.CONFIG_SAMPLE_TIME, DATA.DEFAULT_SAMPLE_TIME);

        sampleQuantity = configBundle.getInt(DATA.CONFIG_SAMPLE_QUANTITY, DATA.DEFAULT_SAMPLE_QUANTITY);

        /* BEGIN initialize GraphView1 */
        // https://github.com/jjoe64/GraphView/wiki
        dataGraph_joystick = (GraphView)findViewById(R.id.dataGraph_joystick);
        dataSeriesx = new PointsGraphSeries<>(new DataPoint[]{});
        dataGraph_joystick.addSeries(dataSeriesx);
        dataGraph_joystick.getViewport().setXAxisBoundsManual(true);
        dataGraph_joystick.getViewport().setMinX(dataGraph1MinX);
        dataGraph_joystick.getViewport().setMaxX(dataGraph1MaxX);

        dataGraph_joystick.getViewport().setYAxisBoundsManual(true);
        dataGraph_joystick.getViewport().setMinY(dataGraph1MinY);
        dataGraph_joystick.getViewport().setMaxY(dataGraph1MaxY);
        //dataGraph_joystick.getGridLabelRenderer().setHorizontalAxisTitle("X");
       //dataGraph_joystick.getGridLabelRenderer().setVerticalAxisTitle("Y");
        //dataGraph_joystick.getGridLabelRenderer().setVerticalAxisTitleTextSize(40);

        /* END initialize GraphView */

        // Initialize Volley request queue
        queue = Volley.newRequestQueue(JoystickActivity.this);

    }
    /**
     * @brief Create JSON file URL from IoT server IP.
     * @param ipAddress IP address (string)
     * @retval GET request URL
     */
    private String getURL(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.JOYSTICK_FILE_NAME);
    }
    private String getURLY(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.JOYSTICK_Y_FILE_NAME);
    }
    private String getURLC(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.JOYSTICK_C_FILE_NAME);
    }

    public void btns_onClick(View v) {
        switch (v.getId()) {
            case R.id.startBtn_joy: {
                startRequestTimer();
                break;
            }
            case R.id.stopBtn_joy: {
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
        int t = 0;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return t;
        }

        // Read chart data form JSON object
        try {
            t = (int)jObject.get(item);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return t;
    }


    /**
     * @brief Initialize request timer period task with 'Handler' post method as 'sendGetRequest'.
     */
    private void initializeRequestTimerTask() {
        requestTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() { sendGetRequest();sendGetRequestY();sendGetRequestC(); }
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
    private void sendGetRequestY()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLY(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingY(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestC()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLC(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingC(response); }
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
    private void drawcharts(int x, int y)
    {
        // update plot series
        double timeStamp = requestTimerTimeStamp / 1000.0; // [sec]

        boolean scrollGraph1 = (timeStamp > dataGraph1MaxX);
        dataSeriesx.resetData(generateData(x,y));
        dataSeriesx.appendData(new DataPoint(x, y), scrollGraph1, sampleQuantity);

        // refresh chart
        dataGraph_joystick.onDataChanged(true, true);

    }
    private void responseHandling(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
            Joystick_x = (int) getRawDataFromResponse(response,"value");

            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingY(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
           Joystick_y = (int) getRawDataFromResponse(response,"value");


            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingC(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response

            int z = (int) getRawDataFromResponse(response,"value");


            z_val = findViewById(R.id.z_value);
            //String z_string = int.toString(z);
            String z_string = String.valueOf(z);
            String z_text = "Z value : ";
            String full_z  =z_text + z_string;
            z_val.setText(full_z);


            drawcharts(Joystick_x,Joystick_y);
Log.e("374","x"+Joystick_x+"y"+Joystick_y+"C"+Joystick_c);
            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    /**
     * @brief Swaps old Datapoints for new ones.
     */
    private DataPoint[] generateData(int x, int y) {
        int count = 1;
        DataPoint[] values = new DataPoint[1];
        for (int i=0; i<count; i++) {
            int horizontal = x;
            int vertical = y;
            DataPoint v = new DataPoint(horizontal, vertical);
            values[i] = v;
        }
        return values;
    }

}
