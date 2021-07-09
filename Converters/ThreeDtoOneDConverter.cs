using System;
using System.Collections.Generic;
using System.Text;
using WpfDesktopApplicationv2.Models;

namespace WpfDesktopApplicationv2.Converters
{
    public class ThreeDtoOneDConverter
    {
        public List<MeasurementModel> Convert(Measurement3dModel xyz)
        {
            List<MeasurementModel> measurements = new List<MeasurementModel>();
            foreach(var name in xyz.Data.Keys)
            {
                var temp = new MeasurementModel();
                temp.Name = name +":"+xyz.Name;           // assign name of measure
                temp.Data = xyz.Data[name]; // assign a value of measure
                temp.Unit = xyz.Unit;       // assign unit 
                measurements.Add(temp);
                            }
            return measurements;
        }
    }
}
