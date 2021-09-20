var tempFromHumUnit = "C";      // Unit of temperature from humidity sensor
var tempFromPresUnit = "C";     // Unit of pressure from humidity sensor
var presUnit = "hPa";           // Unit of pressure
var humUnit = "P";              // Unit of humidity
var sampleTime = 1000;          // Sample time [ms]
var timer;                      // Request timer

const url = "/IOT_server/sensors_via_deamon.php?id=env";

$(document).ready(function() {

    // Writing initial value to input field
    $("#sampleTime").val(sampleTime);

    // Listener for radio inputs
    $("input:radio").change(function(e){
        var name = e.currentTarget.name; 
        if(name == "tempFromHumUnit"){
            tempFromHumUnit = e.currentTarget.value;
        }
        else if(name == "tempFromPressUnit"){
            tempFromPresUnit = e.currentTarget.value;
        }
        else if(name == "presUnit"){
            presUnit = e.currentTarget.value;
        }
        else if(name == "humUnit"){
            humUnit = e.currentTarget.value;
        }

    });

    // Listener for change of sample time
    $("#sampleTime").change(function(){
        var inputValue = Number($(this).val());
        if (inputValue != NaN){
            sampleTime = inputValue;
        }
        stopTimer();
        startTimer();
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
 * @param envObj JS object with data received from server
 */
function updateTable(envObj){

    if(tempFromHumUnit == "K"){ envObj[0].data = CelsiusToKelvin(envObj[0].data); }
    else if (tempFromHumUnit == "F"){ envObj[0].data = CelsiusToFahrenheit(envObj[0].data); }
        
    if(tempFromPresUnit == "K"){ envObj[1].data = CelsiusToKelvin(envObj[1].data); }
    else if (tempFromPresUnit == "F"){ envObj[1].data = CelsiusToFahrenheit(envObj[1].data); }
    
    if(presUnit == "mmHg"){ envObj[2].data = hPaTommHg(envObj[2].data); }
    else if (presUnit == "Bar"){ envObj[2].data = hPaToBar(envObj[2].data); }
    
    if(humUnit == "F"){ envObj[3].data = PercentToDecimal(envObj[3].data); }
   

    $("#tempFromHumValue").html((envObj[0].data).toFixed(5));
    $("#tempFromPresValue").html((envObj[1].data).toFixed(5));
    $("#presValue").html((envObj[2].data).toFixed(5));
    $("#humValue").html((envObj[3].data).toFixed(5));
}

/**
 * @brief Convert Celsius degrees to Fahrenheit degrees
 * @param val Value in Celsius degrees
 * @retval Value in Fahrenheit degrees
 */
function CelsiusToFahrenheit(val){
    return 1.8 * val + 32;
}

/**
 * @brief Convert Celsius degrees to Kelvins
 * @param val Value in Celsius degrees
 * @retval Value in Kelvins 
 */
function CelsiusToKelvin(val){
    return val + 273.15;
}

/**
 * @brief Convert hPa to mmHg
 * @param val Value in hPa 
 * @retval Value in mmHg 
 */
function hPaTommHg(val){
    return val * 0.7500616827;
}

/**
 * @brief Convert hPa to bars
 * @param val Value in hPa degrees
 * @retval Value in bars 
 */
function hPaToBar(val){
    return val / 1000;
}

/**
 * @brief Convert percents degrees to decimal fraction
 * @param val Value percents
 * @retval Value in decimal fraction 
 */
function PercentToDecimal(val){
    return val / 100;
}