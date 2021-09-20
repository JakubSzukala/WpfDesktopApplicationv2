using System;
using System.Collections.Generic;
using System.Text;

namespace WpfDesktopApplicationv2.Stores
{
    /// <summary>
    /// Class storing a current value of slider, created to decouple viewmodels.
    /// </summary>
    public class SliderValueChangedStore
    {
        // event that will be raised when value of slider is changed
        public event Action SliderValueChanged;

        // value of slider will be assigned by object containing this store class
        private int _currentSliderValue;
        public int CurrentSliderValue
        {
            get => _currentSliderValue;
            set
            {
                _currentSliderValue = value;
                OnSliderValueChanged();
            }
        }

        private void OnSliderValueChanged()
        {
            SliderValueChanged?.Invoke();
        }
    }
}
