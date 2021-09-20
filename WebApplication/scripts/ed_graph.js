var sampleTime = 100;					// Sample time [ms]
var sampleTimeSec = sampleTime / 1000;	// Sample time [s]
var maxStoredSamples = 100;				// Maximum number of stored samples

var timeVec;			// Time vector common for all graphs
var tempFromPresVec;	// Vector of temperature values from pressure sensor
var tempFromHumVec;		// Vector of temperature values from humidity sensor
var humVec;				// Vector of humidity values
var presVec;			// Vector of pressure values
var lastTimeStamp;		// Last time stamp [s]

var tempChartContext;	// Context of temperature chart
var presChartContext;	// Context of pressure chart
var humChartContext;	// Context of humidity chart
var tempChart;			// Charts.js object for temperature
var presChart;			// Charts.js object for pressure
var humChart;			// Charts.js object for humidity

var timer;				// Request timer
var notInit = true;		// Graphs initialized flag

const url = "/IOT_server/sensors_via_deamon.php?id=env";	

$(document).ready(function() {

    timer = null;

    // Write initial values to input fields
    $("#sampleTime").val(sampleTime);
    $("#storedSamples").val(maxStoredSamples);

    // Sample time change listener
    $("#sampleTime").change(function(){
		if(notInit){
			var inputValue = Number($(this).val());
			if (inputValue != NaN){
				sampleTime = inputValue;
			}
			sampleTimeSec = sampleTime / 1000;
			stopTimer();
			startTimer();
		}
    });

    // Maximum number of stored samples change listener
    $("#storedSamples").change(function(){
		if(notInit){
			var inputValue = Number($(this).val());
			if (inputValue != NaN){
				maxStoredSamples = inputValue;
			}
		}
    });

    $("#graphs").css("margin-top", $("#menu").height() + 8);

})

/**
 * @brief Add new value to next data point
 * @param data object with received data
 */
function addData(data){
	var newTempFromHumValue = data[0].data;
	var newTempFromPresValue = data[1].data;
	var newPresValue = data[2].data;
	var newHumValue = data[3].data;

	if(humVec.length > maxStoredSamples){
		removeOldData();
		lastTimeStamp+= sampleTimeSec;
		timeVec.push(lastTimeStamp.toFixed(4));
	}
	
    tempFromHumVec.push(newTempFromHumValue);
	tempFromPresVec.push(newTempFromPresValue);
    presVec.push(newPresValue);
	humVec.push(newHumValue);
	presChart.update();
	humChart.update();
	tempChart.update();
}

/**
 * @brief Call initialization of graphs if not initailized
 */
function graphsInit(){
	if(notInit){
		tempChartInit();
		humChartInit();
		presChartInit();
		notInit = false;
	}
}

/**
 * @brief Remove oldest data point
 */
function removeOldData(){
    timeVec.splice(0,1);
	tempFromHumVec.splice(0,1);
	tempFromPresVec.splice(0,1);
    presVec.splice(0,1);
    humVec.splice(0,1);
}

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
	$.getJSON(url, function(data){	
		addData(data);
	})
}

/**
 * @brief Initialize temperature graph
 */
