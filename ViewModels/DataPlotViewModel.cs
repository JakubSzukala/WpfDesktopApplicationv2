using OxyPlot;
using OxyPlot.Axes;
using OxyPlot.Series;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Text;
using System.Windows.Threading;
using WpfDesktopApplicationv2.Stores;

namespace WpfDesktopApplicationv2.ViewModels
{
    class DataPlotViewModel:INotifyPropertyChanged
    {
        // properties
        public PlotModel DataPlotModel { get; set; }

        // fields
        public readonly string Identifier;
        BroadcastPointsStore _broadcast;

        // events
        public event PropertyChangedEventHandler PropertyChanged;

        public DataPlotViewModel(string identifier, LinearAxis axis, BroadcastPointsStore broadcast)
        {
            // assign identifier
            Identifier = identifier;

            // initialize plot on the basis of this identifier
            DataPlotModel = new PlotModel() { Title = Identifier };
            DataPlotModel.Axes.Add(axis);
            DataPlotModel.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Bottom,
                Minimum = 0,
                Maximum = 10,
                Key = "Horizontal",
                Unit = "sec",
                Title = "Time"
            });

            // add empty series for now
            DataPlotModel.Series.Add(new LineSeries() { Title = Identifier + " data series", Color = OxyColor.Parse("#FFFF0000") });

            // initialize broadcast on which plot will receive dictionary with points
            _broadcast = broadcast;

            // subscribe to event
            _broadcast.PointsBroadcasted += Update;
        }

        public void Update(object sender, Dictionary<string, DataPoint> dictionary)// object sender, event args
        {
            // check if series exists and can be reffered
            try
            {
                LineSeries lineSeries = DataPlotModel.Series[0] as LineSeries;
                lineSeries.Points.Add(PickMeasurement(dictionary));
            }
            // if not create one
            catch
            {
                DataPlotModel.Series.Add(new LineSeries() { Title = Identifier + " data series", Color = OxyColor.Parse("#FFFF0000") });
                LineSeries lineSeries = DataPlotModel.Series[0] as LineSeries;
                lineSeries.Points.Add(PickMeasurement(dictionary));
            }

            DataPlotModel.InvalidatePlot(true);
        }

        /// <summary>
        /// Pick a measurement from broadcasted dictionary of points for different measurements.
        /// Measurement is picked based on identifier.
        /// </summary>
        private DataPoint PickMeasurement(Dictionary<string, DataPoint> dictionary)
        {
            foreach(var m in dictionary)
            {
                if(m.Key == Identifier)
                {
                    return m.Value;
                }
            }
            return new DataPoint(0, 0);
        }

        public void ResetChart()
        {
            DataPlotModel.Series.Clear();
            DataPlotModel.InvalidatePlot(true);
        }

        public void OnPropertyChanged(string name)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));
        }
    }
}
