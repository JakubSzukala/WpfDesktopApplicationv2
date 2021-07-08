using System;
using System.Collections.Generic;
using System.Text;
using WpfDesktopApplicationv2.Stores;

namespace WpfDesktopApplicationv2.ViewModels
{
    public class SliderViewModel
    {
        private SliderValueChangedStore _store;

        private int _sliderValue;

        public int SliderValue
        {
            get { return _sliderValue; }
            set 
            {
                _sliderValue = value;
                _store.CurrentSliderValue = value;
            }
        }

        public SliderViewModel(SliderValueChangedStore store)
        {
            _store = store;
        }
    }
}