function tempChartInit(){
    // array with consecutive integers: <0, maxSamplesNumber-1>
	timeVec = [...Array(maxStoredSamples).keys()]; 
	// scaling all values ​​times the sample time 
	timeVec.forEach(function(p, i) {this[i] = (this[i]*sampleTimeSec).toFixed(4);}, timeVec);

	// last value of 'timeVec'
	lastTimeStamp =+timeVec[timeVec.length-1]; 

	// empty array
	tempFromHumVec = []; 
	tempFromPresVec = [];

	// get chart context from 'canvas' element
	tempChartContext = $("#tempChart")[0].getContext('2d');

	Chart.defaults.global.elements.point.radius = 1;
	
	tempChart = new Chart(tempChartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'timeVec' as label, 'tempFromHumVec' and 'tempFromPresVec' as dataset.data
		data: {
			labels: timeVec,
			datasets: [
			{
				fill: false,
				label: 'Temperature from humidity sensor',
				backgroundColor: 'rgba(255, 0, 0, 0.75)',
				borderColor: 'rgba(255, 0, 0, 0.75)',
				data: tempFromHumVec,
				lineTension: 0
			},
			{
				fill: false,
				label: 'Temperature from pressure sensor',
				backgroundColor: 'rgba(0, 255, 0, 0.75)',
				borderColor: 'rgba(0, 255, 0, 0.75)',
				data: tempFromPresVec,
				lineTension: 0
            }
            ]
		},

		// Configuration options
		options: {
			responsive: true,
			maintainAspectRatio: false,
			animation: false,
			scales: {
                xAxes: [{
                    gridLines: {
                        color: 'rgba(255, 255, 255, 0.5)',
						zeroLineColor: 'rgba(255, 255, 255, 0.5)'
                    }
                }],
				yAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Temperature [&degC]'
					},
					ticks: {
						suggestedMin: -30,
						suggestedMax: 105 
                    },
                    gridLines: {
                        color: 'rgba(255, 255, 255, 0.5)',
						zeroLineColor: 'rgba(255, 255, 255, 0.5)'
                    }
				}],es: [{
					scaleLabel: {
						display: true,
						labelString: 'Time [s]'
					}
				}]
			}
		}
	});
	
	tempFromHumVec = tempChart.data.datasets[0].data;
	tempFromPresVec = tempChart.data.datasets[1].data;
	timeVec = tempChart.data.labels;
}

/**
 * @brief Initialize pressure graph
 */
function presChartInit(){
	// empty array
	presVec = []; 

	// get chart context from 'canvas' element
	presChartContext = $("#presChart")[0].getContext('2d');

	Chart.defaults.global.elements.point.radius = 1;

	presChart = new Chart(presChartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'timeVec' as labels, 'presVec' as dataset.data
		data: {
			labels: timeVec,
			datasets: [
			{
				fill: false,
				label: 'Pressure',
				backgroundColor: 'rgba(255, 255, 0, 0.75)',
				borderColor: 'rgba(255, 255, 0, 0.75)',
				data: presVec,
				lineTension: 0
			}
			]
		},

		// Configuration options
		options: {
			responsive: true,
			maintainAspectRatio: false,
			animation: false,
			scales: {
				xAxes: [{
					gridLines: {
						color: 'rgba(255, 255, 255, 0.5)',
						zeroLineColor: 'rgba(255, 255, 255, 0.5)'
					}
				}],
				yAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Pressure [hPa]'
					},
					ticks: {
						suggestedMin: 260,
						suggestedMax: 1260 
					},
					gridLines: {
						color: 'rgba(255, 255, 255, 0.5)',
						zeroLineColor: 'rgba(255, 255, 255, 0.5)'
					}
				}],es: [{
					scaleLabel: {
						display: true,
						labelString: 'Time [s]'
					}
				}]
			}
		}
	});

	presVec = presChart.data.datasets[0].data;
}

/**
 * @brief Initialize humidity graph
 */
function humChartInit(){
	// empty array
	humVec = []; 

	// get chart context from 'canvas' element
	humChartContext = $("#humChart")[0].getContext('2d');

	Chart.defaults.global.elements.point.radius = 1;

	humChart = new Chart(humChartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'timeVec' as labels, 'rollVec' as dataset.data
		data: {
			labels: timeVec,
			datasets: [
			{
				fill: false,
				label: 'Humidity',
				backgroundColor: 'rgba(0, 0, 255, 0.75)',
				borderColor: 'rgba(0, 0, 255, 0.75)',
				data: humVec,
				lineTension: 0
			}
			]
		},

		// Configuration options
		options: {
			responsive: true,
			maintainAspectRatio: false,
			animation: false,
			scales: {
				xAxes: [{
					gridLines: {
						color: 'rgba(255, 255, 255, 0.5)',
						zeroLineColor: 'rgba(255, 255, 255, 0.5)'
					}
				}],
				yAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Humidity [%]'
					},
					ticks: {
						suggestedMin: 0,
						suggestedMax: 100 
					},
					gridLines: {
						color: 'rgba(255, 255, 255, 0.5)',
						zeroLineColor: 'rgba(255, 255, 255, 0.5)'
					}
				}],es: [{
					scaleLabel: {
						display: true,
						labelString: 'Time [s]'
					}
				}]
			}
		}
	});

	humVec = humChart.data.datasets[0].data;
}