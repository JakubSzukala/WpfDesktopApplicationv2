using Newtonsoft.Json.Linq;
using OxyPlot;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;
using System.Timers;
using WpfDesktopApplicationv2.Models;
using WpfDesktopApplicationv2.Stores;

namespace WpfDesktopApplicationv2.ViewModels
{
    class DataDisplayViewModel
    {
        //properties
        public ObservableCollection<MeasurementViewModel> MeasurementsVM { get; set; } // src of problems? Will observable collection trigger?
        public float SamplingTime { get; set; }
        public DataPlotViewModel ActivePlot { get; set; }


        //fields
        private readonly DataConverterModel _converter;
        private Dictionary<string, DataPoint> BroadcastDataPoints;
        private LinearAxesModel Axes;

        // fields - charts
        public DataPlotViewModel TemperatureChart { get; set; }
        public DataPlotViewModel PressureChart { get; set; }
        public DataPlotViewModel HumidityChart{ get; set; }

        // Stores
        private BroadcastPointsStore broadcastPointsStore;

        private Timer RequestTimer;


        public DataDisplayViewModel()
        {
            // data aquisition initialization
            _converter = new DataConverterModel();
            MeasurementsVM = _converter.RequestViewModelsFromServer();
            BroadcastDataPoints = _converter.RequestDataPointsFromServer(1F);

            // model containing models of available axes configuration
            Axes = new LinearAxesModel();

            //Store initialization
            broadcastPointsStore = new BroadcastPointsStore();

            // charts initialization
            TemperatureChart = new DataPlotViewModel("Temperature", Axes.Temperature, broadcastPointsStore);
            PressureChart = new DataPlotViewModel("Pressure", Axes.Pressure, broadcastPointsStore);
            HumidityChart = new DataPlotViewModel("Humidity", Axes.Humidity, broadcastPointsStore);

            ActivePlot = TemperatureChart;

            // temporary
            SamplingTime = 1F;

            SetTimer();
        }

        /// <summary>
        /// Function that sets data update routine for application.
        /// </summary>
        private void SetTimer()
        {
            RequestTimer = new Timer(SamplingTime*1000);
            
            // Update dictionary with points 
            RequestTimer.Elapsed += UpdateBroadcastDataPointsWrapper;

            // broadcast this dictionary to listening charts 
            RequestTimer.Elapsed += BroadcastDataPointsWrapper;


            RequestTimer.AutoReset = true;
            RequestTimer.Enabled = true;
        }

        /// <summary>
        /// Function that wraps RequestDataPointsFromServer of _converter to match delegate of Elapsed event.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void UpdateBroadcastDataPointsWrapper(object sender, EventArgs e)
        {
            BroadcastDataPoints = _converter.RequestDataPointsFromServer(1F);
        }

        /// <summary>
        /// Function that will publish the data in form of event to which charts will subscribe
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void BroadcastDataPointsWrapper(object sender, EventArgs e)
        {
            broadcastPointsStore.OnPointsBroadcasted(BroadcastDataPoints);
        }
    }
}
