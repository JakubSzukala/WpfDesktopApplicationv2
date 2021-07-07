using Newtonsoft.Json.Linq;
using OxyPlot;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Text;
using WpfDesktopApplicationv2.ViewModels;

namespace WpfDesktopApplicationv2.Models
{
    /// <summary>
    /// Middleman / mediator class for communication with server, that delivers data in necessary formats. 
    /// </summary>
    class ServerMediatorModel
    {
        private ServerIoTmock _server;
        private List<MeasurementViewModel> measurementsRaw1;
        private List<MeasurementViewModel> measurementsRaw2;
        private List<DataPoint> DataPoints;


        public ServerMediatorModel(string ip)
        {
            _server = new ServerIoTmock();
            measurementsRaw1 = new List<MeasurementViewModel>();
            DataPoints = new List<DataPoint>();
        }


        /// <summary>
        /// Get raw data from server, transform it into dictionary of data points with names of measurements as keys.
        /// In this form data is ready to be broadcasted on used by charts easily.
        /// </summary>
        /// <param name="TimeStamp">Sampling time added to a data point.</param>
        /// <returns>It returns dictionary of string (key) and data point (value) pairs</returns>
        public Dictionary<string, DataPoint> RequestDataPointsFromServer(float TimeStamp)
        {
            // get raw data into measurementsRaw list 
            measurementsRaw1 = GetRawData();

            // transform it to dictionary of data points from oxyplot identified by measurement name
            return GetAsDataPoints(TimeStamp);
        }


        /// <summary>
        /// Get raw data from a server and prepare it for some general purpose use or to use in list view.
        /// </summary>
        /// <returns>Returns observable collection of Measurement View Models for general purpose. </returns>
        public List<MeasurementViewModel> RequestViewModelsFromServer()
        {
            // get raw data into measurementsRaw list 
            measurementsRaw2 = GetRawData();

            // transform it to observable collection of viewmodels
            return GetAsMeasurementViewModels();
        }


        /// <summary>
        /// Gets data from server in form of JArray and processes it into a List of VMs
        /// </summary>
        /// <returns></returns>
        private List<MeasurementViewModel> GetRawData()
        {
            JArray measurementsJson = _server.getMeasurements();
            List<MeasurementViewModel> temp = new List<MeasurementViewModel>();
            List<MeasurementModel> measurementsModels = measurementsJson.ToObject<List<MeasurementModel>>();
            
            foreach (var m in measurementsModels)
            {
                temp.Add(new MeasurementViewModel(m));
            }
            return temp;
        }


        /// <summary>
        /// Get data already formatted for displaying on the chart. Values can be identified by key (their name).
        /// </summary>
        /// <param name="TimeStamp">sampling time that will be added to a data point.</param>
        /// <returns>It returns dictionary of string (key) and data point (value) pairs.</returns>
        private Dictionary<string, DataPoint> GetAsDataPoints(float TimeStamp)
        {
            if(measurementsRaw1.Count > 0)
            {
                Dictionary<string, DataPoint> temp = new Dictionary<string, DataPoint>();
                foreach(var m in measurementsRaw1)
                {
                    temp.Add(m.Name, new DataPoint(TimeStamp, float.Parse(m.Data, CultureInfo.InvariantCulture)));
                }
                return temp;
            }
            else
            {
                return null;
            }
        }


        /// <summary>
        /// Constructs Observable collection from List.
        /// </summary>
        /// <returns>Observable collection of measurementviewmodels.</returns>
        private List<MeasurementViewModel> GetAsMeasurementViewModels()
        {
            return new List<MeasurementViewModel>(measurementsRaw2);
        }
       
    }
}
