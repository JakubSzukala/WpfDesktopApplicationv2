using OxyPlot;
using System;
using System.Collections.Generic;
using System.Text;

namespace WpfDesktopApplicationv2.Stores
{
    public class BroadcastPointsStore
    {
        public event EventHandler<Dictionary<string, DataPoint>> PointsBroadcasted;

        public void OnPointsBroadcasted(Dictionary<string, DataPoint> dictionary)
        {
            PointsBroadcasted?.Invoke(this, dictionary);
        }
    }
}
