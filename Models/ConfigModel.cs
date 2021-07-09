using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Text;

namespace WpfDesktopApplicationv2.Models
{
    public class ConfigModel : INotifyPropertyChanged
    {
        private float _samplingTime;

        public float SamplingTime
        {
            get => _samplingTime;
            set
            {
                _samplingTime = value;
                OnPropertyChanged(nameof(SamplingTime));
            }
        }

        private string _ipAddress;

        public string IpAddress
        {
            get => _ipAddress;
            set
            {
                _ipAddress = value;
                OnPropertyChanged(nameof(IpAddress));
            }
        }

        private int _maxPoints;

        public int MaxPoints
        {
            get => _maxPoints;
            set => _maxPoints = value;
        }


        public ConfigModel(float sT, string ip, int max)
        {
            SamplingTime = sT;
            IpAddress = ip; OnPropertyChanged("IpAddress");
            MaxPoints = max;
        }
        public event PropertyChangedEventHandler PropertyChanged;
        public void OnPropertyChanged(string propName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propName));
        }

    }
}
