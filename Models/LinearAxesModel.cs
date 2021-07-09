using OxyPlot.Axes;
using System;
using System.Collections.Generic;
using System.Text;

namespace WpfDesktopApplicationv2.Models
{
    public class LinearAxesModel
    { 
        public LinearAxis Temperature()
        {
            return new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = -30,
                Maximum = 105,
                Key = "Vertical",
                Unit = "*C",
                Title = "Temperature"
            };
        }

        public LinearAxis Pressure()
        {
            return new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = 260,
                Maximum = 1260,
                Key = "Vertical",
                Unit = "hPa",
                Title = "Pressure"
            };
        }

        public LinearAxis Humidity()
        {
            return new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = 20,
                Maximum = 80,
                Key = "Vertical",
                Unit = "%",
                Title = "Humidity"
            };
        }

        public LinearAxis Roll()
        {
            return new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = -500,
                Maximum = 500,
                Key = "Vertical",
                Unit = "°",
                Title = "Roll"
            };
        }

        public LinearAxis Pitch()
        {
            return new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = -500,
                Maximum = 500,
                Key = "Vertical",
                Unit = "°",
                Title = "Pitch"
            };
        }

        public LinearAxis Yaw()
        {
            return new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = -500,
                Maximum = 500,
                Key = "Vertical",
                Unit = "°",
                Title = "Yaw"
            };
        }

        public LinearAxis Generic()
        {
            return new LinearAxis()
            {
                Position = AxisPosition.Left,
                Minimum = 0,
                Maximum = 10,
                Key = "Vertical",
                Unit = "",
                Title = ""
            };
        }
        public LinearAxesModel()
        { 
        }
    }
}
