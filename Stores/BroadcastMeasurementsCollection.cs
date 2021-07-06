using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;
using WpfDesktopApplicationv2.ViewModels;

namespace WpfDesktopApplicationv2.Stores
{
    public class BroadcastMeasurementsCollection
    {
        public event EventHandler<ObservableCollection<MeasurementViewModel>> CollectionBroadcasted;

        public void OnCollectionBroadcasted(ObservableCollection<MeasurementViewModel> collection)
        {
            CollectionBroadcasted?.Invoke(this, collection);
        }
    }
}
