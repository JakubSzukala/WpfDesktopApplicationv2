using Newtonsoft.Json.Linq;
using OxyPlot;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Diagnostics;
using System.Globalization;
using System.Text;
using System.Timers;
using WpfDesktopApplicationv2.Commands;
using WpfDesktopApplicationv2.Models;
using WpfDesktopApplicationv2.Stores;

namespace WpfDesktopApplicationv2.ViewModels
{
    class DataDisplayViewModel:INotifyPropertyChanged
    {
        //properties
        public List<MeasurementViewModel> MeasurementsVM { get; set; } // src of problems? Will observable collection trigger?
        
        //public DataPlotViewModel ActivePlot { get; set; }

        private DataPlotViewModel _activePlot;

        public DataPlotViewModel ActivePlot
        {
            get { return _activePlot; }
            set
            { 
                _activePlot = value;
                OnPropertyChanged("ActivePlot");
            }
        }


        private DataListViewModel _dataList;
        public DataListViewModel DataList
        {
            get { return _dataList; }
            set 
            { 
                _dataList = value;
                OnPropertyChanged("DataList");
            }
        }


        private string _samplingTimeBox;
        public string SamplingTimeBox
        {
            get { return _samplingTimeBox; }
            set 
            { 
                _samplingTimeBox = value;
                OnPropertyChanged("SamplingTimeBox");
            }
        }

        private string _ipAddressBox;

        public string IpAddressBox
        {
            get { return _ipAddressBox; }
            set 
            {
                _ipAddressBox = value;
                OnPropertyChanged("IpAddressBox");
            }
        }

        private string _plotToDisplayBox;
        public string PlotToDisplayBox
        {
            get { return _plotToDisplayBox; }
            set 
            {
                _plotToDisplayBox = value;
                OnPropertyChanged("PlotToDisplayBox");
            }
        }

        private string _errorMessage;
        public string ErrorMessage
        {
            get { return _errorMessage; }
            set 
            { 
                _errorMessage = value;
                OnPropertyChanged(nameof(ErrorMessage));
            }
        }


        private string _infoString;

        public string InfoString
        {
            get { return _infoString; }
            set 
            { 
                _infoString = value;
                OnPropertyChanged("InfoString");
            }
        }

        //private ConfigModel _config;
        private ConfigModel _config;

        public ConfigModel Config
        {
            get { return _config; }
            set { _config = value; }
        }


        // buttons 
        public ButtonCommand ConnectButton { get; set; }
        public ButtonCommand DisconnectButton { get; set; }
        public ButtonCommand ChangeChartButton { get; set; }
        public ButtonCommand RefreshListButton { get; set; }

        //fields
        private readonly ServerMediatorModel _mediator;
        private Dictionary<string, DataPoint> BroadcastDataPoints;
        private LinearAxesModel Axes;
        private float _timeStamp;
        

        // fields - charts
        private List<DataPlotViewModel> _charts;

        // Stores
        private BroadcastPointsStore broadcastPointsStore;
        private BroadcastMeasurementsList broadcastMeasurementsCollection;
        private ErrorStore _errorStore;

        // timer
        private Timer RequestTimer;


