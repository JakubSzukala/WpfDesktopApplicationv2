using OxyPlot.Axes;
using System;
using System.Collections.Generic;
using System.Text;

namespace WpfDesktopApplicationv2.Models
{
    public class LinearAxesModel
    {
        public readonly LinearAxis Temperature;
        public readonly LinearAxis Pressure;
        public readonly LinearAxis Humidity;
        public readonly LinearAxis Roll;
        public readonly LinearAxis Pitch;
        public readonly LinearAxis Yaw;
        public readonly LinearAxis Generic;

        public LinearAxesModel()
        {
            Temperature = new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = -30,
                Maximum = 105,
                Key = "Vertical",
                Unit = "*C",
                Title = "Temperature"
            };

            Pressure = new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = 260,
                Maximum = 1260,
                Key = "Vertical",
                Unit = "hPa",
                Title = "Pressure"
            };

            Humidity = new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = 20,
                Maximum = 80,
                Key = "Vertical",
                Unit = "%",
                Title = "Humidity"
            };

            Roll = new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = -500,
                Maximum = 500,
                Key = "Vertical",
                Unit = "°",
                Title = "Roll"
            };
            Pitch = new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = -500,
                Maximum = 500,
                Key = "Vertical",
                Unit = "°",
                Title = "Pitch"
            };

            Yaw = new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = -500,
                Maximum = 500,
                Key = "Vertical",
                Unit = "°",
                Title = "Yaw"
            };

            Generic = new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = 0,
                Maximum = 10,
                Key = "Vertical",
                Unit = "",
                Title = ""
            };
        }
    }
}
