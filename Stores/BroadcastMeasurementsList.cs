using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;
using WpfDesktopApplicationv2.ViewModels;

namespace WpfDesktopApplicationv2.Stores
{
    public class BroadcastMeasurementsList
    {
        public event EventHandler<List<MeasurementViewModel>> CollectionBroadcasted;

        public void OnCollectionBroadcasted(List<MeasurementViewModel> collection)
        {
            CollectionBroadcasted?.Invoke(this, collection);
        }
    }
}
