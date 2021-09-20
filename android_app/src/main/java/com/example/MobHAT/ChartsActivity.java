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
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class ChartsActivity extends AppCompatActivity {
    double Temperature, Pressure, Humidity;

    int sampleTime =DATA.DEFAULT_SAMPLE_TIME;
    String ipAddress =DATA.DEFAULT_IP_ADDRESS;
    int sampleQuantity =DATA.DEFAULT_SAMPLE_QUANTITY;
    TextView textViewError;

    /* Graph1 */
    private GraphView dataGraph1;
    private LineGraphSeries<DataPoint> dataSeries1;
    private final int dataGraph1MaxDataPointsNumber = 1000;
    private final double dataGraph1MaxX = 10.0d;
    private final double dataGraph1MinX =  0.0d;
    private final double dataGraph1MaxY =  120.0d;
    private final double dataGraph1MinY = -40.0d;

    /* Graph2 */
    private GraphView dataGraph2;
    private LineGraphSeries<DataPoint> dataSeries2;
    private final int dataGraph2MaxDataPointsNumber = 1000;
    private final double dataGraph2MaxX = 10.0d;
    private final double dataGraph2MinX =  0.0d;
    private final double dataGraph2MaxY =  1260.0d;
    private final double dataGraph2MinY = 260.0d;

    /* Graph3 */
    private GraphView dataGraph3;
    private LineGraphSeries<DataPoint> dataSeries3;
    private final int dataGraph3MaxDataPointsNumber = 1000;
    private final double dataGraph3MaxX = 10.0d;
    private final double dataGraph3MinX =  0.0d;
    private final double dataGraph3MaxY =  100.0d;
    private final double dataGraph3MinY = 0.0d;

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
        setContentView(R.layout.activity_charts);
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
        dataGraph1 = (GraphView)findViewById(R.id.dataGraph1);
        dataSeries1 = new LineGraphSeries<>(new DataPoint[]{});
        dataGraph1.addSeries(dataSeries1);
        dataGraph1.getViewport().setXAxisBoundsManual(true);
        dataGraph1.getViewport().setMinX(dataGraph1MinX);
        dataGraph1.getViewport().setMaxX(dataGraph1MaxX);
        dataGraph1.getViewport().setYAxisBoundsManual(true);
        dataGraph1.getViewport().setMinY(dataGraph1MinY);
        dataGraph1.getViewport().setMaxY(dataGraph1MaxY);
        dataGraph1.getGridLabelRenderer().setHorizontalAxisTitle("Time[s]");
        dataGraph1.getGridLabelRenderer().setVerticalAxisTitle("[deg c]");

        /* END initialize GraphView */

        /* BEGIN initialize GraphView2 */
        // https://github.com/jjoe64/GraphView/wiki
        dataGraph2 = (GraphView)findViewById(R.id.dataGraph2);
        dataSeries2 = new LineGraphSeries<>(new DataPoint[]{});
        dataGraph2.addSeries(dataSeries2);
        dataGraph2.getViewport().setXAxisBoundsManual(true);
        dataGraph2.getViewport().setMinX(dataGraph2MinX);
        dataGraph2.getViewport().setMaxX(dataGraph2MaxX);
        dataGraph2.getViewport().setYAxisBoundsManual(true);
        dataGraph2.getViewport().setMinY(dataGraph2MinY);
        dataGraph2.getViewport().setMaxY(dataGraph2MaxY);
        dataGraph2.getGridLabelRenderer().setHorizontalAxisTitle("Time[s]");
        dataGraph2.getGridLabelRenderer().setVerticalAxisTitle("[mbar]");

        /* END initialize GraphView */

        /* BEGIN initialize GraphView3 */
        // https://github.com/jjoe64/GraphView/wiki
        dataGraph3 = (GraphView)findViewById(R.id.dataGraph3);
        dataSeries3 = new LineGraphSeries<>(new DataPoint[]{});
        dataGraph3.addSeries(dataSeries3);
        dataGraph3.getViewport().setXAxisBoundsManual(true);
        dataGraph3.getViewport().setMinX(dataGraph3MinX);
        dataGraph3.getViewport().setMaxX(dataGraph3MaxX);
        dataGraph3.getViewport().setYAxisBoundsManual(true);
        dataGraph3.getViewport().setMinY(dataGraph3MinY);
        dataGraph3.getViewport().setMaxY(dataGraph3MaxY);
        dataGraph3.getGridLabelRenderer().setHorizontalAxisTitle("Time[s]");
        dataGraph3.getGridLabelRenderer().setVerticalAxisTitle("[%]");
        /* END initialize GraphView */

        // Initialize Volley request queue
        queue = Volley.newRequestQueue(ChartsActivity.this);

    }
    /**
     * @brief Create JSON file URL from IoT server IP.
     * @param ip IP address (string)
     * @retval GET request URL
     */
    private String getURL(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Temperature);
    }
    private String getURLPressure(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Pressure);
    }
    private String getURLHumidity(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Humidity);
    }
    public void btns_onClick(View v) {
        switch (v.getId()) {
            case R.id.startBtn: {
                startRequestTimer();
                break;
            }
            case R.id.stopBtn: {
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


    /**
     * @brief Initialize request timer period task with 'Handler' post method as 'sendGetRequest'.
     */
    private void initializeRequestTimerTask() {
        requestTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() { sendGetRequest();sendGetRequestPressure();sendGetRequestHumidity(); }

                });
            }
        };
    }


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
    private void drawcharts(double temp, double press, double humi)
    {
        // update plot series
        double timeStamp = requestTimerTimeStamp / 1000.0; // [sec]

        boolean scrollGraph1 = (timeStamp > dataGraph1MaxX);
        dataSeries1.appendData(new DataPoint(timeStamp, temp), scrollGraph1, sampleQuantity);

        // refresh chart
        dataGraph1.onDataChanged(true, true);

        // update plot series
        boolean scrollGraph2 = (timeStamp > dataGraph2MaxX);
        dataSeries2.appendData(new DataPoint(timeStamp, press), scrollGraph2, sampleQuantity);

        // refresh chart
        dataGraph2.onDataChanged(true, true);

        // update plot series
        boolean scrollGraph3 = (timeStamp > dataGraph3MaxX);
        dataSeries3.appendData(new DataPoint(timeStamp, humi), scrollGraph3, sampleQuantity);

        // refresh chart
        dataGraph3.onDataChanged(true, true);

    }
    private void responseHandling(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
            Temperature = getRawDataFromResponse(response,"value");
//            double press = getRawDataFromResponse(response,"value");
//            double humi = getRawDataFromResponse(response,"value");
//            drawcharts(Temperature,Pressure,Humidity);

            Log.e("396","Temperature"+Temperature+"Pressure"+Pressure +"Humidity"+ Humidity);

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
            Pressure = getRawDataFromResponse(response,"value");
//            double humi = getRawDataFromResponse(response,"humi");
//            drawcharts(Temperature,Pressure,Humidity);
            Log.e("414","Temperature"+Temperature+"Pressure"+Pressure +"Humidity"+ Humidity);
            // remember previous time stamp
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
            Humidity = getRawDataFromResponse(response,"value");
            drawcharts(Temperature,Pressure,Humidity);
            Log.e("431","Temperature"+Temperature+"Pressure"+Pressure +"Humidity"+ Humidity);


            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
}
