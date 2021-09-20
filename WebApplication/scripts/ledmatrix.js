
var resultColorPreview; // Result color preview HTML element 
var resultColor;        // Result color value
var R;                  // Value of red slider
var G;                  // Value of green slider
var B;                  // Value of blue slider
var selectedLEDs = [];  // Selected LEDs
var LEDobj = {};             // Object with data to send       

const url = '/IOT_server/ledmatrix_via_deamon.php';

$(document).ready(function() {

    // Assign HTML element to variable
    resultColorPreview = document.getElementById("resultColor");

    // Get values of sliders
    R = $("#slideRed").val();
    G = $("#slideGreen").val();
    B = $("#slideBlue").val();

    // Set preview color
    setResultColor(resultColorPreview);

    // Set event handler for LEDs
    $(".led").click(function(){
        
        // Change color of LED
        setResultColor(this); 
        
        // Assign RGB value to object with tag of the LED as key
        LEDobj[$(this).attr('id')] = [R, G, B];
    });
})


/**
 * @brief Get value of red slide bar, update preview color and displayed value
 * @param value Value of red slider (0-255)
 */
function changeRed(value){
    R = value;
    $("#valRed").html(value);
    setResultColor(resultColorPreview);
}

/**
 * @brief Get value of green slide bar, update preview color and displayed value
 * @param value Value of green slider (0-255)
 */
function changeGreen(value){
    G = value;
    $("#valGreen").html(value);
    setResultColor(resultColorPreview);
}

/**
 * @brief Get value of blue slide bar, update preview color and displayed value
 * @param value Value of blue slider (0-255)
 */
function changeBlue(value){
    B = value;
    $("#valBlue").html(value);
    setResultColor(resultColorPreview);
}

/**
 * @brief Set color of element to result color
 * @param element HTML element 
 */
function setResultColor(element){
    resultColor = "rgba(" + R + "," + G + "," + B + ", 0.5)";
    $(element).css("background-color", resultColor);
}

/**
 * @brief Send JSON using POST method
 */
function sendButton(){

    $.ajax({
        url : url,
        method : 'post',
        contentType : "application/json",
        data : JSON.stringify(LEDobj),
        dataType : 'json'
    })
    .done(function(response){
        console.log(response);
    });
}

/**
 * @brief Set color of LEDs to default and clear selected LEDs array
 */
function clearButton(){
    $(".led").css("background-color", "rgb(128, 128, 128)");
    LEDobj = {};
}
