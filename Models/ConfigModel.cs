using System;
using System.Collections.Generic;
using System.Text;

namespace WpfDesktopApplicationv2.Models
{
    public class ConfigModel
    {
        private float _samplingTime;

        public float SamplingTime
        {
            get => _samplingTime;
            set => _samplingTime = value;
        }

        private string _ipAddress;

        public string IpAddress
        {
            get => _ipAddress;
            set => _ipAddress = value;
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
            IpAddress = ip;
            MaxPoints = max;
        }


    }
}