        public DataDisplayViewModel()
        {
            // initial values
            Config = new ConfigModel(1F, "192.168.56.5", 10);

            // data aquisition initialization
            _errorStore = new ErrorStore();
            _errorStore.ErrorState = " ";
            _errorStore.Error += ErrorStateUpdate;
            ErrorMessage = _errorStore.ErrorState;

            _mediator = new ServerMediatorModel(Config.IpAddress, _errorStore);
            MeasurementsVM = _mediator.RequestViewModelsFromServer();
            BroadcastDataPoints = _mediator.RequestDataPointsFromServer(Config.SamplingTime);
            _timeStamp = 0;

            // model containing models of available axes configuration
            Axes = new LinearAxesModel();

            //Store initialization
            broadcastPointsStore = new BroadcastPointsStore();
            broadcastMeasurementsCollection = new BroadcastMeasurementsList();

            // charts initialization, pass sampling time as a reference so it will be update if necessary 
            // but cannot be modified by a chart object
            _charts = new List<DataPlotViewModel>();
            _charts.Add(new DataPlotViewModel("TemmperatureFromHumidity", Axes.Temperature(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("TemmperatureFromPressure", Axes.Temperature(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("Pressure", Axes.Pressure(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("Humidity", Axes.Humidity(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("yaw:acceleration", Axes.Yaw(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("pitch:acceleration", Axes.Pitch(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("roll:acceleration", Axes.Roll(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("y:magnetic", Axes.Yaw(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("x:magnetic", Axes.Pitch(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("z:magnetic", Axes.Roll(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("yaw:gyroscope", Axes.Yaw(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("pitch:gyroscope", Axes.Pitch(), broadcastPointsStore, Config));
            _charts.Add(new DataPlotViewModel("roll:gyroscope", Axes.Roll(), broadcastPointsStore, Config));


            ActivePlot = _charts[0];

            // buttons initialization 
            ConnectButton = new ButtonCommand(ConnectOnClick);
            DisconnectButton = new ButtonCommand(DisconnectOnClick);
            RefreshListButton = new ButtonCommand(RefreshOnClick);
            ChangeChartButton = new ButtonCommand(SetPlotToDisplay);

            // informational text
            InfoString = "Not connected";

            // data list initialization
            DataList = new DataListViewModel(broadcastMeasurementsCollection);

            // handle event containing identifier of a plot that user picked on a list
            DataList.UserPicked += UpdatePlotToDisplayBox;
        }

        /// <summary>
        /// Button on click function, that will prepare application for connection to a server.
        /// </summary>
        private void ConnectOnClick()
        {
            // invalidate plots 
            foreach(var c in _charts)
            {
                c.ResetChart();
            }

            // reset current time stamp 
            _timeStamp = 0;

            // get sampling time
            try
            {
                Config.SamplingTime = float.Parse(SamplingTimeBox, CultureInfo.InvariantCulture);
            }
            catch (Exception e)
            {
                if(e is InvalidCastException || e is NullReferenceException)
                {
                    Config.SamplingTime = 1F;
                }
            }

            // get ip address
            Config.IpAddress = IpAddressBox;

            // reinitialize timer
            if(RequestTimer != null)
            {
                DispatchRequestTimer();
            }

            SetRequestTimer();

            // inform user about connection
            SetConnectionInfoString(true);
        }

        private void DisconnectOnClick()
        {
            // clear series from plots
            foreach (var c in _charts)
            {
                c.ResetChart();
            }

            //TemperatureChart.ResetChart();
            //PressureChart.ResetChart();
            //HumidityChart.ResetChart();

            // reset current time stamp
            _timeStamp = 0;

            // deinitialize timer
            DispatchRequestTimer();

            // inform user about disconnection
            SetConnectionInfoString(false);
        }

        /// <summary>
        /// On refresh click, request viewmodels from server and raise them in an event
        /// </summary>
        private void RefreshOnClick()
        {
            broadcastMeasurementsCollection.OnCollectionBroadcasted(_mediator.RequestViewModelsFromServer());
        }

        //private void 

        /// <summary>
        /// Function that sets data update routine for application.
        /// </summary>
        private void SetRequestTimer()
        {
            
            RequestTimer = new Timer(Config.SamplingTime*1000);
            
            // Update dictionary with points 
            RequestTimer.Elapsed += UpdateBroadcastDataPointsWrapper;

            // broadcast this dictionary to listening charts 
            RequestTimer.Elapsed += BroadcastDataPointsWrapper;

            // update time stamp
            RequestTimer.Elapsed += IncreaseTimeStamp;

            RequestTimer.AutoReset = true;
            RequestTimer.Enabled = true;
        }

        /// <summary>
        /// Dispatch timer.
        /// </summary>
        private void DispatchRequestTimer()
        {
            RequestTimer.Enabled = false;
            RequestTimer = null;
        }

        /// <summary>
        /// Update information about connection.
        /// </summary>
        /// <param name="connected"></param>
        private void SetConnectionInfoString(bool connected)
        {
            if (connected)
            {
                InfoString = "Connected to \n" + Config.IpAddress + "\n with sampling time\n " + Config.SamplingTime;
            }
            else
            {
                InfoString = "Disconnected";
            }
        }

        /// <summary>
        /// Simple handler that will take broadcasted arguments from event and assign it to PlotToDisplayBox. 
        /// This argument is an identifier string that user picked.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="identifier"></param>
        private void UpdatePlotToDisplayBox(object sender, string identifier)
        {
            PlotToDisplayBox = identifier;
        }

        /// <summary>
        /// Assign which plot will be active by identifier obtained in TextBox from user picking from list.
        /// </summary>
        private void SetPlotToDisplay()
        {
            foreach(var c in _charts)
            {
                if(_plotToDisplayBox == c.Identifier)
                {
                    ActivePlot = c;
                    ActivePlot.DataPlotModel.InvalidatePlot(true);
                }
            }
        }

        /// <summary>
        /// Function that wraps RequestDataPointsFromServer of _converter to match delegate of Elapsed event.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void UpdateBroadcastDataPointsWrapper(object sender, EventArgs e)
        {
            BroadcastDataPoints = _mediator.RequestDataPointsFromServer(_timeStamp);
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

        /// <summary>
        /// Function increasing time stamp on event trigger of a main timer.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void IncreaseTimeStamp(object sender, EventArgs e)
        {
            _timeStamp += Config.SamplingTime;
        }

        public event PropertyChangedEventHandler PropertyChanged;
        private void OnPropertyChanged(string propName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propName));
        }
        private void ErrorStateUpdate()
        {
            ErrorMessage = _errorStore.ErrorState;
        }
    }
}
