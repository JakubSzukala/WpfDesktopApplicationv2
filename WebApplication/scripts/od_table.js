var AccelerometerUnit = "deg";      // Unit of roll, pitch, yaw from accelerometer
var GyroscopeUnit = "deg"           // Unit of roll, pitch, yaw from gyroscope
var sampleTime = 1000;              // Sample time [ms]
var maxStoredSamples = 1000;        // Maximum number of stored samples
var timer;                          // Request timer

const url = "/IOT_server/sensors_via_deamon.php?id=ori";

$(document).ready(function() {

    // Writing initial values to input fields
    $("#sampleTime").val(sampleTime);
    $("#storedSamples").val(maxStoredSamples);

    // Listener for radio input
    $("input:radio").change(function(e){
        var name = e.currentTarget.name; 
        if(name == "Gunit"){
            GyroscopeUnit = e.currentTarget.value;
        }
        else if(name == "Aunit"){
            AccelerometerUnit = e.currentTarget.value;
        }
    });

    // Sample time change listener
    $("#sampleTime").change(function(){
        var inputValue = Number($(this).val());
        if (inputValue != NaN){
            sampleTime = inputValue;
        }
    });

    // Maximum number of stored samples change listener
    $("#storedSamples").change(function(){
        var inputValue = Number($(this).val());
        if (inputValue != NaN){
            maxStoredSamples = inputValue;
        }
    });

})

/**
* @brief Start request timer
*/
function startTimer(){
	if(timer == null)
		timer = setInterval(ajaxGetJSON, sampleTime);
}

/**
* @brief Stop request timer
*/
function stopTimer(){
	if(timer != null) {
		clearInterval(timer);
		timer = null;
	}
}

/**
* @brief Send HTTP GET request to IoT server
*/
function ajaxGetJSON(){
	$.getJSON(url , function(data){	
        console.log(data)
        updateTable(data);
	})
}

/**
 * @brief Update values in table
 * @param oriObj JS object with data received from server
 */
function updateTable(oriObj){

    if(AccelerometerUnit == "rad"){ 
        oriObj[0].data.roll = DegreesToRadians(oriObj[0].data.roll); 
        oriObj[0].data.pitch = DegreesToRadians(oriObj[0].data.pitch); 
        oriObj[0].data.yaw = DegreesToRadians(oriObj[0].data.yaw); 
    }
        
    if(GyroscopeUnit == "rad"){ 
        oriObj[2].data.roll = DegreesToRadians(oriObj[2].data.roll); 
        oriObj[2].data.pitch = DegreesToRadians(oriObj[2].data.pitch); 
        oriObj[2].data.yaw = DegreesToRadians(oriObj[2].data.yaw);  
    }

    $("#AXValue").html((oriObj[0].data.roll).toFixed(5));
    $("#AYValue").html((oriObj[0].data.pitch).toFixed(5));
    $("#AZValue").html((oriObj[0].data.yaw).toFixed(5));
    $("#MXValue").html((oriObj[1].data.x).toFixed(5));
    $("#MYValue").html((oriObj[1].data.y).toFixed(5));
    $("#MZValue").html((oriObj[1].data.z).toFixed(5));
    $("#GXValue").html((oriObj[2].data.roll).toFixed(5));
    $("#GYValue").html((oriObj[2].data.pitch).toFixed(5));
    $("#GZValue").html((oriObj[2].data.yaw).toFixed(5));
}

/**
 * @brief Convert value from degrees to radians
 * @param value in degrees
 * @retval value in radians
 */
function DegreesToRadians(value){
    return value * Math.PI / 180;
}