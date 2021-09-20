var sampleTime = 100;					// Sample time [ms]
var sampleTimeSec = sampleTime / 1000;	// Sample time [s]
var maxStoredSamples = 100;				// Maximum number of stored samples

var timeVec;				// Time vector common for all graphs
var AXVec, AYVec, AZVec;	// Vectors of roll, pitch, yaw values from accelerometer
var MXVec, MYVec, MZVec;	// Vectors of x, y, z values from magnetometer
var GXVec, GYVec, GZVec;	// Vectors of roll, pitch and yaw from gyroscope
var lastTimeStamp;			// Last time stamp [s]

var AchartContext;	// Context of accelerometer chart
var MchartContext;	// Context of magnetometer chart
var GchartContext;	// Context of gyroscope chart
var Achart;			// Chart.js object for accelerometer chart
var Mchart;			// Chart.js object for magnetometer chart
var Gchart;			// Chart.js object for gyroscope chart

var timer;				// Requst timer
var notInit = true;		// Graphs initialized flag

const url = "/IOT_server/sensors_via_deamon.php?id=ori"

/**
 * @brief Add new value to next data point
 * @param data object with received data
 */
function addData(data){
    if(AXVec.length > maxStoredSamples)
    {
        removeOldData();
        lastTimeStamp += sampleTimeSec;
		timeVec.push(lastTimeStamp.toFixed(4));
    }
    
	AXVec.push(data[0].data.roll);
	AYVec.push(data[0].data.pitch);
	AZVec.push(data[0].data.yaw);
	MXVec.push(data[1].data.x);
	MYVec.push(data[1].data.y);
	MZVec.push(data[1].data.z);
	GXVec.push(data[2].data.roll);
	GYVec.push(data[2].data.pitch);
	GZVec.push(data[2].data.yaw);

	Achart.update();
	Mchart.update();
	Gchart.update();
}

/**
* @brief Remove oldest data point.
*/
function removeOldData(){
	timeVec.splice(0,1);
    AXVec.splice(0,1);
	AYVec.splice(0,1);
	AZVec.splice(0,1);
	MXVec.splice(0,1);
	MYVec.splice(0,1);
	MZVec.splice(0,1);
	GXVec.splice(0,1);
	GYVec.splice(0,1);
	GZVec.splice(0,1);
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

$(document).ready(function() {

    timer = null;

    // Writing initial values to input fields
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
		}
    });

    // Max stored samples change listener
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
 * @brief Call initialization of graphs if not initailized
 */
function graphsInit(){
	if(notInit){
		AChartInit();
		MChartInit();
		GChartInit();
		notInit = false;
	}
}

/**
 * @brief Initialize accelerometer graph
 */
function AChartInit(){
    // array with consecutive integers: <0, maxSamplesNumber-1>
	timeVec = [...Array(maxStoredSamples).keys()]; 
	// scaling all values ​​times the sample time 
	timeVec.forEach(function(p, i) {this[i] = (this[i]*sampleTimeSec).toFixed(4);}, timeVec);

	// last value of 'timeVec'
	lastTimeStamp =+timeVec[timeVec.length-1]; 

	// empty array
	AXVec = []; 
	AYVec = [];
	AZVec = [];

	// get chart context from 'canvas' element
	AchartContext = $("#Achart")[0].getContext('2d');

	Chart.defaults.global.elements.point.radius = 1;
	
	Achart = new Chart(AchartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'timeVec' as label, 'tempFromHumVec' and 'tempFromPresVec' as dataset.data
		data: {
			labels: timeVec,
			datasets: [
			{
				fill: false,
				label: 'Roll',
				backgroundColor: 'rgba(255, 0, 0, 0.75)',
				borderColor: 'rgba(255, 0, 0, 0.75)',
				data: AXVec,
				lineTension: 0
			},
			{
				fill: false,
				label: 'Pitch',
				backgroundColor: 'rgba(0, 255, 0, 0.75)',
				borderColor: 'rgba(0, 255, 0, 0.75)',
				data: AYVec,
				lineTension: 0
			},
			{
				fill: false,
				label: 'Yaw',
				backgroundColor: 'rgba(0, 0, 255, 0.75)',
				borderColor: 'rgba(0, 0, 255, 0.75)',
				data: AZVec,
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
						labelString: 'Angular position'
					},
					ticks: {
						suggestedMin: 0,
						suggestedMax: 350
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
	
	AXVec = Achart.data.datasets[0].data;
	AYVec = Achart.data.datasets[1].data;
	AZVec = Achart.data.datasets[2].data;
	timeVec = Achart.data.labels;
}

/**
 * Initialize magnetometer graph
 */
function MChartInit(){
	// empty array
	MXVec = []; 
	MYVec = [];
	MZVec = [];

	// get chart context from 'canvas' element
	MchartContext = $("#Mchart")[0].getContext('2d');

	Chart.defaults.global.elements.point.radius = 1;
	
	Mchart = new Chart(MchartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'timeVec' as label, 'tempFromHumVec' and 'tempFromPresVec' as dataset.data
		data: {
			labels: timeVec,
			datasets: [
			{
				fill: false,
				label: 'X',
				backgroundColor: 'rgba(255, 0, 0, 0.75)',
				borderColor: 'rgba(255, 0, 0, 0.75)',
				data: MXVec,
				lineTension: 0
			},
			{
				fill: false,
				label: 'Y',
				backgroundColor: 'rgba(0, 255, 0, 0.75)',
				borderColor: 'rgba(0, 255, 0, 0.75)',
				data: MYVec,
				lineTension: 0
			},
			{
				fill: false,
				label: 'Z',
				backgroundColor: 'rgba(0, 0, 255, 0.75)',
				borderColor: 'rgba(0, 0, 255, 0.75)',
				data: MZVec,
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
						labelString: 'Magnetic induction'
					},
					ticks: {
						suggestedMin: -70,
						suggestedMax: 70
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
	
	MXVec = Mchart.data.datasets[0].data;
	MYVec = Mchart.data.datasets[1].data;
	MZVec = Mchart.data.datasets[2].data;
}

/**
 * @brief Initialize gyroscope graph
 */
function GChartInit(){
	// empty array
	GXVec = []; 
	GYVec = [];
	GZVec = [];

	// get chart context from 'canvas' element
	GchartContext = $("#Gchart")[0].getContext('2d');

	Chart.defaults.global.elements.point.radius = 1;
	
	Gchart = new Chart(GchartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'timeVec' as label, 'tempFromHumVec' and 'tempFromPresVec' as dataset.data
		data: {
			labels: timeVec,
			datasets: [
			{
				fill: false,
				label: 'Roll',
				backgroundColor: 'rgba(255, 0, 0, 0.75)',
				borderColor: 'rgba(255, 0, 0, 0.75)',
				data: GXVec,
				lineTension: 0
			},
			{
				fill: false,
				label: 'Pitch',
				backgroundColor: 'rgba(0, 255, 0, 0.75)',
				borderColor: 'rgba(0, 255, 0, 0.75)',
				data: GYVec,
				lineTension: 0
			},
			{
				fill: false,
				label: 'Yaw',
				backgroundColor: 'rgba(0, 0, 255, 0.75)',
				borderColor: 'rgba(0, 0, 255, 0.75)',
				data: GZVec,
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
						labelString: 'Angular position'
					},
					ticks: {
						suggestedMin: 0,
						suggestedMax: 360
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
	
	GXVec = Gchart.data.datasets[0].data;
	GYVec = Gchart.data.datasets[1].data;
	GZVec = Gchart.data.datasets[2].data;
}
