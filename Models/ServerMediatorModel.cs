using Newtonsoft.Json.Linq;
using OxyPlot;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WpfDesktopApplicationv2.Converters;
using WpfDesktopApplicationv2.ViewModels;

namespace WpfDesktopApplicationv2.Models
{
    /// <summary>
    /// Middleman / mediator class for communication with server, that delivers data in necessary formats. 
    /// </summary>
    class ServerMediatorModel
    {
        private ServerIoT _server;
        private List<MeasurementViewModel> measurementsRaw1;
        private List<MeasurementViewModel> measurementsRaw2;
        private List<DataPoint> DataPoints;
        private Dictionary<string, List<int>> _ledsToPost;
        private ThreeDtoOneDConverter _converter;


        public ServerMediatorModel(string ip)
        {
            _server = new ServerIoT(ip);
            _converter = new ThreeDtoOneDConverter();
            measurementsRaw1 = new List<MeasurementViewModel>();
            measurementsRaw2 = new List<MeasurementViewModel>();
            DataPoints = new List<DataPoint>();
            _ledsToPost = new Dictionary<string, List<int>>();
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
            GetRawData(measurementsRaw1);

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
            GetRawData(measurementsRaw2);

            // transform it to observable collection of viewmodels
            return GetAsMeasurementViewModels();
        }


        /// <summary>
        /// Gets data from server in form of JArray and processes it into a List of VMs
        /// </summary>
        /// <returns></returns>
        private async void GetRawData(List<MeasurementViewModel> list)
        {
            // request a data from server
            Task<string> responseTaskEnv = _server.GETwithClient();
            Task<string> responseTaskRpy = _server.GETwithClientRPY();
            
            // await for response
            string responseTextEnv = await responseTaskEnv;
            string responseTextRpy = await responseTaskRpy;

            // parse as JArray object
            JArray responseJsonEnv = JArray.Parse(responseTextEnv);
            JArray responseJsonRpy = JArray.Parse(responseTextRpy);

            // convert into models lists and merge together
            List<MeasurementModel> ModelsEnv = responseJsonEnv.ToObject<List<MeasurementModel>>();
            List<Measurement3dModel> ModelsRpy = responseJsonRpy.ToObject<List<Measurement3dModel>>();

            // expand each 3d model into 3 x 1d models and connect them together
            List<MeasurementModel> SingleModelsRpy = new List<MeasurementModel>();
            foreach(var m in ModelsRpy)
            {
                //SingleModelsRpy.Concat(_converter.Convert(m)).ToList();
                var temp = _converter.Convert(m);
                SingleModelsRpy.Add(temp[0]);
                SingleModelsRpy.Add(temp[1]);
                SingleModelsRpy.Add(temp[2]);
            }

            // concatenate into final list
            List<MeasurementModel> measurementModels = ModelsEnv.Concat(SingleModelsRpy).ToList();

            list.Clear();
            foreach (var m in measurementModels)
            {
                list.Add(new MeasurementViewModel(m));
            }

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

        public async void PostLedControl(ObservableCollection<ObservableCollection<int[]>> stateMatrix)
        {
            AdjustFormat(stateMatrix);

            Task<string> responseTask = _server.POSTwithClient(_ledsToPost);

            string responseText = await responseTask;
        }
       
        public void AdjustFormat(ObservableCollection<ObservableCollection<int[]>> stateMatrix)
        {
            _ledsToPost.Clear(); // src of errors? in case results are empty dictionary check this
            for (int row = 0; row < stateMatrix.Count; row++)
            {
                for (int column = 0; column < stateMatrix[0].Count; column++)
                {
                    _ledsToPost.Add(new string($"LED{column}{row}"),
                        new List<int>(new int[3] {
                            stateMatrix[row][column][0],
                            stateMatrix[row][column][1],
                            stateMatrix[row][column][2]
                        }));
                }
            }
        }
    }
}
