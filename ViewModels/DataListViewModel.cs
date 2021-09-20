using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using WpfDesktopApplicationv2.Stores;

namespace WpfDesktopApplicationv2.ViewModels
{
    class DataListViewModel:INotifyPropertyChanged
    {
        private MeasurementViewModel _userPick;
        public MeasurementViewModel UserPick
        {
            get { return _userPick; }
            set 
            {
                _userPick = value;
                OnPropertyChanged("UserPick");
                OnUserPicked(value.Name);
            }
        }


        private ObservableCollection<MeasurementViewModel> _measurementsListView; // unnecessary???
        public ObservableCollection<MeasurementViewModel> DataListMeasurements
        {
            get { return _measurementsListView; }
            set
            { 
                _measurementsListView = value;
                OnPropertyChanged("DataListMeasurements");
            }
        }

        // fields
        private BroadcastMeasurementsList _publisher;

        /// <summary>
        /// Constructor initializes a mediator and assigns event handler that will udpate a list view collection 
        /// </summary>
        /// <param name="collection"></param>
        public DataListViewModel(BroadcastMeasurementsList collection)
        {
            _measurementsListView = new ObservableCollection<MeasurementViewModel>();
            _publisher = collection;
            _publisher.CollectionBroadcasted += UpdateList;
        }

        /// <summary>
        /// Handler for list view collection update
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="newMeasurements">New updated measurements</param>
        private void UpdateList(object sender, List<MeasurementViewModel> newMeasurements)
        {
            DataListMeasurements = new ObservableCollection<MeasurementViewModel>(newMeasurements);
        }

        /// <summary>
        /// Event handler that will broadcast a identifier of a measurement that user picked.
        /// </summary>
        public event EventHandler<string> UserPicked;
        private void OnUserPicked(string identifier)
        {
            UserPicked?.Invoke(this, identifier);
        }

        public event PropertyChangedEventHandler PropertyChanged;
        private void OnPropertyChanged(string propName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propName));
        }
    }
}
