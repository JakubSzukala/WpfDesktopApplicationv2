package com.example.MobHAT;

public final class DATA {
    // activities request codes
    public final static int REQUEST_CODE_CONFIG = 1;

    // configuration info: names and default values
    public final static String CONFIG_IP_ADDRESS = "ipAddress";
    public static String DEFAULT_IP_ADDRESS = "192.168.56.101";
    public final static String CONFIG_SAMPLE_TIME = "sampleTime";
    public static int DEFAULT_SAMPLE_TIME = 500;

    public final static String CONFIG_SAMPLE_QUANTITY = "sampleQuantity";
    public static int DEFAULT_SAMPLE_QUANTITY = 100;

    // error codes
    public final static int ERROR_TIME_STAMP = -1;
    public final static int ERROR_NAN_DATA = -2;
    public final static int ERROR_RESPONSE = -3;

    // IoT server data
    public final static String FILE_NAME_Temperature = "measurements/temperature_C.php";
    public final static String FILE_NAME_Pressure = "measurements/pressure_hPa.php";
    public final static String FILE_NAME_Humidity = "measurements/humidity_%.php";
    public final static String FILE_NAME_Temperature2 = "measurements/temperature_F.php";
    public final static String FILE_NAME_Pressure2 = "measurements/pressure_mmHg.php";
    public final static String FILE_NAME_Humidity2 = "measurements/humidity_-.php";
    public final static String FILE_NAME_TABLES = "data_arrayAN.json";
    public final static String LED_FILE_NAME = "led_display.php";
    public final static String JOYSTICK_FILE_NAME = "measurements/joy_counter_x_click_pos.json" ;
    public final static String JOYSTICK_Y_FILE_NAME = "measurements/joy_counter_y_click_pos.json";
    public final static String JOYSTICK_C_FILE_NAME = "measurements/joy_counter_c_click_pos.json";
    public final static String RPY_FILE_NAME = "measurements/roll_deg.php";
    public final static String FILE_NAME_Pitch = "measurements/pitch_deg.php";
    public final static String FILE_NAME_Yaw = "measurements/yaw_deg.php";
    public final static String RPY_FILE_NAME2 = "measurements/roll_rad.php";
    public final static String FILE_NAME_Pitch2 = "measurements/pitch_rad.php";
    public final static String FILE_NAME_Yaw2 = "measurements/yaw_rad.php";
}
