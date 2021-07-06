using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Text;
using WpfDesktopApplicationv2.Models;

namespace WpfDesktopApplicationv2.ViewModels
{
    class MeasurementViewModel : INotifyPropertyChanged
    {
        private string _Name;

        public string Name
        {
            get { return _Name; }
            set
            {
                _Name = value;
                OnPropertyChanged("Name");
            }
        }

        private string _Data;

        public string Data
        {
            get { return _Data; }
            set
            {
                _Data = value;
                OnPropertyChanged("Data");
            }
        }

        private string _Unit;

        public string Unit
        {
            get { return _Unit; }
            set
            {
                _Unit = value;
                OnPropertyChanged("Unit");
            }
        }


        public MeasurementViewModel(MeasurementModel model)
        {
            UpdateDataFromModel(model);
        }

        public MeasurementViewModel()
        {
            Name = "";
            Data = "";
            Unit = "";
        }

        /// <summary>
        /// Function that updates an object using data received from a server in form of model.
        /// </summary>
        /// <param name="model">Formatted data from a server.</param>
        public void UpdateDataFromModel(MeasurementModel model)
        {
            Name = model.Name;
            Data = model.Data;
            Unit = model.Unit;
        }

        public event PropertyChangedEventHandler PropertyChanged;

        protected void OnPropertyChanged(string propName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propName));
        }
    }
}
