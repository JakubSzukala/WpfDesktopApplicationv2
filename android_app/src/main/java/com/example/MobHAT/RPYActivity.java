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

public class RPYActivity extends AppCompatActivity {
    double Roll, Pitch, Yaw;
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
    private final double dataGraph1MaxY =  360.0d;
    private final double dataGraph1MinY = 0.0d;

    /* Graph2 */
    private GraphView dataGraph2;
    private LineGraphSeries<DataPoint> dataSeries2;
    private final int dataGraph2MaxDataPointsNumber = 1000;
    private final double dataGraph2MaxX = 10.0d;
    private final double dataGraph2MinX =  0.0d;
    private final double dataGraph2MaxY =  360.0d;
    private final double dataGraph2MinY = 0.0d;

    /* Graph3 */
    private GraphView dataGraph3;
    private LineGraphSeries<DataPoint> dataSeries3;
    private final int dataGraph3MaxDataPointsNumber = 1000;
    private final double dataGraph3MaxX = 10.0d;
    private final double dataGraph3MinX =  0.0d;
    private final double dataGraph3MaxY = 360.0d;
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
        setContentView(R.layout.activity_rpy);
        textViewError = findViewById(R.id.textViewErrorMsg);
        textViewError.setText("");

        Intent intent = getIntent();

        Bundle configBundle = intent.getExtras();
        ipAddress = configBundle.getString(DATA.CONFIG_IP_ADDRESS, DATA.DEFAULT_IP_ADDRESS);

        sampleTime = configBundle.getInt(DATA.CONFIG_SAMPLE_TIME, DATA.DEFAULT_SAMPLE_TIME);

        sampleQuantity = configBundle.getInt(DATA.CONFIG_SAMPLE_QUANTITY, DATA.DEFAULT_SAMPLE_QUANTITY);

        dataGraph1 = (GraphView)findViewById(R.id.dataGraph1_rpy);
        dataSeries1 = new LineGraphSeries<>(new DataPoint[]{});
        dataGraph1.addSeries(dataSeries1);

        dataGraph1.getViewport().setXAxisBoundsManual(true);
        dataGraph1.getViewport().setMinX(dataGraph1MinX);
        dataGraph1.getViewport().setMaxX(dataGraph1MaxX);


        dataGraph1.getViewport().setYAxisBoundsManual(true);
        dataGraph1.getViewport().setMinY(dataGraph1MinY);
        dataGraph1.getViewport().setMaxY(dataGraph1MaxY);
        dataGraph1.getGridLabelRenderer().setHorizontalAxisTitle("Time[s]");
        dataGraph1.getGridLabelRenderer().setVerticalAxisTitle("[deg]");

        dataGraph2 = (GraphView)findViewById(R.id.dataGraph2_rpy);
        dataSeries2 = new LineGraphSeries<>(new DataPoint[]{});
        dataGraph2.addSeries(dataSeries2);

        dataGraph2.getViewport().setXAxisBoundsManual(true);
        dataGraph2.getViewport().setMinX(dataGraph2MinX);
        dataGraph2.getViewport().setMaxX(dataGraph2MaxX);

        dataGraph2.getViewport().setYAxisBoundsManual(true);
        dataGraph2.getViewport().setMinY(dataGraph2MinY);
        dataGraph2.getViewport().setMaxY(dataGraph2MaxY);
        dataGraph2.getGridLabelRenderer().setHorizontalAxisTitle("Time[s]");
        dataGraph2.getGridLabelRenderer().setVerticalAxisTitle("[deg]");
        /* END initialize GraphView */

        /* BEGIN initialize GraphView3 */
        // https://github.com/jjoe64/GraphView/wiki
        dataGraph3 = (GraphView)findViewById(R.id.dataGraph3_rpy);
        dataSeries3 = new LineGraphSeries<>(new DataPoint[]{});
        dataGraph3.addSeries(dataSeries3);

        dataGraph3.getViewport().setXAxisBoundsManual(true);
        dataGraph3.getViewport().setMinX(dataGraph3MinX);
        dataGraph3.getViewport().setMaxX(dataGraph3MaxX);

        dataGraph3.getViewport().setYAxisBoundsManual(true);
        dataGraph3.getViewport().setMinY(dataGraph3MinY);
        dataGraph3.getViewport().setMaxY(dataGraph3MaxY);
        dataGraph3.getGridLabelRenderer().setHorizontalAxisTitle("Time[s]");
        dataGraph3.getGridLabelRenderer().setVerticalAxisTitle("[deg]");
        /* END initialize GraphView */

        // Initialize Volley request queue
        queue = Volley.newRequestQueue(RPYActivity.this);

    }

    private String getURL(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.RPY_FILE_NAME);
    }
    private String getURLPitch(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Pitch);
    }
    private String getURLYaw(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_Yaw);
    }
    public void btns_onClick(View v) {
        switch (v.getId()) {
            case R.id.startBtn_rpy: {
                startRequestTimer();
                break;
            }
            case R.id.stopBtn_rpy: {
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

            requestTimer = new Timer();

            initializeRequestTimerTask();
            requestTimer.schedule(requestTimerTask, 0, sampleTime);


        }
    }

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }

    private void initializeRequestTimerTask() {
        requestTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() { sendGetRequest();sendGetRequestPitch();sendGetRequestYaw(); }
                });
            }
        };
    }

    private void sendGetRequest()
    {

        String url = getURL(ipAddress);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandling(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        queue.add(stringRequest);
    }
    private void sendGetRequestPitch()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLPitch(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingPitch(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendGetRequestYaw()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURLYaw(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandlingYaw(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

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

    private void drawcharts(double roll, double pitch, double yaw)
    {
        // update plot series
        double timeStamp = requestTimerTimeStamp / 1000.0; // [sec]

        boolean scrollGraph1 = (timeStamp > dataGraph1MaxX);
        dataSeries1.appendData(new DataPoint(timeStamp, roll), scrollGraph1, sampleQuantity);

        // refresh chart
        dataGraph1.onDataChanged(true, true);

        // update plot series
        boolean scrollGraph2 = (timeStamp > dataGraph2MaxX);
        dataSeries2.appendData(new DataPoint(timeStamp, pitch), scrollGraph2, sampleQuantity);

        // refresh chart
        dataGraph2.onDataChanged(true, true);

        // update plot series
        boolean scrollGraph3 = (timeStamp > dataGraph3MaxX);
        dataSeries3.appendData(new DataPoint(timeStamp, yaw), scrollGraph3, sampleQuantity);

        // refresh chart
        dataGraph3.onDataChanged(true, true);

    }
    private void responseHandling(String response)
    {
        if(requestTimer != null) {

            long requestTimerCurrentTime = SystemClock.uptimeMillis();
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            Roll = getRawDataFromResponse(response,"value");

            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingPitch(String response)
    {
        if(requestTimer != null) {

            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            Pitch = getRawDataFromResponse(response,"value");

            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
    private void responseHandlingYaw(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            Yaw = getRawDataFromResponse(response,"value");
            drawcharts(Roll,Pitch,Yaw);

            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }

}